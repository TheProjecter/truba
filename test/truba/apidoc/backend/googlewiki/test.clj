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
  truba.apidoc.backend.googlewiki.test
  (:require [truba.apidoc.backend.googlewiki :as gw])
  (:use truba.unittest))

(deftest text
  (are= [a b] a (gw/convert b)
    "text" [:text "text"]
    "one *two*" [:text "one " [:strong "two"]]
    "[http://clojure.org Clojure]" [:text [:a "Clojure" "http://clojure.org"]]))

(deftest unordered-lists
  (are= [a b] a (gw/convert b)
    " * one\n * two\n * three\n" [:ul [:text "one"] [:text "two"] [:text "three"]]))

(deftest ordered-lists
  (are= [a b] a (gw/convert b)
    " # one\n # two\n # three\n" [:ol [:text "one"] [:text "two"] [:text "three"]]))

