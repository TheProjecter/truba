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
  truba.build.group
  (:require [truba.build.collector :as c])
  (:use [truba.build :only [*collector*]]
        [truba.build.property :only [property]]))

(defn expand-child-groups [{gm :groups :as data} name]
  (apply merge-with merge
    (dissoc data :groups)
    (map
      (fn [[k v]] (v (concat name k))) gm)))

(defn update-group-key [data name]
  (let [update-1   (fn [[k v]]
                     [(update-in k [:group] conj name) v]); XXX is this ok?
        update-all (fn [m]
                     (into (empty m) (map update-1 m)))
        update-key (fn [data k]
                     (update-in data [k] update-all))]
    (-> data
      (update-key :properties)
      (update-key :tasks)
      (update-key :generators))))

(defn wrap-build-body [f]
  (fn [name]
    (->
      (c/with-collector c/build-collector
        (f name))
      (expand-child-groups name))))

(defn create-build [id body]
  (with-meta [(list id) (wrap-build-body body)] {:type :Build}))

(defmacro build* [id & body]
  `(*collector*
     (create-build '~id
                   (fn [name#]
                     (property :GroupName name#)
                     ~@body))))

(defmacro build [id & [f & r :as xs]]
  (let [; Extract docstring (unused for now).
        [m xs] (if (string? f)
                 [f r]
                 [nil xs])]
    `(build* ~id ~@xs)))

(defn wrap-group-body [f]
  (fn [name]
    (->
      (c/with-collector c/group-collector
        (f name))
      (update-group-key name)
      (expand-child-groups name))))

(defn create-group [id body]
  (with-meta [(list id) (wrap-group-body body)] {:type :Group}))

(defmacro group* [id & body]
  `(*collector*
     (create-group '~id
                   (fn [name#]
                     (property :GroupName name#)
                     ~@body))))

(defmacro group [id & [f & r :as xs]]
  (let [; Extract docstring (unused for now).
        [m xs] (if (string? f)
                 [f r]
                 [nil xs])]
    `(group* ~id ~@xs)))
