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
  truba.project
  (:use [truba.build.finalizer :only [finalizer]]
        [truba.build.group :only [build group]]))

(defn quote-args [as]
  (map #(list 'quote %) as))

(defn process-references [[n & args]]
  `(~(symbol "clojure.core" (clojure.core/name n)) ~@(quote-args args)))

(defn split-ns-specs [xs]
  (split-with
    (fn [x]
      (and (seq? x) (keyword? (first x))))
    xs))

; XXX write this one
(defn project-imports [])

; XXX Use switch ns
(defmacro project* [& xs]
  `(let [ns# (create-ns (gensym "project__"))]
     (binding [*ns* ns#]
       (build
         (project-imports)
         ~@xs
         (finalizer
           (remove-ns ns#))))))

(defmacro project [id & [f & r :as xs]]
  (let [; Extract docstring (ignored for now).
        [m xs] (if (string? f)
                 [f r]
                 [nil xs])

        ; Extract ns like specs
        [ns-specs xs] (split-ns-specs xs)]
    `(project* ~id ~@(map process-references ns-specs) ~@xs)))

; XXX write this one
(defn module-imports [])

; XXX Use switch-ns
(defmacro module* [& xs]
  `(let [ns# (create-ns (gensym "module__"))]
     (binding [*ns* ns#]
       (group
         (module-imports)
         ~@xs
         (finalizer
           (remove-ns ns#))))))

(defmacro module [id & [f & r :as xs]]
  (let [; Extract docstring (unused for now).
        [m xs] (if (string? f)
                 [f r]
                 [nil xs])

        ; Extract ns like specs
        [ns-specs xs] (split-ns-specs xs)]
    `(module* ~id ~@(map process-references ns-specs)) ~@xs))
