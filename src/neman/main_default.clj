;; Copyright (c) Krešimir Šojat, 2009. All rights reserved. The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution. By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license. You must not
;; remove this notice, or any other, from this software.

(in-ns 'neman.main)

(defn wrap-default* [f]
  (try
    (f)
    (catch Exception e
      (println (.getMessage e)) (System/exit 1))))

(defmacro wrap-default [block]
  `(wrap-default* (fn [] ~block)))

(def help-option
  {:name "help" :desc "Print Help"})

(defn add-help-option [opts]
  (apply vector help-option opts))

(def version-option
  {:name "version" :desc "Show version information"})

(defn add-version-option [opts]
  (apply vector version-option opts))

(defn add-standard-options [{ver :version :as specs}]
  (update-in specs [:options]
    (if ver
      (comp add-help-option add-version-option) add-help-option)))

(defn help-hook [specs {help? :help}]
  (when help?
    (println (help-str specs {}))))

(defn version-hook [{ver :version} {ver? :version}]
  (when ver?
    (println ver) (System/exit 0)))

(defmacro standard-hooks [specs opts]
  `(do
     (help-hook ~specs ~opts)
     (version-hook ~specs ~opts)))

(defmacro create-default-1 [_ [bindings & body]]
  `(fn [& ~bindings] ~@body))

(defn make-specs [specs]
  (-> specs
    add-standard-options convert-specs))

(defmacro create-default-2 [specs [bindings & body]]
  `(fn [& args#]
     (wrap-default
       (let [specs#    (make-specs ~specs)
             [f# s#]   (parse args# (:options specs#))
             ~bindings [f# s#]]
         (standard-hooks specs# f#)
         ~@body))))

(defmacro create-default [specs block]
  (if (empty? specs)
    `(create-default-1 ~specs ~block)
    `(create-default-2 ~specs ~block)))
