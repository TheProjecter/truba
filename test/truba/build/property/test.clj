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
  truba.build.property.test
  (:require [truba.build.property :as p])
  (:use truba.unittest))

(deftest resolve-dependencies
  (let [p1 (p/property p1 1)
        p2 (p/property p2 [p1] 2)
        p3 (p/property p3 [p1 p2] 3)
        pm (into {} [p1 p2 p3])
        pk (set (keys pm))]
    (are [a b] (= a (map #(p/resolve-1 % pk) (:deps (second b))))
      '() p1
      '(({:name p1})) p2
      '(({:name p1}) ({:name p2})) p3)))

(deftest resolve-soft-dependencies
  (let [p1 (p/property :A 1)
        p2 (p/property :A 2)
        p3 (p/property P1 3)
        p4 (p/property P2 [:A P1] [as p1]
             (apply + p1 as))
        pm (into {}
             [p1 p2 p3 p4])]
    (is
      (= 6 (get (p/calc-all pm) {:name 'P2})))))

(deftest calc-all
  (let [pm (into {}
             [(p/property p1 42)
              (p/property p2 [p1] (inc p1))
              (p/property p3 [p1 p2] (+ p1 p2))])]
    (is
      (= {{:name 'p1} 42
          {:name 'p2} 43
          {:name 'p3} 85}
         (p/calc-all pm)))))
