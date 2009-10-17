
(require ['truba.ext.clojure :as 'clj])

(use ['truba.build.property :only ['property 'calc-all]])

(add-classpath (.toURL (java.io.File. "test")))

(use
  ['truba.build.command :only ['command]]
  ['truba.unittest])

(property :TestDir
  (java.io.File. "test"))

(property :ClojureTestFiles [:TestDir] [test-dirs]
  (clj/clojure-files test-dirs))

(property :ClojureTestNamespaces [:ClojureTestFiles] [test-files]
  (filter (complement nil?)
    (mapcat clj/ns-decl (apply concat test-files))))

(command test
  :options
    [{:name "&verbose" :desc "Verbose output"}]

  [build {verbose? :verbose} _]
  (let [props (calc-all (:properties build))
        tests (apply concat
                (map
                  (fn [[k v]]
                    (when (= (:type k) :ClojureTestNamespaces)
                      v))
                  props))
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
