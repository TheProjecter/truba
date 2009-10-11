
(require ['truba.ext.clojure :as 'clj])

(def test-dir
  (java.io.File. "test"))

(add-classpath (.toURL (java.io.File. "test")))

(use
  ['truba.build.command :only ['command]]
  ['truba.unittest])

(command test
  :options
    [{:name "&verbose" :desc "Verbose output"}]

  [build {verbose? :verbose} _]
  #_(println verbose?)
  (let [tests (map symbol (clj/namespaces-in-dirs (list test-dir)))
        found-ns (atom [])]
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
