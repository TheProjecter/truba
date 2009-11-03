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
  truba.build.group.test
  (:require [truba.build.group :as g])
  (:use truba.unittest))

(deftest empty-build
  (let [[f s] (g/build foo)
        body  (s f)
        pm    (:properties body)]
    (is
      (= 1 (count pm)))
    (is
      (= :GroupName (:type (ffirst pm))))))

(deftest build-with-child-groups
  (let [[f s] (g/build foo
                (g/group bar)
                (g/group baz))
        body  (s f)]
    (is= 3 (count (:properties body)))))

(deftest empty-group
  (let [[f s] (g/group foo)
        body  (s f)
        pm    (:properties body)]
    (is
      (= 1 (count pm)))
    (is
      (= :GroupName (:type (ffirst pm))))))

(deftest group-with-child-groups
  (let [[f s] (g/group foo
                (g/group bar)
                (g/group baz))
        body  (s f)]
    (is= 3 (count (:properties body)))))
