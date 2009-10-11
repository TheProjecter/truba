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
  truba.graph.test
  (:require [truba.graph :as g])
  (:use truba.unittest))

(def graph-data
  {:setup
    (fn []
      {:a #{:b :c :d} :b #{} :c #{:e :f} :d #{:c} :e #{} :f {}})})

(deftest tsort [graph graph-data]
  (is
    (=
      '(:f :e :c :b :d :a) (g/tsort graph))))

(deftest psort [graph graph-data]
  (is
    (= '((:a) (:d) (:c) (:f :e :b)) (g/psort graph))))
