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
  truba.repository.core
  (:import (java.io File FileInputStream FileReader FileWriter
                    BufferedReader BufferedWriter PushbackReader)))


(defn read-1 [stream]
  (let [eol (Object.)
        res (read stream false eol)]
    (when-not (= res eol)
      res)))

(defn repolist-purge [data]
  (filter
    (fn [d] (-> d (File.) .exists)) data))

(defn repolist-reader [path]
  (-> path
    (FileReader.) (PushbackReader.)))

(defn load-repolist [path]
  (.createNewFile path)
  (with-open [r (repolist-reader path)]
    (read-1 r)))

(defn repolist-writer [path]
  (-> path
    (FileWriter.) (BufferedWriter.)))

(defn save-repolist [path data]
  (.createNewFile path)
  (with-open [w (repolist-writer path)]
    (.write w (binding [*print-dup* true] (pr-str (set data))))))

(defn update-repolist [path f & args]
;;  (.createNewFile path)
  (let [update (fn [data]
                 (repolist-purge
                   (apply f data args)))]
    (save-repolist
      path (-> path load-repolist update))))

(defn add-to-repolist [repolist uri]
  (update-repolist repolist conj uri))

(defn remove-from-repolist [repolist uri]
  (update-repolist repolist disj uri))
