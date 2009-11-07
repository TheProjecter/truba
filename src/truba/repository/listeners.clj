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
  truba.repository.listeners
  (:require [truba.conf :as conf]
           [truba.repository.core :as core])
  (:use [truba.event :only [deflisteners on]]))

(deflisteners standard-reporters
  (on :repository-created
    (fn [_ uri]
      (printf "Repository created at: %s\n" uri)))

  (on :repository-creation-failed
    (fn [_ uri message]
      (printf "Faild to create the repository %s with message: %s\n"
              uri
              message)))

  (on :repository-removed
    (fn [_ uri]
      (printf "Repository removed at: %s\n" uri)))

  (on :repository-removal-failed
    (fn [_ uri message]
      (printf "Faild to remove repository %s with message: %s\n"
              uri
              message))))

(deflisteners standard-listeners
  (on :repository-created
    (fn [_ uri]
      (core/add-to-repolist conf/repolist-file (str uri))))

  (on :repository-removed
    (fn [_ uri]
      (core/remove-from-repolist conf/repolist-file (str uri)))))

