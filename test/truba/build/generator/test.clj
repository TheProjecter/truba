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
  truba.build.generator.test
  (:require [truba.build.generator :as g])
  (:use truba.unittest))

(deftest expand-unknown
  (is
    (thrown? (g/expand-key [] [] [] '(:unknown (println "hi"))))))

(deftest unmatched-properties
  (let [g (g/generator wee [a] [type id]
             (:match
                true)
             (:each
                nil))]
    (is
      (thrown? ((:match (second g)) {})))
    (is
      (thrown? ((:each (second g)) {})))))

(deftest match-task-id
  (let [g (g/generator g [type id]
             (:match
               (and (= :Task type) (= :X (:type id))))
             (:each
                nil))
        body (second g)
        mfn  ((:match body) {})]
    (is
      (true? (mfn [:Task {:type :X}])))))

(deftest match-property-id
  (let [g (g/generator g [type id]
             (:match
               (and (= :Property type) (= :X (:type id))))
             (:each
                nil))
        body (second g)
        mfn  ((:match body) {})]
    (is
      (true? (mfn [:Property {:type :X}])))))

; XXX Test that matched task/property is generated
