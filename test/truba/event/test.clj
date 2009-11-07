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
  truba.event.test
  (:require [truba.event :as ev])
  (:use truba.unittest))

(deftest merge-listeners
  (let [ls1 (binding [ev/*listeners* (atom {})]
              (ev/on :one
                (fn [& _]))
              (ev/on :two
                (fn [& _]))
              @ev/*listeners*)
        ls2 (binding [ev/*listeners* (atom {})]
              (ev/on :one
                (fn [& _]))
              (ev/on :two
                (fn [& _]))
              @ev/*listeners*)
        all (ev/merge-listeners ls1 ls2)]
    (are= [a b] a (count (get all b))
      2 :one
      2 :two)))

(deftest add-listeners
  (binding [ev/*listeners* (atom {})]
    (ev/on :some-event
      (fn [] (println "Some function")))
    (is
      (not (empty? @ev/*listeners*))))

  (binding [ev/*listeners* (atom {})]
    (ev/on :ev
      (fn [_] (println "First listener")))
    (ev/on :ev
      (fn [_] (println "Second listener")))
    (is
      (= 2 (count (:ev @ev/*listeners*))))))

(deftest call-listeners
  (binding [ev/*listeners* (atom {})]
    (let [v (atom 0)]
      (ev/on :ev
        (fn [_ x] (swap! v + x)))
      (ev/on :ev
        (fn [_ x] (swap! v + x)))
      (ev/emit :ev 2)
      (is
        (= @v 4)))))

