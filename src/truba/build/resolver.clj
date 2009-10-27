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
  truba.build.resolver
  (:require [truba.build.property :as p]))

;(defn post-collect [{pm :props tm :tasks gm :groups :as data}]
;  (let [pm (p/calc-all pm)
;        tm (t/resolve-all tm pm)
;        tm (into (empty tm)
;            (map
;              (fn [[k v]] [k (update-in v [:action] partial pm)])
;              tm))]
;    [pm tm gm]))

(defn resolve-all [{pm :properties :as data}]
  (let [pm (p/calc-all pm)]
    (assoc data :properties pm)))
