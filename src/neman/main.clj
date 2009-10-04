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
  neman.main
  (:use [neman.cli :only [parse usage-str help-str]]
        [neman.cli.desc :only [convert-specs]])
  (:load "main_extra" "main_default"))

(defn split-prelude [xs]
  (loop [[f s & r :as xs] xs opts {}]
    (when (seq xs)
      (if (keyword? f)
        (recur r (assoc opts f s)) [opts xs]))))

(defn split-meta [[f s & r :as xs]]
  (cond
    (and (string? f) (map? s))
      [(assoc s :doc f) r]

    (string? f)
      [{:doc f} (next xs)]

    (map? f)
      [f (next xs)]

    :else
      [{} xs]))

(defn split-command [[f & r :as args]]
  (if (or (empty? f) (.startsWith f "-")) ["" args] [f r]))

(defn as-name [x]
  (if (symbol? x) (name x) x))

(defn block-seq [xs]
  (println xs)
  (map (fn [[n & xs]] [(as-name n) (split-prelude xs)]) xs))

(defn block-map [xs]
  (reduce
    (fn [m [k v]]
      (update-in m [k]
        (if (= k :extra) conj (fn [_ v] v)) v))
    {}
    xs))

(defn dissoc-get [map key]
  [(get map key) (dissoc map key)])

(defmacro main-fn [bindings & body]
  `(let [main-ns# *ns*]
     (defn ~'-main [& ~bindings] (binding [*ns* main-ns#] ~@body))))

(defn get-property [p]
  (java.lang.System/getProperty p))

(defmacro current-ns? [n]
  `(= (str (ns-name ~'*ns*)) ~n))

(defmacro launch? []
  `(and (not *compile-files*) (current-ns? (get-property "_main"))))

(defmacro launch-from-source []
  `(when (launch?)
     (apply ~'-main *command-line-args*)))

(defn import-commands
  "Import all commands defined using unquote or unquote-splicing syntax."
  [xs]
  (let [uq?  #(= 'clojure.core/unquote %)
        uqs? #(= 'clojure.core/unquote-splicing %)]
    (mapcat
      (fn [[f s & _ :as x]]
        (cond
          (and (uq? f) (symbol? s))
            (list
              (var-get (resolve s)))

          (and (uq? f) (seq? s))
            (map (fn [x] (var-get (resolve x))) s)

          (and (uqs? f) (symbol? s))
            (deref (var-get (resolve s)))

          :else
            x))
        xs)))

(defmacro command* [specs [bingings & body]])

(defmacro command [name & xs]
  (let [[specs xs] (split-prelude xs)]
    `(command* ~specs ~xs)))

(defmacro defmain [& [f s & r :as xs]]
  (let [; Collect metadata and doc string
        z (println "hi")
        [m xs] (split-meta xs)
        z (println "hi again")
        ; Split main prelude and body
        [specs xs] (split-prelude xs)
        z (println "hi 3")

        ; Wrap single body definition inside :default block and copy
        ; specs to this block
        xs (if (seq? (first xs))
             xs
             (list (concat [:default] (concat (apply concat specs) xs))))
        z (println "hi 4")

        ; Get all subcommand blocks.
        ;z (println (-> xs import-commands))
        ;z (println "====================")
        ;z (println (-> xs import-commands block-seq))
        blocks (-> xs
                 import-commands block-seq block-map)
        z (println "hi 5")

        [extras blocks] (dissoc-get blocks :extra)
        z (println "hi")
        ;extras (map
        ;         (fn [x]
        ;           `(create-extra ~x))
        ;         extras)

        ;[default blocks] (dissoc-get blocks :default)
        ];default `(create-default ~@default)]
    `(do
       ; Define entry point function
       #_(main-fn [& args#]
         (let [[command# args#] (split-command args#)]
           (apply ~default args#)))

       ; Generate class
       (gen-class :name ~(ns-name *ns*) :main true)

       (launch-from-source))))
