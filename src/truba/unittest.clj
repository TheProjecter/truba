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
  truba.unittest
  (:refer-clojure :exclude [assert]))

(defmacro thrown?
  ([expr]
   `(thrown? Exception ~expr))

  ([exception expr]
   `(thrown? ~exception #".*" ~expr))

  ([exception message-re expr]
   `(try
      ~expr
      false
      (catch ~exception e#
        (not (nil? (re-matches ~message-re (.getMessage e#)))))
      (catch Throwable _#
        false))))

(def *report* identity)

(defmulti report
  (fn [{:keys [assert status]}] [assert status]))

(defmethod report [:is :pass] [{expr :expr}]
  (format "[SUCCESS] %s" expr))

(defmethod report [:is :fail] [{expr :expr}]
  (format "[FAIL]    %s" expr))

(defmethod report [:is :error] [{expr :expr message :message}]
  (format "[ERROR]   %s with message: %s" expr message))

(defmethod report [:are :pass] [{:keys [expr vars args]}]
  (format "[SUCCESS] %s for %s %s" expr vars args))

(defmethod report [:are :fail] [{:keys [expr vars args]}]
  (format "[FAIL]    %s for %s %s" expr vars args))

(defmethod report [:are :error] [{:keys [expr vars args message]}]
  (format "[ERROR]   %s for %s %s with message: %s" expr vars args message))

(defn update-context [x desc]
  (update-in x [:context]
    (fn [c]
      (if c (format "%s %s" desc c) desc))))

(defmacro testing [desc & body]
  `(let [report# *report*]
     (binding [*report* (fn [x#]
                          (report# (update-context x# ~desc)))]
       ~@body)))

(defmacro try-assert [& body]
  `(try
     ~@body
     (catch Throwable t#
       {:status :error :message (.getMessage t#)})))

(defmacro is [expr]
  `(*report*
     (merge
       {:assert :is :expr '~expr}
       (try-assert
         {:status (if ~expr :pass :fail)}))))

(defmacro are* [vars expr & args]
  `(*report*
     (merge
       {:assert :are :expr '~expr :vars '~vars :args '~args}
       (try-assert
         (let [~vars ~(vec args), result# ~expr]
           {:status (if result# :pass :fail)})))))

(defmacro are [bindings expr & args]
  (let [args (partition (count bindings) args)]
    `(do
       ~@(map
           (fn [as] `(are* ~bindings ~expr ~@as)) args))))

(defmacro deftest [name & [f & _ :as xs]]
  (let [; Split fixtures dependencies
        [fixtures & body] (if (vector? f)
                            xs
                            (conj xs nil))

        ; Wrap test body with setup/cleanup fixture pairs.
        wrap (fn [body [vars fixture]]
               `(let [args# ((:setup ~fixture))
                      ~vars args#]
                  ~@body
                  (when-let [cleanup# (:cleanup ~fixture)]
                    (cleanup# args#))))

        ; New test body with all fixtures added.
        body (if-let [fixtures (seq (partition 2 fixtures))]
               (list
                 (reduce wrap body fixtures))
               body)]
    `(def ~(with-meta name {:unittest true})
       (fn []
         (let [reports# (atom [])]
           (binding [*report* (fn [x#] (swap! reports# conj x#))]
             ~@body
             (deref reports#)))))))

(defn test? [v]
  (:unittest (meta v)))

(defn print-reports [report-seq]
  (doseq [r report-seq] (println (report r))))

(comment

  (def fixture-1
    :setup
      (fn []
        [:a :b :c]))

  (def fixture-2
    :setup
      (fn []
        [:d :e :f]))

  (deftest test1 [[a b c] fixture-1 [d e f] fixture-2]
    (println "Normal test code with values from fixtures.")))
