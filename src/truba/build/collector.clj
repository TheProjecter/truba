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

; XXX Remove this later
(defn post-collect [data]
  data)
;(defn post-collect [{pm :props tm :tasks gm :groups :as data}]
;  (let [pm (p/calc-all pm)
;        tm (t/resolve-all tm pm)
;        tm (into (empty tm)
;            (map
;              (fn [[k v]] [k (update-in v [:action] partial pm)])
;              tm))]
;    [pm tm gm]))

(defn with-collector* [collector-fn body-fn]
  (post-collect
    (collect collector-fn body-fn)))

(defmacro with-collector [collector-fn & body]
  `(with-collector* ~collector-fn (fn [] ~@body)))

(defn try-to-add [data key [name specs] type-str]
  (if (contains? (get data key) name)
    (throwf "%s %s is already defined." type-str name)
    (assoc-in data
      [key name] specs)))

(defn add-group [data group]
  (try-to-add data :groups group "Group"))

(defn add-task [data task]
  (try-to-add data :tasks task "Task"))

(defn add-generator [data generator]
  (try-to-add data :generators generator "Generator"))

(defn add-property [data property]
  (try-to-add data :properties property "Property"))

(defn add-command [data [name _ :as command]]
  (if (contains? (set (map first (:commands data))) name)
    (throwf "Command %s is already defined." name)
    (update-in data [:commands]
      (fn [cs]
        (conj (vec cs) command)))))

(defn file-collector [data x]
  (let [data? (fn [key]
                (not (empty? (get data key))))]
    (condp #(= %1 (type %2)) x
      :Build
        (cond
          (data? :groups)
            (throwf "Can't define top level build group, one is already defined.")

          (or (data? :tasks) (data? :generators) (data? :commands))
            (throwf "Can't define build group, items outside of it already defined."))

      :Group
        (throwf "Can't define sub group.")

      :Finalizer
        nil; XXX add this

      :Listener
        nil; XXX add this

      :Task
        (if-not (data? :groups)
          (add-task data x)
          (throwf "Can't declare task outside of the main build group."))

      :Generator
        (if-not (data? :groups)
          (add-generator data x)
          (throwf "Can't declare generator outside of the main build group."))

      :Property
        (if-not (data? :groups)
          (add-property data x)
          (throwf "Can't declare property outside of the main build group."))

      :Command
        (if-not (data? :groups)
          (add-command data x)
          (throwf "Can't declare command outside of the main build group.")))))

(defn build-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Build declarations can't be nested.")
        :Group     add-group
        :Finalizer nil; XXX add this
        :Listener  nil; XXX add this
        :Task      add-task
        :Generator add-generator
        :Property  add-property
        :Command   add-command))))

(defn group-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Can't declare build group inside the sub group.")
        :Group     add-group
        :Finalizer nil; XXX add this
        :Listener  nil; XXX add this
        :Task      add-task
        :Generator add-generator
        :Property  add-property
        :Command   (throwf "Can't declare commaind inside the sub group.")))))

(defn fragment-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Can't declare group inside the fragment.")
        :Group     (throwf "Can't declare group inside the fragment.")
        :Finalizer nil; XXX add this
        :Listener  nil; XXX add this
        :Task      add-task
        :Generator add-generator
        :Property  add-property
        :Command   (throwf "Can't declare command inside the fragment.")))))

(defn generator-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Can't declare group inside the generator.")
        :Group     (throwf "Can't declare group inside the generator.")
        :Finalizer nil; XXX add this
        :Listener  nil; XXX add this
        :Task      add-task
        :Generator add-generator
        :Property  add-property
        :Command   (throwf "Can't declare command inside the generator.")))))
