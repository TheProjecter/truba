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
  truba.build.generator
  (:use neman.ex
        [truba.build.id :only [expand-id]]
        [truba.build.property :only [with-properties]]))

(defmulti expand-key
  (fn [_ _ _ [key & body]] key) :default ::default)

(defmethod expand-key ::default [key & _]
  (throwf "Unknow generator key: %s" key))

(defmethod expand-key :match [deps vars qid [_ & body]]
  [:match
   `(fn [pm#]
      (with-properties pm# ~deps ~vars
        (fn [~qid] ~@body)))])

(defmethod expand-key :once [deps vars _ [_ & body]]
  [:once
   `(fn [pm#]
      (with-properties pm# ~deps ~vars
        (fn [] ~@body)))])

(defmethod expand-key :each [deps vars _ [_ & body]]
  [:each
   `(fn [pm#]
      (with-properties pm# ~deps ~vars
        (fn [] ~@body)))])

(defn create-generator [id body]
  (assert (contains? body :match))
  (assert (contains? body :each))
  [(expand-id id) (merge {:once (fn [_])} body)])

(defmacro generator [id & [f s t & r :as xs]]
  (let [[pd pb qid xs] (cond
                         (and (vector? f) (vector? s) (vector? t))
                           [f s t r]
                         (and (vector? f) (vector? s))
                           [f f s (nnext xs)]
                         :else
                           [[] [] f (next xs)])
        body (into {}
               (map
                 (fn [x]
                   (expand-key pd pb qid x))
                 xs))]
    `(create-generator '~id ~body)))
