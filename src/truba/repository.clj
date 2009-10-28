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
  (:use neman.ex)
  (:import (java.io File IOException)
           (java.net URI)))

; XXX use event system
(defn create! [#^URI base]
  (let [dir (File. base)]
    (if (.exists dir)
      (throwf "Can't create repository, location %s already exists." dir)
      (try
        (.mkdirs dir)
        ; XXX emit ok
        ; XXX write initial feed file

        (catch IOException e
          ; XXX emit don't throw
          (throwf "Failed!"))))))

(defn remove! [#^URI base]
  (let [dir (File. base)]
    (cond
      (not (.exists dir))
        (throwf "Repository not found: %s" base); XXX use emit
      (not (.isDirectory dir))
        (throwf "Invalid repoistory location: %s" base); XXX use emit
      :else
        (.delete dir))))
