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
  truba.build.collector.test
  (:require [truba.build.collector :as c])
  (:use truba.unittest
        [truba.build.property :only [property]]
        [truba.build.command :only [command]]))

(def properties
  {:setup
    (fn []
      [(property p1 1)
       (property p2 2)])})

(def commands
  {:setup
    (fn []
      [(command c1 [build & _] nil)
       (command c2 [build & _] nil)])})

(deftest add-property [[p1 p2] properties]
  (is
    (thrown?
      (c/add-property {:properties (into {} [p1])} p1))))

(deftest add-command [[c1 c2] commands]
  (is
    (thrown? (c/add-command {:commands [c1]} c1))))

(deftest file-collector
 [[c1 c2] commands
  [p1 p2] properties]

  (are= [a b] a b
    {:commands [c1 c2]}
    (-> {}
      (c/file-collector c1) (c/file-collector c2))

    {:properties (into {} [p1 p2])}
    (-> {}
      (c/file-collector p1) (c/file-collector p2))))

(deftest build-collector
  [[c1 c2] commands
   [p1 p2] properties]

  (are= [a b] a b
    {:commands [c1 c2]}
    (-> {}
      (c/build-collector c1) (c/build-collector c2))

    {:properties (into {} [p1 p2])}
    (-> {}
      (c/build-collector p1) (c/build-collector p2))))

(deftest group-collector
  [[c1 c2] commands
   [p1 p2] properties]

  (is
    (thrown?
      (-> {}
        (c/group-collector c1)
        (c/group-collector c2))))

  (is
    (= {:properties (into {} [p1 p2])}
       (-> {}
         (c/group-collector p1)
         (c/group-collector p2)))))

(deftest fragment-collector
  [[c1 c2] commands
   [p1 p2] properties]

  (is
    (thrown?
      (-> {}
        (c/fragment-collector c1)
        (c/fragment-collector c2))))

  (is
    (= {:properties (into {} [p1 p2])}
       (-> {}
         (c/fragment-collector p1)
         (c/fragment-collector p2)))))

(deftest generator-collector
  [[c1 c2] commands
   [p1 p2] properties]

  (is
    (thrown?
      (-> {}
        (c/generator-collector c1)
        (c/generator-collector c2))))

  (is
    (= {:properties (into {} [p1 p2])}
       (-> {}
         (c/generator-collector p1)
         (c/generator-collector p2)))))
