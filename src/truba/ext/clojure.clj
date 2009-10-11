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
  truba.ext.clojure
  (:import (java.io File FileReader BufferedReader PushbackReader)))

;;
;; Helpers used to exctract namespace declarations from Clojure source files
;;

(defn ns-decl [file]
  (with-open [p (PushbackReader. (BufferedReader. (FileReader. file)))]
    (reduce
      (fn [n [x & [y & _]]]
        (if (= x 'ns) (conj n y) n))
      []
      (take-while (complement nil?) (repeatedly #(read p false nil))))))

(defn clojure-file? [file]
  (.endsWith (.getName file) ".clj"))

(defn clojure-files [src-dirs]
  (filter clojure-file? (mapcat file-seq src-dirs)))

(defn namespaces-in-dirs
  "Return names of namespaces in clojure files under src-dirs directories.
   All elements of src-dirs must be instances of java.io.File class."
  [src-dirs]
  (filter (complement nil?)
    (mapcat ns-decl (clojure-files src-dirs))))

