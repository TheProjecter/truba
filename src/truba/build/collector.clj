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
  truba.build.collector
  (:use neman.ex
        [truba.build :only [*collector*]]))

(defn collect [collector-fn body-fn]
  (reduce
    collector-fn
    {}
    (let [data (atom [])]
      (binding [*collector* #(swap! data conj %)]
        (body-fn))
      @data)))

; XXX Add this later
(defn post-collect [data]
  data)

(defn with-collector* [collector-fn body-fn]
  (post-collect
    (collect collector-fn body-fn)))

(defmacro with-collector [collector-fn & body]
  `(with-collector* ~collector-fn (fn [] ~@body)))

(defn add-command [data [name _ :as command]]
  (if (contains? (set (map first (:commands data))) name)
    (throwf "Command %s already exists." name)
    (update-in data [:commands]
      (fn [cs]
        (conj (vec cs) command)))))

(defn file-collector [data x]
  (condp #(= %1 (type %2)) x
    :Build
      nil

    :Group
      nil

    :Task
      nil

    :Property
      nil

    :Command
      (add-command data x)))
