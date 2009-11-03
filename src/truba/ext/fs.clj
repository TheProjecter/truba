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
  truba.ext.fs
  (:import (java.io File)
           (org.apache.commons.io FileUtils IOUtils)))

(defn copy
  "Copy file from source location to destionation."
  [#^File src-file #^File dest-file]
  (FileUtils/copyFile src-file dest-file))

(defn copy-to
  "Copy file from source to destionation directory with the same name."
  [#^File src-file #^File dest-dir]
  (FileUtils/copyFileToDirectory src-file dest-dir))

(defn move
  "Move file from source location to destionation."
  [#^File src-file #^File dest-file]
  (FileUtils/moveFile src-file dest-file))

(defn make-dir
  "Create directories, including necessary parent dirs."
  [& dirs]
  (doseq [d dirs] (.mkdirs d)))

(defn rm
  "Remove file from file system."
  [#^File file]
  (.delete file))

(defn rmdir
  "Remove directory recursively (like 'rm -r dirname')."
  [#^File dir]
  (FileUtils/deleteDirectory dir))

