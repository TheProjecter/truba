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
  truba.main
  (:require
     (truba.command repl shell repo))
  (:use [neman.main :only [defmain]]
        [truba.project.loader :only [load-buildfile]]
        [clojure.main :only [load-script]]))

(use
  ['truba.event :only ['*listeners* 'on]])

(defn load-build [file]
  (let [build (atom {})]
    (binding [*listeners* (atom {})]
      (on :loading-started
        (fn [_ build file]
          (println "Loading file " file)))

      (on :loading-finished
        (fn [_ build file data]
          (println "Loading finished")))

      (on :loading-failed
        (fn [_ build file err]
          (println "Loading failed" (.getMessage err))))

      (load-buildfile build file))))

(defmain
  :header
    "Truba project automation tool"

  :options
    ["Build Options"
     {:name "verbose"   :desc "Show verbose output"}
     {:name "&queue"    :desc "Add task to execution queue" :args "<task-pattern>*"}
     {:name "&skip"     :desc "Skip tasks" :args "<task-pattern>*"}
     {:name "&describe" :desc "Describe tasks"}
     {:name "&file"     :desc "Use file instead of the default trubafile.clj"}
     {:name "D"         :desc "Set build property" :args "<property>=<value>"}
     {:name "version"   :desc "Show version information."}

     "Runner configuration"
     {:name "&jobs"   :desc "Number of parallel jobs" :args "<num>"}
     {:name "&runner" :desc "Configure task runner" :args "<args>*"}]

  :footer
    ["When using -q or -s options, task names can use standard glob syntax.
    This lets you execute multiple tasks with single command like:\n"

    "truba -q *:clean\n"

    "That will execute clean task in every sub-module.\n
    For more info visit http://code.google.com/p/truba/\n
    Copyright (c) 2009. Krešimir Šojat. All rights reserved."]

  (:default [{:keys [queue skip describe file D] :as opts} _]
    (when (:version opts)
      (println "Truba v0.1")
      (System/exit 0))

    (println "Hello from main"))

  (:extra []
    (list
      truba.command.repl/repl
      truba.command.shell/shell
      truba.command.repo/repo-create
      truba.command.repo/repo-remove
      truba.command.repo/repo-list
      truba.command.repo/repo-info))

  (:extra
     :options
       [{:name "&file" :desc "Use file instead of the default trubafile.clj"}]

     [{file :file} _]
     (when-let [build (load-build (or file "trubafile.clj"))]
       (map
         (fn [[name {:keys [desc body] :as command}]]
           [name (assoc command :body (partial body build))])
         (:commands build)))))
