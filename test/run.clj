;; Copyright (c) Krešimir Šojat, 2009. All rights reserved. The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution. By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license. You must not
;; remove this notice, or any other, from this software.

(require 'clojure.test)

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

    (apply clojure.test/run-tests @found-ns)))

(run-tests)
