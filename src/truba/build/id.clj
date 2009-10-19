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
  truba.build.id)

(defn expand-id* [id]
  (condp #(%1 %2) id
    symbol?  {:name id}
    keyword? {:type id}
    map?     id

    ; Report invalid property id.
    (throwf "Invalid property id: %s" id)))

(defn expand-id [id]
  (let [id (expand-id* id)]
    (if (:name id)
      id
      (assoc id :uid (gensym)))))

