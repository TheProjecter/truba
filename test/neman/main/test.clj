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
  neman.main.test
  (:require [neman.main :as m])
  (:use clojure.test))

(deftest split-prelude
  (is (= [{} (list 'a 'b 'c)] (m/split-prelude (list 'a 'b 'c)))))

(deftest split-meta
  (is
    (= [{} (list 'a 'b 'c)] (m/split-meta (list 'a 'b 'c))))
  (is
    (= [{:doc "test"} (list 'a 'b 'c)] (m/split-meta (list "test" 'a 'b 'c))))
  (is
    (= [{:x 1 :y 2} (list 'a 'b 'c)] (m/split-meta (list {:x 1 :y 2} 'a 'b 'c))))
  (is
    (= [{:doc "test" :x 1 :y 2} (list 'a 'b 'c)] (m/split-meta (list "test" {:x 1 :y 2} 'a 'b 'c)))))

