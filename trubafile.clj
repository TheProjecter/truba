(require ['truba.ext.clojure :as 'clj])

(add-classpath (.toURL (File. "test")))
(use
  ['truba.unittest])

(load
  "/truba/ext/unittest")

(task :Test #{} [:TestDir :ClojureTestNamespaces] [test-dir test-ns]
  (let [found-ns (atom [])]
    (doseq [t test-ns]
      (try
        (require t)
        (swap! found-ns t)

        (catch Exception e
          (println "Error while loading" t (.getMessage e))))

    (doseq [n @found-ns]
      (println "Running tests in" n)
      (let [test-fns (filter test? (vals (ns-publics n)))]
        (print-reports
          (filter #(not= :pass (:status %))
            (mapcat (fn [t] (t)) test-fns))))))))

(command test
  :options
    [{:name "&verbose" :desc "Verbose output"}]

  [build {verbose? :verbose} _]
  (let [props (:properties build)
        tests (apply concat
                (map
                  (fn [[k v]]
                    (when (= (:type k) :ClojureTestNamespaces)
                      v))
                  props))
        found-ns (atom [])]

    (doseq [t tests]
      (try
        (require t)
        (swap! found-ns conj t)

        (catch Exception e
          (println "Error while loading" t (.getMessage e)))))

    (doseq [n @found-ns]
      (println "Running tests in" n)
      (let [test-fns (filter test? (vals (ns-publics n)))]
        (print-reports
          (filter #(not= :pass (:status %))
            (mapcat (fn [t] (t)) test-fns)))))))

(use ['truba.project :only ['project]])
#_(project truba
  "Truba project management system for Clojure."

  (command test
    :desc
      "Run unit tests."
    :options
      [{:name "&verbose" :desc "Verbose output"}]

    [build {verbose? :verbose} _]
    (println "Test!")))
