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
        [truba.build.command :only [command]]
        [truba.build.generator :only [generator]]
        [truba.build.group :only [build group]]
        [truba.build.finalizer :only [finalizer]]))

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

(def generators
  {:setup
    (fn []
      [(generator g1 [type id] (:match false) (:each nil))
       (generator g2 [type id] (:match false) (:each nil))])})

(def builds
  {:setup
    (fn []
      [(build b1)
       (build b2)])})

(def groups
  {:setup
    (fn []
      [(group gr1)
       (group gr2)])})

(def finalizers
  {:setup
    (fn []
      [(finalizer 42)
       (finalizer 24)])})

(deftest add-property [[p1 p2] properties]
  (is
    (thrown?
      (c/add-property {:properties (into {} [p1])} p1))))

(deftest add-command [[c1 c2] commands]
  (is
    (thrown? (c/add-command {:commands [c1]} c1))))

(deftest add-generator [[g1 g2] generators]
  (is
    (thrown?
      (-> {}
        (c/add-generator g1)
        (c/add-generator g1)))))

(deftest add-build [[b1 b2] builds]
  (is
    (thrown?
      (-> {}
        (c/add-group b1)
        (c/add-group b1)))))

(deftest add-finalizer [[f1 f2] finalizers]
  (let [ffn (:finalizers
              (-> {}
                (c/add-finalizer f1)
                (c/add-finalizer f2)))]
    (is (not (nil? ffn)))))

(deftest file-collector
 [[c1 c2] commands
  [p1 p2] properties
  [g1 g2] generators
  [b1 b2] builds
  [gr1 _] groups
  [f1 f2] finalizers]

  (are= [a b] a b
    {:commands [c1 c2]}
    (-> {}
      (c/file-collector c1) (c/file-collector c2))

    {:properties (into {} [p1 p2])}
    (-> {}
      (c/file-collector p1) (c/file-collector p2))

    {:generators (into {} [g1 g2])}
    (-> {}
      (c/file-collector g1) (c/file-collector g2)))

  (is
    (thrown?
      (-> {}
        (c/file-collector b1)
        (c/file-collector b2))))

  (is
    (thrown? (c/file-collector {} gr1)))

  (is
    (not (nil? (:finalizers
                 (-> {}
                   (c/file-collector f1)
                   (c/file-collector f2)))))))

(deftest build-collector
  [[c1 c2] commands
   [p1 p2] properties
   [g1 g2] generators
   [gr1 gr2] groups
   [b1 b2] builds
   [f1 f2] finalizers]

  (are= [a b] a b
    {:commands [c1 c2]}
    (-> {}
      (c/build-collector c1) (c/build-collector c2))

    {:properties (into {} [p1 p2])}
    (-> {}
      (c/build-collector p1) (c/build-collector p2))

    {:generators (into {} [g1 g2])}
    (-> {}
      (c/build-collector g1) (c/build-collector g2))

    {:groups (into {} [gr1 gr2])}
    (-> {}
      (c/build-collector gr1) (c/build-collector gr2)))

  (is
    (thrown? (c/build-collector {} b1)))

  (is
    (fn? (:finalizers
           (-> {}
             (c/build-collector f1)
             (c/build-collector f2))))))

(deftest group-collector
  [[c1 c2] commands
   [p1 p2] properties
   [g1 g2] generators
   [gr1 gr2] groups
   [b1 b2] builds
   [f1 f2] finalizers]

  (is
    (thrown?
      (-> {}
        (c/group-collector c1)
        (c/group-collector c2))))

  (is
    (= {:properties (into {} [p1 p2])}
       (-> {}
         (c/group-collector p1)
         (c/group-collector p2))))

  (is
    (= {:generators (into {} [g1 g2])}
       (-> {}
         (c/group-collector g1)
         (c/group-collector g2))))

  (is
    (= {:groups (into {} [gr1 gr2])}
       (-> {}
         (c/group-collector gr1)
         (c/group-collector gr2))))

  (is
    (thrown? (c/group-collector {} b1)))

  (is
    (fn? (:finalizers
           (-> {}
             (c/group-collector f1)
             (c/group-collector f2))))))

(deftest build-fragment-collector
  [[c1 c2] commands
   [p1 p2] properties
   [g1 g2] generators
   [gr1 gr2] groups
   [b1 b2] builds
   [f1 f2] finalizers]

  (is
    (= {:properties (into {} [p1 p2])}
       (-> {}
         (c/build-fragment-collector p1)
         (c/build-fragment-collector p2))))

  (is
    (= {:commands [c1 c2]}
       (-> {}
         (c/build-fragment-collector c1)
         (c/build-fragment-collector c2))))

  (is
    (= {:generators (into {} [g1 g2])}
       (-> {}
         (c/build-fragment-collector g1)
         (c/build-fragment-collector g2))))

  (is
    (thrown? (c/build-fragment-collector {} gr1)))

  (is
    (thrown? (c/build-fragment-collector {} b1)))

  (is
    (thrown?
      (-> {}
        (c/build-fragment-collector f1)
        (c/build-fragment-collector f2)))))

(deftest group-fragment-collector
  [[c1 c2] commands
   [p1 p2] properties
   [g1 g2] generators
   [gr1 gr2] groups
   [b1 b2] builds
   [f1 f2] finalizers]

  (is
    (thrown?
      (-> {}
        (c/group-fragment-collector c1)
        (c/group-fragment-collector c2))))

  (is
    (= {:properties (into {} [p1 p2])}
       (-> {}
         (c/group-fragment-collector p1)
         (c/group-fragment-collector p2))))

  (is
    (= {:generators (into {} [g1 g2])}
       (-> {}
         (c/group-fragment-collector g1)
         (c/group-fragment-collector g2))))

  (is
    (thrown? (c/group-fragment-collector {} gr1)))

  (is
    (thrown? (c/group-fragment-collector {} b1)))

  (is
    (thrown?
      (-> {}
        (c/group-fragment-collector f1)
        (c/group-fragment-collector f2)))))

(deftest property-generator-collector
  [[c1 c2] commands
   [p1 p2] properties
   [g1 g2] generators
   [gr1 gr2] groups
   [b1 b2] builds
   [f1 f2] finalizers]

  (is
    (thrown?
      (-> {}
        (c/property-generator-collector c1)
        (c/property-generator-collector c2))))

  (is
    (= {:properties (into {} [p1 p2])}
       (-> {}
         (c/property-generator-collector p1)
         (c/property-generator-collector p2))))

  (is
    (= {:generators (into {} [g1 g2])}
       (-> {}
         (c/property-generator-collector g1)
         (c/property-generator-collector g2))))

  (is
    (thrown? (c/property-generator-collector {} gr1)))

  (is
    (thrown? (c/property-generator-collector {} b1)))

  (is
    (thrown?
      (-> {}
        (c/property-generator-collector f1)
        (c/property-generator-collector f2)))))

(deftest task-generator-collector
  [[c1 c2] commands
   [p1 p2] properties
   [g1 g2] generators
   [gr1 gr2] groups
   [b1 b2] builds
   [f1 f2] finalizers]

  (is
    (thrown?
      (-> {}
        (c/task-generator-collector c1)
        (c/task-generator-collector c2))))

  (is
    (= {:properties (into {} [p1 p2])}
       (-> {}
         (c/task-generator-collector p1)
         (c/task-generator-collector p2))))

  (is
    (= {:generators (into {} [g1 g2])}
       (-> {}
         (c/task-generator-collector g1)
         (c/task-generator-collector g2))))

  (is
    (thrown? (c/task-generator-collector {} gr1)))

  (is
    (thrown? (c/task-generator-collector {} b1)))

  (is
    (thrown?
      (-> {}
        (c/task-generator-collector f1)
        (c/task-generator-collector f2)))))
