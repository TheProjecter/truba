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
               :url  "http://opensource.org/licenses/eclipse-1.0.php"}
     :doc "String manipulation helpers."}
  neman.string
  (:refer-clojure :exclude [repeat]))

(defn join
  "Join string sequence with given separator."
  [sep & sseq]
  (apply str (interpose sep (remove nil? sseq))))

(defn split
  "Split string with given regular expression."
  [s re]
  (when s
    (seq (.split s re))))

(defn repeat
  "Repeat given string n times."
  ([s n]
    (apply str (clojure.core/repeat n s)))

  ([n]
    (repeat " " n)))

(defn tokenize
  "Split string by whitespace characters."
  [s]
  (seq (split s "\\s")))

(defn split-by-tokens [s width]
  (reverse
    (reduce
      (fn [[f & r :as xs] s]
        (if (> width (+ (count f) (count s)))
          (conj r (join " " f s))
          (conj xs s)))
      '()
      (tokenize s))))

(defn left-pad [s n]
  (format (str "%" n "s") s))

(defn right-pad [s n]
  (format (str "%" (- n) "s") s))

