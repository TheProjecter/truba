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
 neman.cli.desc.test
 (:require [neman.cli.desc :as desc])
 (:use clojure.test))

(deftest create-option-name
  (are [a b] (= a (desc/to-option-name b))
    [nil "help"]    "help"
    ["v" "version"] "&version"
    ["H" "help"]    "&Help")
  (is
    (thrown? Exception (desc/to-option-name "abc123")))
  (is
    (thrown? Exception (desc/to-option-name 42))))

(deftest create-option-args
  (are [a b] (= a (desc/to-option-args b))
    {:name [] :sep nil :num 0} ""
    {:name ["type"] :sep nil :num 1} "<type>"
    {:name ["task"] :sep nil :num \*} "<task>*"
    {:name ["key" "val"] :sep "=" :num 1} "<key>=<val>")
  (is
    (thrown? Exception (desc/to-option-args "<invalid")))
  (is
    (thrown? Exception (desc/to-option-args "<key>=<val>*"))))

