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
  truba.project.loader
  (:import (java.io File)
           (clojure.lang Compiler))
  (:use
     [truba.event :only [emit]]
     [truba.build.collector :only [with-collector file-collector]]))

(defn load-buildfile [file]
  (let [b-name (gensym "trubafile_")
        b-ns   (create-ns b-name)]
    (binding [*ns* b-ns]
      ; Add clojure.core
      (refer-clojure)

      ; Standard imports
      (import '(java.io File)
              '(java.net URL URI))

      ; Truba parts initialy available in trubafile.
      (use
        '(truba.build
           [command :only [command]]
           [property :only [property properties]]
           [task :only [task]]
           [generator :only [generator]]))

      ; Add basedir of trubafile to classpath.
      ; XXX Fix this
      #_(let [dir (.. (File. file) getParentFile toURI)]
        (add-classpath dir))

      ; XXX Fix this
      #_(emit :loading-started file)
      (merge
        (with-collector file-collector
          (Compiler/loadFile file))
        {:Finalizer
          (fn []
            (remove-ns b-name))}))))
