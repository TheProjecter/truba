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
  truba.apidoc.simplemarkup.test
  (:require [truba.apidoc.simplemarkup :as s])
  (:use truba.unittest))

(deftest simple-strings
  (are [a b] (= a (first (s/parse b)))
    "text"          "text"
    "*text"         "*text"
    "text*"         "text*"
    [:strong "x"]   "*x*"
    [:strong "x y"] "*x y*"
    [:emp "x"]      "_x_"
    [:emp "x y"]    "_x y_"
    [:cite "x"]     "?x?"
    [:cite "x y"]   "?x y?"
    [:del "x"]      "-x-"
    [:del "x y"]    "-x y-"
    [:ins "x"]      "+x+"
    [:ins "x y"]    "+x y+"
    [:sup "x"]      "^x^"
    [:sup "x y"]    "^x y^"
    [:a "Clojure" "http://clojure.org"] "[Clojure]:http://clojure.org"))

(deftest mixed-strings
  (are [a b] (= a (s/parse b))
    ["hi" [:strong "there"]] "hi *there*"
    [[:strong "hi"] "there"] "*hi* there"
    ["who" [:strong "are"] "you?"] "who *are* you?"))

