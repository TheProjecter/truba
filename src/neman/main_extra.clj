;; Copyright (c) Krešimir Šojat, 2009. All rights reserved. The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution. By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license. You must not
;; remove this notice, or any other, from this software.

(in-ns 'neman.main)

(defmacro create-extra-1 [_ [bindings & body]]
  `(fn [& ~bindings]
     ~@body))

(defmacro create-extra-2 [specs [bidings & body]]
  `(fn [& args#]
     (let [[f# s#] (parse args# (:options specs#))]
       ~@body)))

(defmacro create-extra [specs block]
  (if (empty? specs)
    `(create-extra-1 ~specs ~block)
    `(create-extra-2 ~specs ~block)))


