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
  truba.conf
  (:import (java.lang System)
           (java.io File))
  (:use
     [clojure.main :only [load-script]]
     truba.event))

(def home-dir
  (File. (System/getProperty "truba.home")))

(def var-dir
  (File. home-dir "var"))

(def repolist-file
  (File. var-dir "repolist.clj"))

;; Load external configuration files.
(defn conf-file []
  (or (System/getProperty "truba.conf") (System/getenv "TRUBA_CONF")))

(when-not *compile-files*
  (when-let [cf (conf-file)] (load-script cf)))
