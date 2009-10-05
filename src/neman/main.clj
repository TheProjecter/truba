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
        [neman.cli.desc :only [convert-specs]]))

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
  (if (or (empty? f) (.startsWith f "-")) [:default args] [f r]))

(defn as-name [x]
  (if (symbol? x) (name x) x))

(defn block-seq [xs]
  (map (fn [[n & xs]] [(as-name n) (split-prelude xs)]) xs))

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

; Command variant without command line options.
(defmacro command-1 [_ [bindings & body]]
  `(hash-map
     :desc (fn [])
     :body (fn [& ~bindings]
             ~@body)))

; Command variant with command line options.
(defmacro command-2 [specs [bindings & body]]
  `(hash-map
     :desc (fn [] ~specs)
     :body (fn [& args#]
             (let [specs#    (convert-specs ~specs)
                   ~bindings (parse args# (:options specs#))]
               ~@body))))

(defmacro command* [name specs xs]
  `(vector
     (as-name '~name)
     ~(if (empty? specs)
        `(command-1 ~specs ~xs) `(command-2 ~specs ~xs))))

(defmacro command [name & xs]
  (let [[specs xs] (split-prelude xs)]
    `(command* ~name ~specs ~xs)))

(defn extract-from-blocks [name blocks]
  (reduce
    (fn [res [f & _ :as block]]
      (update-in res [(if (= f name) 0 1)] #(conj % block)))
    [[] []]
    blocks))

(defn extract-extras [blocks]
  (extract-from-blocks :extra blocks))

(defn extract-extras [blocks]
  (let [[extras blocks] (extract-from-blocks :extra blocks)]
    ; Strip :extra from block
    [(map (fn [[_ extra]] extra) extras) blocks]))

(defn extract-unknown [blocks]
  ; Strip :unknown and specs parts from block
  (let [[[[_ [_ unknown]] & _] blocks] (extract-from-blocks :unknown blocks)]
    [unknown blocks]))

(defmacro defmain [& [f s & r :as xs]]
  (let [; Collect metadata and doc string
        [m xs] (split-meta xs)

        ; Split main prelude and body
        [specs xs] (split-prelude xs)

        ; Wrap single body definition inside :default block and copy
        ; specs to this block
        xs (if (seq? (first xs))
             xs
             (list (concat [:default] (concat (apply concat specs) xs))))

        ; Get all subcommand blocks.
        blocks (block-seq xs)

        ; Extract all blocks provide extra commands at runtime.
        [extras blocks] (extract-extras blocks)

        ; Extract handler for unknown commands.
        [unknown blocks] (extract-unknown blocks)
        unknown (if unknown
                  `(fn ~@unknown)
                  `(fn [cmd# _#]
                     (println "Unknown command:" cmd#)))

        ; Convert blocks to commands (including the :default block).
        commands (vec
                   (map (fn [[f s]] `(command* ~f ~@s)) blocks))]
    `(do
       ; Define entry point function
       (main-fn [& args#]
         (let [[cmd# args#] (split-command args#)]
           (if-let [cfn# (get-in (into {} ~commands) [cmd# :body])]
             (apply cfn# args#)
             (~unknown cmd# args#))))

       ; Generate class
       (gen-class :name ~(ns-name *ns*) :main true)

       (launch-from-source))))
