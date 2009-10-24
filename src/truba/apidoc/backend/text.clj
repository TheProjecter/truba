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
  truba.apidoc.backend.text
  (:use neman.ex))

(defmulti convert
  (fn [[tag & _]] tag))

(defmethod convert :text [[_ & text]]
  (apply str
    (map
      (fn [x]
        (cond
          (string? x)
            x
          (= (first x) :a)
            (format "%s [%s]" (nth x 1) (nth x 2))
          :else
            (nth x 1)))
      text)))

(defmethod convert :ul [[_ & xs]]
  (apply str
    (map (fn [x] (format "* %s\n" (convert x))) xs)))

(defmethod convert :ol [[_ & xs]]
  (apply str
    (map
      (fn [i x]
        (format "%d. %s\n" i (convert x)))
      (range 1 (inc (count xs))) xs)))

(defmethod convert :table [[_ & xs]]
  (throwf "Tables are not supported for now"))
