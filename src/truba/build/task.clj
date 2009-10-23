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
  truba.build.task
  (:use [truba.build :only [*collector*]]
        [truba.build.id :only [expand-id]]
        [truba.build.property :only [with-properties]]))

(defmacro assert-all [& xs]
  `(do
     ~@(map (fn [x] `(assert ~x)) xs)))

(defmacro pfn [f s & xs]
  `(fn [pm#]
     (with-properties pm# ~f ~s
       (fn [] ~@xs))))

(defn set-type [t]
  (with-meta t {:type :Task}))

(defn create-task [id deps body]
  (set-type
    [(expand-id id) {:deps deps :body body}]))

(defmacro task* [id deps body]
  `(*collector*
     (create-task '~id (pfn ~@deps) (pfn ~@body))))

(defmacro task [id & [f & r :as xs]]
  (let [; Extract doc string (unused for now)
        [m xs] (if (string? f)
                 [{:doc f} r] [{} xs ])

        [f & r] xs]
    (if (set? f)
      `(task* ~id ~(list [] [] f) ~r)
      (let [[[df & dx] [bf & bx] & others] xs]
        (assert-all
          (not (seq others)) (= df :deps) (= bf :body))
        `(task* ~id ~dx ~bx)))))

(comment

  ; Simple task with no dependencies.
  (task example1 #{}
    (println "Hello!"))

  ; Task with simple dependencies
  (task example2 #{'example1}
    (println "Hello 2!"))

) ; end of comment
