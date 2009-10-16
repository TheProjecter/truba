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

(deftest basic-expand-id
  (are [a b] (= a (p/expand-id* b))
    {:name 'a}  'a
    {:type :b}  :b
    {:x 1 :y 2} {:x 1 :y 2})
  (is
    (thrown? (p/expand-id 42))))

(deftest expand-id
  (is
    (contains? (p/expand-id :a) :uid))
  (is
    (not
      (contains? (p/expand-id 'a) :uid))))

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

(deftest calc-all
  (let [pm (into {}
             [(property p1 42)
              (property p2 [p1] (inc p1))
              (property p3 [p1 p2] (+ p1 p2))])]
    (is
      (= {{:name 'p1} 42
          {:name 'p2} 43
          {:name 'p3} 85}
         (p/calc-all pm)))))
