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

(defn with-collector* [collector-fn body-fn]
  (collect collector-fn body-fn))

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

(defn add-finalizer [{f1 :finalizers :as data} [f2]]
  (assoc data :finalizers
    (if f1
      (fn [] (f1) (f2))
      f2)))

(defn add-listener [data listener]
  (throwf "Listener collector not implemented.")); XXX add this

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
                (not (empty? (get data key))))
        add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build
          (cond
            (data? :groups)
              (throwf "Can't define top level build group, one is already defined.")

            (or (data? :tasks) (data? :generators) (data? :commands))
              (throwf "Can't define build group, items outside of it already defined.")
            :else
              add-group)

        :Group
          (throwf "Can't define sub group.")

        :Finalizer
          add-finalizer

        :Listener
          add-listener

        :Task
          (if-not (data? :groups)
            add-task
            (throwf "Can't declare task outside of the main build group."))

        :Generator
          (if-not (data? :groups)
            add-generator
            (throwf "Can't declare generator outside of the main build group."))

        :Property
          (if-not (data? :groups)
            add-property
            (throwf "Can't declare property outside of the main build group."))

        :Command
          (if-not (data? :groups)
            add-command
            (throwf "Can't declare command outside of the main build group."))
        ; Unknown element
        (throwf "Can't collect element of type: %s" (type x))))))

(defn build-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Build declarations can't be nested.")
        :Group     add-group
        :Finalizer add-finalizer
        :Listener  add-listener
        :Task      add-task
        :Generator add-generator
        :Property  add-property
        :Command   add-command
        ; Unknown element
        (throwf "Can't collect element of type: %s" (type x))))))

(defn group-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Can't declare build group inside the sub group.")
        :Group     add-group
        :Finalizer add-finalizer
        :Listener  add-listener
        :Task      add-task
        :Generator add-generator
        :Property  add-property
        :Command   (throwf "Can't declare commaind inside the sub group.")
        ; Unknown element
        (throwf "Can't collect element of type: %s" (type x))))))

(defn build-fragment-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Can't declare group inside the fragment.")
        :Group     (throwf "Can't declare group inside the fragment.")
        :Finalizer (throwf "Can't declare finalizer inside the fragment.")
        :Listener  add-listener
        :Task      add-task
        :Generator add-generator
        :Property  add-property
        :Command   add-command
        ; Unknown element
        (throwf "Can't collect element of type: %s" (type x))))))

(defn group-fragment-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Can't declare group inside the fragment.")
        :Group     (throwf "Can't declare group inside the fragment.")
        :Finalizer (throwf "Can't declare finalizer inside the fragment.")
        :Listener  add-listener
        :Task      add-task
        :Generator add-generator
        :Property  add-property
        :Command   (throwf "Can't declare command inside the fragment.")
        ; Unknown element
        (throwf "Can't collect element of type: %s" (type x))))))

(defn property-generator-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Can't declare group inside the generator.")
        :Group     (throwf "Can't declare group inside the generator.")
        :Finalizer (throwf "Can't declare finalizer inside the generator.")
        :Listener  add-listener
        :Task      (throwf "Can't declare task inside the property generator.")
        :Generator add-generator
        :Property  add-property
        :Command   (throwf "Can't declare command inside the generator.")
        ; Unknown element
        (throwf "Can't collect element of type: %s" (type x))))))

(defn task-generator-collector [data x]
  (let [add-with #(%1 data x)]
    (add-with
      (condp #(= %1 (type %2)) x
        :Build     (throwf "Can't declare group inside the generator.")
        :Group     (throwf "Can't declare group inside the generator.")
        :Finalizer (throwf "Can't declare finalizer inside the generator.")
        :Listener  add-listener
        :Task      add-task
        :Generator add-generator
        :Property  add-property
        :Command   (throwf "Can't declare command inside the generator.")
        ; Unknown element
        (throwf "Can't collect element of type: %s" (type x))))))
