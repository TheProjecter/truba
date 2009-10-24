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
  truba.apidoc.tags.test
  (:require [truba.apidoc.tags :as t])
  (:use truba.unittest))

(deftest unknown-tag
  (is
    (thrown? (t/expand [:unknown-tag 42])))
  (is
    (thrown? (t/expand 42))))

(deftest raw-strings
  (are= [a b] a (t/expand b)
    [:text] ""
    [:text "hi"] "hi"
    [:text "hi " [:strong "you"]] "hi *you*"))

(deftest unordered-lists
  (are= [a b] a (t/expand b)
    [:ul [:text "one"] [:text "two"] [:text "three"]]
    [:ul "one" "two" "three"]))

(deftest ordered-lists
  (are= [a b] a (t/expand b)
    [:ol [:text "one"] [:text "two"] [:text "three"]]
    [:ol "one" "two" "three"]))

(deftest tables
  (are= [a b] a (t/expand b)
    [:table [[:text "1"] [:text "2"] [:text "3"]]
            [[:text "4"] [:text "5"] [:text "6"]]]
    [:table ["1" "2" "3"] ["4" "5" "6"]]))
