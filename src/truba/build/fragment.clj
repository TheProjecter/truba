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
  truba.build.fragment)

(def +fragment+ (atom {}))

(defn add-fragment [fm f]
  (apply assoc fm f))

(defn add-fragment! [fragment]
  (swap! +fragments+ add-fragment fragment))

(defmacro deffragment* [id & body]
  `(vector '~id (fn [] ~@body)))

(defmacro deffragment [id & [f & r :as xs]]
  (let [; Extract docstring (unused for now).
        [m xs] (if (string? f)
                 [f r]
                 [nil xs])]
    `(deffragment* ~id ~@xs)))
