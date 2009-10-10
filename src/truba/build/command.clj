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
  truba.build.command
  (:require neman.main)
  (:use [neman.cli :only [parse]]
        [neman.cli.desc :only [convert-specs]]
        [neman.main :only [split-prelude as-name]]
        [truba.build :only [*collector*]]))

(defmacro command-1 [specs [[build & bindings] & body]]
  `(hash-map
     :desc (fn [])
     :body (fn [~build & ~(vec bindings)] ~@body)))

(defmacro command-2 [specs [[build & bindings] & body]]
  `(hash-map
     :desc (fn [] ~specs)
     :body (fn [~build & args#]
             (let [specs# (convert-specs ~specs)
                   ~(vec bindings) (parse args# (:options specs#))]
               ~@body))))

(defmacro command* [name specs xs]
  `(vector
     (as-name '~name)
     ~(if (empty? specs)
        `(command-1 ~specs ~xs)
        `(command-2 ~specs ~xs))))

(defmacro command [name & xs]
  (let [[specs xs] (split-prelude xs)]
    `(*collector*
       (with-meta
         (command* ~name ~specs ~xs) {:type :Command}))))
