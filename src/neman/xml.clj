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
  neman.xml
  (:refer-clojure :exclude [doc])
  (:import (java.io File FileWriter BufferedWriter)
           (org.jdom Namespace Document Element)
           (org.jdom.output XMLOutputter Format)))

(def pretty-format
  (Format/getPrettyFormat))

(def xml-outputter
  (XMLOutputter. pretty-format))

(defmacro doc [root-element]
  `(doto (Document.)
     (.setRootElement ~root-element)))

(defmacro => [tag & body]
  (if (vector? tag)
    `(doto (Element. (name ~(first tag)))
       (.setNamespace ~(second tag))
       ~@body)
    `(doto (Element. (name ~tag)) ~@body)))

(defn write! [#^File path doc]
  (with-open [out (-> path (FileWriter.) (BufferedWriter.))]
    (.output xml-outputter doc out)))

