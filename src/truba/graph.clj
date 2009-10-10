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
  truba.graph
  (:use neman.ex))

(defn tsort
  ([graph start]
    (let [hidden (gensym)
          graph  (assoc graph hidden start)
          visited (atom [])
          sort (fn sort [v back]
                 (cond
                   ; Detect cycles
                   (some #(= v %) back)
                     (throwf "Cycle detected: %s <-> %s" v (last back))

                   ; Check does node exists
                   (not (contains? graph v))
                     (throwf "Dependency %s for %s missing." (last back) v)

                   :else
                     (when-not (some #(= v %) @visited)
                       (let [ns (graph v)]
                         (if (not (empty? ns))
                           (let [back (conj back v)]
                             (doseq [n ns]
                               (sort n back)))))
                        (swap! visited conj v))))]
      (sort hidden [])
      (drop-last @visited)))

  ([graph]
    (when (not (empty? graph))
      (tsort graph (set (keys graph))))))

(defn psort [graph & specs]
  (let [order (apply tsort graph specs)

        ranks (reduce
                (fn [rm v]
                  (let [dist (map second (select-keys rm (graph v)))
                        dist-max (if (empty? dist)
                                   0
                                   (apply max dist))]
                    (assoc rm v (inc dist-max))))
                {}
                order)

        groups (reduce
                (fn [m [k r]] (assoc m r (conj (m r) k)))
                {}
                ranks)]
    (map second (sort #(> (first %1) (first %2)) groups))))

(comment

  (def g
    {:a #{:b :c :d} :b #{} :c #{:e :f} :d #{:c} :e #{} :f #{}})

  (tsort g)
  ; Returns
  (:f :e :c :b :d :a)

  (psort g)
  ; Returns
  ((:a) (:d) (:c) (:f :e :b))

); end of comment

