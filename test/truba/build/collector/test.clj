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
        [truba.build.command :only [command]]))

(def commands
  {:setup
    (fn []
      [(command c1 [build & _] nil)
       (command c2 [build & _] nil)])})

(deftest add-command [[c1 c2] commands]
  (is
    (thrown? (c/add-command {:commands [c1]} c1))))

(deftest file-collector [[c1 c2] commands]
  (is
    (= {:commands [c1 c2]}
       (-> {}
         (c/file-collector c1)
         (c/file-collector c2)))))

(deftest build-collector [[c1 c2] commands]
  (is
    (= {:commands [c1 c2]}
       (-> {}
         (c/build-collector c1)
         (c/build-collector c2)))))

(deftest group-collector [[c1 c2] commands]
  (is
    (thrown?
      (-> {}
        (c/group-collector c1)
        (c/group-collector c2)))))

(deftest fragment-collector [[c1 c2] commands]
  (is
    (thrown?
      (-> {}
        (c/fragment-collector c1)
        (c/fragment-collector c2)))))

(deftest generator-collector [[c1 c2] commands]
  (is
    (thrown?
      (-> {}
        (c/generator-collector c1)
        (c/generator-collector c2)))))
