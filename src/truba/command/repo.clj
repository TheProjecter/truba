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
  truba.command.repo
  (:require [truba.conf :as conf])
  (:use [neman.main :only [command]]
        [truba.repository :only [create! remove!]]
        [truba.repository.core :only [load-repolist]])
  (:import (java.io File)))

(defn create-repo-uri [repo]
  (let [repo-path (File. repo)
        repo-path (if (.isAbsolute repo-path)
                    repo-path
                    (File. (File. (System/getProperty "user.dir"))
                           repo))]
    (.toURI repo-path)))

(def repo-create
  (command "repo:create"
    :desc
      "Create new local repository."
    [repo-dir]
    (if repo-dir
      (try
        (-> repo-dir
          create-repo-uri create!)
        (catch Exception e
          (println (.getMessage e))))
      (println "Missing repository location."))))

(def repo-remove
  (command "repo:remove"
    :desc
      "Remove local repository."
    [repo-dir]
    (if repo-dir
      (try
        (-> repo-dir
          create-repo-uri remove!)
        (catch Exception e
          (println (.getMessage e))))
      (println "Missing repository location."))))

(def repo-list
  (command "repo:list" []
    :desc
      "Show all known repositories."
    []
    (let [repolist (load-repolist conf/repolist-file)]
      (if (empty? repolist)
        (println "No repositories found.")
        (do
          (doseq [r repolist]
            (printf "Repository: %s\n" r)))))))

; XXX add this
(def repo-add
  (command "repo:add"
    :desc
      "Add new repository to the list of known repositories."
    []
    (println "Add repository command is not implemented.")))

; XXX add this
(def repo-info
  (command "repo:info"
    :desc
      "Show information about the given repository."
    [repo-dir]
    (println "Repository info is not implemented.")))

