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
  truba.build.action
  (:use [truba.build.property :only [with-properties]]))

(defmacro defaction
  {:arglists '([name doc-string? attr-map? p-deps? p-bindings? & fn-tail])}
  [name & [f s & r :as xs]]
  (let [[m xs] (cond
                 (and (string? f) (map? s))
                   [(assoc s :doc f) r]
                 (string? f)
                   [{:doc f} (next xs)]
                 (map? f)
                   [f (next xs)]
                 :else
                   [{} xs])

        [f s t & _] xs
        [pd pb xs]  (cond
                      (and (vector? f) (vector? s) (vector? t))
                        [f s (next xs)]
                      (and (vector? f) (vector? s))
                        [f f (next xs)]
                      :else
                        [[] [] xs])]
    `(defn ~(vary-meta name merge m) [& args#]
       (with-properties ~pd ~pb
         (apply (fn ~@xs) args#)))))

