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
  truba.feed
  (:require [neman.xml :as xml])
  (:use [neman.xml :only [=> write!]])
  (:import (java.util UUID Date)
           (org.jdom Namespace Document Element)
           (org.apache.commons.lang.time DateFormatUtils)))

(defn now []
  (Date.))

(def date-format
  DateFormatUtils/ISO_DATETIME_TIME_ZONE_FORMAT)

(defn now-str []
  (.format date-format (now)))

(def atom-ns
  (Namespace/getNamespace "http://www.w3.org/2005/Atom"))

(defn feed-generator []
  (=> [:generator atom-ns]
    (.setAttribute "version" "v0.1a")
    (.setAttribute "uri" "http://code.google.com/p/truba/")
    (.setText "Truba project management tool for Clojure.")))

(defn feed-title []
  (=> [:title atom-ns]
    (.setText "Truba Repository")))

(defn feed-author [author]
  (=> [:author atom-ns]
    (.addContent
      (=> [:name atom-ns]
        (.setText (:name author))))
    (.addContent
      (=> [:uri atom-ns]
        (.setText (:uri author))))
    (.addContent
      (=> [:email atom-ns]
        (.setText (:email author))))))

(defn feed-id []
  (=> [:id atom-ns]
    (.setText (str "uuid:" (UUID/randomUUID)))))

(defn feed-updated []
  (=> [:updated atom-ns]
    (.setText (now-str))))

(defn create-feed-doc [settings]
  (xml/doc
    (=> [:feed atom-ns]
      (.addContent (feed-title))
      (.addContent (feed-generator))
      (.addContent (feed-author
                     (merge
                       {:name  "Unknown"
                        :uri   "http://code.google.com/p/truba/"}
                       (:author settings))))
      (.addContent (feed-id))
      (.addContent (feed-updated)))))

