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
  truba.build.id.test
  (:require [truba.build.id :as id])
  (:use truba.unittest))

(deftest basic-expand-id
  (are [a b] (= a (id/expand-id* b))
    {:name 'a}  'a
    {:type :b}  :b
    {:x 1 :y 2} {:x 1 :y 2})
  (is
    (thrown? (id/expand-id 42))))

(deftest expand-id
  (is
    (contains? (id/expand-id :a) :uid))
  (is
    (not
      (contains? (id/expand-id 'a) :uid))))

