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
  truba.apidoc.tags
  (:use neman.ex
        [truba.apidoc.simplemarkup :only [parse]]))

(defmulti expand
  (fn [x]
    (if (vector? x) (first x) (class x)))
  :default ::default)

(defn expand-all [& xs]
  (map expand xs))

(defmethod expand ::default [x]
  (throwf "Unknown apidoc tag: %s" x))

(defmethod expand String [x]
  (apply vector :text (parse x)))

; XXX check that all expanded elements are :text tags
(defmethod expand :ul [[_ & data]]
  (apply
    vector :ul (apply expand-all data)))

; XXX check that all expanded elements are :text tags
(defmethod expand :ol [[_ & data]]
  (apply
    vector :ol (apply expand-all data)))

(defmethod expand :table [[_ & data]]
  (apply
    vector :table (map #(apply expand-all %) data)))

