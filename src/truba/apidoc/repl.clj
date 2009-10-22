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
  truba.apidoc.repl
  (:refer-clojure :exclude [doc print-namespace-doc print-doc])
  (:import (clojure.lang Fn MultiFn))
  (:require [neman.string :as str])
  (:use [clojure.set :only [map-invert]]))

(defn- ns-str [n]
  (when n
    (-> n ns-name str)))

(defn- ns-name-parts [n]
  (str/split (ns-str n) "\\."))

(defn- ns-base [n]
  (let [[f s & r] (ns-name-parts n)]
    (if (some #{f} ["com" "org" "net"]) (str f s) f)))

(defn- ns-common-base? [n1 n2]
  (= (ns-base n1) (ns-base n2)))

(defn- ns-comp [n1 n2]
  (.compareTo (ns-str n1) (ns-str n2)))

(defn- ns-sort [ns-seq]
  (sort ns-comp ns-seq))

(defn ns-groups [ns-seq]
  (reduce
    (fn [[f & r :as xs] n]
      (if (ns-common-base? (first f) n)
        (conj r (conj f n))
        (conj xs [n])))
    []
    (ns-sort ns-seq)))

(defn term [s]
  (str (char 27) "[" s "m"))

(defn ns-doc [nspace]
  (let [m (meta nspace)]
    (or (:doc m) (:apidoc m))))

(defn print-namespace-doc [nspace]
  (let [m (meta nspace)
        doc (or (:doc m)
                (:apidoc m))]
    (print
      (str
        (format "Namespace: %s\n" (ns-str nspace))
        (term "2") doc (term "1") "\n"))))

(defn print-namespace-overview []
  (println "Currently loaded namespaces:\n")

  (let [am (map-invert (ns-aliases *ns*))]
    (doseq [gs (ns-groups (all-ns))]
      (doseq [n gs]
        (let [ndoc (or (ns-doc n) "** no description available **")]
          (print
            (str
              (cond
                (am n)     (format " %s (Alias: %s)\n" (ns-str n) (name (am n)))
                (= n *ns*) (format " %s (*ns*)\n" (ns-str n))
                :else      (format " %s\n" (ns-str n)))
              (format "   %s\n" (str (term "2") ndoc (term "1")))))))
      (println))))

(defn- var-type [v]
  (cond
    (:macro ^v)
      "Macro"
    (= (:tag ^v) MultiFn)
      "Multimethod"
    (isa? (class (var-get v)) Fn)
      "Function"
    :else
      "Var"))

; XXX show arglists formatted
(defn print-doc [v]
  (let [{:keys [ns name arglists doc apidoc macro]} ^v]
    (printf "%s: %s/%s\n%s\n%s\n"
            (var-type v) (ns-str ns) name arglists (or doc apidoc))))

(defmacro doc
  ([]
   `(print-namespace-overview))

  ([name]
    (if (or (special-form-anchor `~name) (syntax-symbol-anchor `~name))
      `(clojure.core/doc ~name)
      (if-let [nspace (find-ns name)]
        `(print-namespace-doc ~nspace)
        `(print-doc (var ~name))))))
