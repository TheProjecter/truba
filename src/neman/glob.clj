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
 neman.glob
 (:import (java.io File)
          (java.util.regex Matcher)))

(defn- glob->regex [s]
  (-> s
    (.replace "." "\\.")
    (.replace "^" "\\^")
    (.replace "*" "(\\w*)*?")
    (.replace "?" "\\w")))

(defn glob-pattern [s]
  (-> s
    glob->regex re-pattern))

(defn glob-match [glob-str str]
  (not (nil? (re-matches (glob-pattern glob-str) str))))

; XXX replace / with sep
(def sep
  (File/separator))

(def path-re
  (re-pattern
    "(?:(.*?)(?:(\\*\\*)|(\\*)|(\\?)|(?:\\{([^\\}]*)\\}))|(.*))"))

(defn path-glob->regex [s]
  (apply str
    (map
      (fn [[_ raw1 x1 x2 x3 x4 raw2]]
        (str
          (.replace (or raw1 raw2 "") "." "\\.")
          (cond
            x1 "(\\w*/?)*?"
            x2 "\\w*?"
            x3 "\\w"
            x4 (format "(%s)" (.replace x4 "," "|")))))
      (re-seq path-re s))))

(defn path-glob-pattern [s]
  (-> s
    path-glob->regex re-pattern))

