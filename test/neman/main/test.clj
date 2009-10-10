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
  (:use truba.unittest))

(deftest split-prelude
  (is (= [{} (list 'a 'b 'c)] (m/split-prelude (list 'a 'b 'c)))))

(deftest split-meta
  (are [a b] (= a (m/split-meta b))
    [{} (list 'a 'b 'c)] (list 'a 'b 'c)
    [{:doc "test"} (list 'a 'b 'c)] (list "test" 'a 'b 'c)
    [{:x 1 :y 2} (list 'a 'b 'c)] (list {:x 1 :y 2} 'a 'b 'c)
    [{:doc "test" :x 1 :y 2} (list 'a 'b 'c)] (list "test" {:x 1 :y 2} 'a 'b 'c)))

(deftest split-command
  (are [a b] (= a (m/split-command b))
    [:default (list "-x" "-y")] (list "-x" "-y")
    ["install" (list "-x")] (list "install" "-x")
    ["install" nil] (list "install")))

(deftest block-seq
  (are [a b] (= a (m/block-seq b))
    ; Simple single command block
    (list
      ["install" [{} (list [] 'a 'b)]])
    (list
      '(install [] a b))

    ; Command name as string
    (list
      ["install" [{} (list [] 'a 'b)]])
    (list
      '("install" [] a b))

    ; Command with extra options
    (list
      ["install" [{:desc "Desc"} '([] a b)]])
    (list
      '(install :desc "Desc" [] a b))

    ; Default block with normal command
    (list
      [:default [{} '([] a b)]] ["install" [{} '([] c d)]])
    (list
      '(:default [] a b) '(install [] c d))))
