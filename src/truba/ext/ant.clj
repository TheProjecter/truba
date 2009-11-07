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
  truba.ext.ant
  (:import (java.lang System)
           (org.apache.tools.ant Project NoBannerLogger UnknownElement)))

(defn ant-logger
  "Create standard Ant build logger."
  []
  (doto (NoBannerLogger.)
    (.setMessageOutputLevel Project/MSG_INFO)
    (.setOutputPrintStream  System/out)
    (.setErrorPrintStream   System/out)))

(defn ant-project
  "Create new Apache Ant project."
  [base-dir]
  (doto (Project.)
    (.init)
    (.setBaseDir base-dir)
    (.addBuildListener (ant-logger))))

(defn ant-subproject
  "Create Ant sub project with new base directory."
  [project base-dir]
  (doto (.createSubProject project)
    (.setBaseDir base-dir)))

(defn ant-lib [project]
  nil)

(defn unknown-element [project el-name & specs]
  (let [ue      (doto (UnknownElement. (name el-name))
                  (.setProject project))
        wrapper (.getRuntimeConfigurableWrapper ue)
        [ps cs] (if (map? (first specs))
                  specs
                  (conj specs {}))]
    (doseq [[k v] ps]
      (.setAttribute wrapper (name k) v))

    (doseq [c cs]
      (let [child (apply unknown-element project c)]
        (.addChild ue child)
        (.addChild wrapper (.getRuntimeConfigurableWrapper child))))

    (.maybeConfigure ue)
    ue))

(defn ant-task [project & specs]
  (.. (apply unknown-element project specs) getRealThing execute))


