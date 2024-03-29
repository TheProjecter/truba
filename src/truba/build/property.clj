;; Copyright (c) Krešimir Šojat, 2009. All rights reserved. The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution. By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license. You must not
;; remove this notice, or any other, from this software.

(ns
  #^{:author "Krešimir Šojat"
     :license {:name "Eclipse Public License 1.0"
               :url  "http://opensource.org/licenses/eclipse-1.0.php"}}
  truba.build.property
  (:use neman.ex
        [truba.graph :only [tsort]]
        [truba.build :only [*collector*]]
        [truba.build.id :only [expand-id* expand-id]]
        [truba.build.selector :only [selector]]))

(declare *properties*)

(defn set-type [p]
  (with-meta p {:type :Property}))

(defn create-dep [d]
  (-> d
    expand-id* (assoc :leaf nil) (assoc :dynamic nil)))

(defn create-property [id deps expr]
  (set-type
    [(expand-id id) {:deps (vec (map create-dep deps)) :expr expr}]))

(defmacro property*
  ([id deps bindings expr]
   `(*collector*
      (create-property '~id '~deps (fn ~bindings ~expr))))

  ([id deps expr]
   `(*collector*
      (create-property '~id '~deps (fn ~deps ~expr))))

  ([id expr]
   `(*collector*
      (create-property '~id [] (fn [] ~expr)))))

(defmacro property
  "Define new property local to current build group."
  {:arglists '([id doc-string? deps? bindings? expr])}
  [id & [f & _ :as xs]]
  (let [; Extract description string (unused for now).
        [doc & xs] (if (string? f)
                     xs
                     (conj xs nil))]
    `(property* ~id ~@xs)))

(defmacro properties
  "Define multiple named properties."
  [& [f s & r :as xs]]
  (let [; Extract global dependencies, bindings and property
        ; expression pairs
        [[gd gb] xs] (cond
                       (and (vector? f) (vector? s))
                         [[f s] r]
                       (vector? f)
                         [[f f] (next xs)]
                       :else
                         [[] xs])

        ; Every defined property must depend on all previous defined
        ; properties in this 'properties' block.
        ps (reduce
             (fn [ps [k v]]
               (conj ps
                 [k (vec (map first ps)) v]))
             []
             (partition 2 xs))

        ; Every defined property must depend on global dependencise
        ; for this 'properties' block.
        ps (map
             (fn [[f s t]]
               [f (vec (concat s gd)) (vec (concat s gb)) t])
             ps)]
    `(do
       ~@(for [p ps] `(property ~@p)))))

(defn resolve-1 [p pk]
  (seq
    ((selector p) pk)))

(defn resolve-all [pm]
  (let [pk (set (keys pm))]
    (map
      (fn [[k v]]
        [k
         (assoc v :deps
           (map #(resolve-1 % pk) (:deps v)))])
      pm)))

(defn get-p [r pm]
  (let [[f & _ :as xs] (resolve-1
                         (expand-id* r) (set (keys pm)))]
    (if (:uid f)
      (map pm xs) (pm f))))

(defmacro with-properties* [pm & [f s & r :as xs]]
  (let [[deps bindings body] (cond
                               (and (vector? f) (vector? s))
                                 [f s r]
                                (vector? f)
                                 [f f (next xs)]
                                :else
                                 [[] [] xs])]
    (let [gx (gensym)]
      `(let [~gx ~pm]
         (binding [*properties* ~gx]
           (let ~(vec (interleave bindings
                                  (map (fn [d] `(get-p '~d ~gx)) deps)))
             ~@body))))))

(defmacro with-properties
  {:arglists '([props-map? deps bindings? & body])}
  [& [f & _ :as xs]]
  (if (vector? f)
    `(with-properties* *properties* ~@xs)
    `(with-properties* ~@xs)))

(defn calc-1 [{:keys [expr deps]} pm]
  (apply expr
    (map
      (fn [[f & _ :as ps]]
        (if (not (:uid f)) (pm f) (map pm ps)))
      deps)))

(defn calc-graph [pm]
  (into {}
    (map
      (fn [[k {deps :deps}]]
        [k (set (apply concat deps))])
      pm)))

(defn calc-all [pm]
  (let [pm (into (empty pm) (resolve-all pm))]
    (reduce
      (fn [m p]
        (assoc m p (calc-1 (pm p) m)))
      {}
      (-> pm calc-graph tsort))))

(comment

  ; Simple property declaration
  (property a 42)

  ; Property with named dependencies (no need to provide both deps and bindings)
  (property b [a]
    (+ a 1))

  ; Property with docstring.
  (property src-dir
    "Directory with source files."
    [:BaseDir] [base-dir]
    (java.io.File. base-dir "src"))

  ; Property with complex dependencies.
  (property b [:BaseDir src-dir] [base-dir src-dir]
    (java.io.File. base-dir "src"))

  ; Example properties usage without external dependencies
  (properties
    a 1
    b (+ a 1))

  ; Example properties usage with external dependencies
  (properties [:BaseDir] [base-dir]
    main-dir (java.io.File. base-dir "main")
    src-dir  (java.io.File. main-dir "src"))

) ; end of comment
