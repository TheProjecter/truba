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
  truba.event)

(declare
  #^{:doc "Global listeners, should be bound to reference type."}
  *listeners*)

(defn merge-listeners [& mseq]
  (reduce (partial merge-with concat) mseq))

(defn on
  ([event callback-fn]
   (on *listeners* event callback-fn))

  ([listeners event callback-fn]
   (swap! listeners update-in [event] conj callback-fn)))

(defn emit-to [listeners event & args]
  (doseq [f (get listeners event)] (apply f event args)))

(defn emit [event & args]
  (apply emit-to @*listeners* event args))

(defmacro deflisteners [name & body]
  `(defn ~name []
     (binding [*listeners* (atom {})]
       ~@body
       (deref *listeners*))))
