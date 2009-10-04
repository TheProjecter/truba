;; Copyright (c) Krešimir Šojat, 2009. All rights reserved. The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution. By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license. You must not
;; remove this notice, or any other, from this software.

(ns run
  (:use truba.unittest))

(def tests
  ['neman.glob.test
   'neman.cli.test
   'neman.cli.desc.test
   'neman.main.test])

(defn run-tests []
  (let [found-ns (atom [])]
    (doseq [t tests]
      (try
        (require :reload-all t)
        (swap! found-ns conj t)

        (catch Exception e
          (println (.getMessage e)))))

    (doseq [n @found-ns]
      (println "Running tests in" n)
      (let [test-fns (filter test? (vals (ns-publics n)))]
        (print-reports
          (filter #(not= :pass (:status %))
            (mapcat (fn [t] (t)) test-fns)))))))

(run-tests)
