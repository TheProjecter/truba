
(add-classpath (.toURL (java.io.File. "test")))

(use
  ['truba.build.command :only ['command]]
  ['truba.unittest])

(def tests
  ['neman.glob.test
   'neman.cli.test
   'neman.cli.desc.test
   'neman.main.test
   'truba.build.collector.test])

(command test
  :options
    [{:name "&verbose" :desc "Verbose output"}]

  [build {verbose? :verbose} _]
  #_(println verbose?)
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
