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
  truba.apidoc.backend.googlewiki
  (:use neman.ex))

(defmulti convert
  (fn [[tag & _]] tag))

(defmethod convert :text [[_ & text]]
  (apply str
    (map
      (fn [x]
        (if (string? x)
          x
          (let [[f & r] x]
            (apply format
              (condp = f
                :strong "*%s*"
                :emp    "_%s_"
                :cite   "`%s`"
                :del    "~~%s~~"
                :ins    "%s"
                :sup    "^%s^"
                :sub    ",,%s,,"
                :a      "[%2$s %1$s]")
              r))))
      text)))

(defmethod convert :ul [[_ & data]]
  (apply str
    (map #(format " * %s\n" (convert %)) data)))

(defmethod convert :ol [[_ & data]]
  (apply str
    (map #(format " # %s\n" (convert %)) data)))

(defmethod convert :table [[_ & xs]]
  (throwf "Tables are not supported for now"))
