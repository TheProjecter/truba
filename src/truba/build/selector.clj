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
  truba.build.selector
  (:require [clojure.set :as set])
  (:use neman.ex))

(defn select [key val xs]
  (set/select (fn [x] (= val (get x key))) xs))

(defmulti select-by
  (fn [key val] key) :default ::default)

(defmacro allow-empty [key empty?]
  `(defmethod select-by ~key [k# v#]
     [~empty? (fn [xs#] (select k# v# xs#))]))

(allow-empty ::default true)
(allow-empty :name false)

(defn selector* [xm]
  (reduce
    (fn [[e1 sfn1] [k v]]
      (let [[e2 sfn2] (select-by k v)]
        [(and e1 e2) (comp sfn1 sfn2)]))
    [true identity]
    xm))

(defn selector [xm]
  (let [[empty? sfn] (selector* xm)]
    (if empty?
      sfn
      (fn [xs]
        (let [res (sfn xs)]
          (if (seq res)
            res
            (throwf "Can't find dependency: %s" xm)))))))
