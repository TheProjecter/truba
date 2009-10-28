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
  truba.repository.test
  (:require [truba.repository :as r])
  (:use truba.unittest)
  (:import (java.io File)
           (java.net URI)))

(defn tmp-dir []
  (doto (File/createTempFile "tmp" (Long/toString (System/nanoTime)))
    .delete
    .mkdir))

(def base-dir
  {:setup
    (fn []
      (tmp-dir))
   :cleanup
    (fn [tmp-dir]
      (println "I was called"); XXX Check why this is not printed.
      (.delete tmp-dir))})

(deftest create-repository
  [base-dir base-dir]
  (let [base-uri (.toURI base-dir)
        repo-uri (URI. (str base-uri "testrepo"))]
    (r/create! repo-uri)
    (is
      (.exists (File. repo-uri)))
    (is
      (thrown? (r/create! repo-uri)))))

(deftest remove-repository
  [base-dir base-dir]
  (let [base-uri (.toURI base-dir)
        repo-uri (URI. (str base-uri "testrepo"))]
    (r/create! repo-uri)
    (is
      (.exists (File. repo-uri)))
    (r/remove! repo-uri)
    (is
      (not (.exists (File. repo-uri))))
    (is
      (thrown? (r/remove! repo-uri)))))
