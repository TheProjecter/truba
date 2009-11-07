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
  truba.repository
  (:require [truba.feed :as feed]
            [neman.xml :as xml])
  (:use neman.ex
        [truba.ext.fs :only [rmdir]]
        [truba.event :only [emit-to merge-listeners]]
        [truba.repository.listeners :only
          [standard-listeners standard-reporters]])
  (:import (java.io File IOException)
           (java.net URI)))

(def listeners
  (merge-listeners
    (standard-listeners) (standard-reporters)))

(def repo-settings
  {:author {}})

(defn create* [listeners #^URI base]
  (let [dir (File. base)
        feed-file (File. dir "atom.xml")
        emit (partial emit-to listeners)]
    (if (.exists dir)
      (emit :repository-creation-failed dir "Location already exists.")
      (try
        (.mkdirs dir)
        (xml/write! feed-file (feed/create-feed-doc repo-settings))
        (emit :repository-created dir)

        (catch IOException e
          (emit :repository-creation-failed dir (.getMessage e)))))))

(defn create! [#^URI base]
  (create* listeners base))

(defn remove* [listeners #^URI base]
  (let [dir (File. base)
        emit (partial emit-to listeners)]
    (cond
      (not (.exists dir))
        (emit :repository-removal-failed dir "Repository not found.")
      (not (.isDirectory dir))
        (emit :repository-removal-failed dir "Invalid repository location.")
      :else
        (do
          (rmdir dir)
          (emit :repository-removed dir)))))

(defn remove! [#^URI base]
  (remove* listeners base))
