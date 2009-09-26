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
               :url  "http://opensource.org/licenses/eclipse-1.0.php"}
     :apidoc
       ["Command line arguments parsing library for Clojure."
        [:sa "neman.cli.desc" "neman.main"]]}
  neman.cli
  (:require [neman.string :as str])
  (:use neman.ex))

;;
;; Help printing.
;;

(defn option-str [[[sn ln] {:keys [desc args]}] {:keys [width left-pad]}]
  (let [lp (or left-pad 3)
        w  (or width 79)
        nw (+ 30 lp)
        dw (- w nw 1)

        name-str (str/join " "
                   (str/join ", "
                             (when sn (str "-" sn))
                             (when ln (str "--" ln)))
                   (apply str/join (:sep args)
                          (map #(format "<%s>" %) (:name args))))
        name-str (str/right-pad
                   (str (str/repeat " " lp) name-str) nw)

        desc-str (map
                   #(str/right-pad % dw) (str/split-by-tokens desc dw))

        opt-str (conj (next desc-str)
                       (str name-str " " (first desc-str)))
        opt-str (map #(str/left-pad % w) opt-str)
        opt-str (apply str/join "\n" opt-str)]

    (str opt-str \newline)))

(defn group-str [[n {:keys [hidden options]}] formatting]
  (when-not hidden
    (str n ":\n" (apply str (map #(option-str % formatting) options)))))

(defn wrapped-str [text {:keys [left-pad width]}]
  (let [lp (or left-pad 0)
        tw (- (or width 79) lp)]
    (apply str/join "\n"
      (map #(str (str/repeat lp) %) (str/split-by-tokens text tw)))))

(defn help-str [{:keys [header footer options]} formatting]
  (let [wrap   #(wrapped-str % formatting)
        to-vec #(if (vector? %) % [%])]
    (str
      (when header
        (str
          (apply str/join "\n" (map wrap (to-vec header))) "\n\n"))

      (apply str/join "\n"
        (map #(group-str % formatting) options))

      (when footer
        (str
          (apply str (map wrap (to-vec footer))) "\n")))))

(defn usage-str [bin]
  (str "Usage: " bin))

;;
;; Parsing of command line arguments.
;;

(defn argument-name [[f s & r :as x]]
  (cond
    (and (= f \-) (= s \-) (seq r)) (apply str r)
    (and (= f \-) (= s \-))         "-"
    (= f \-)                        (str s)
    :else
      (throwf "Invalid argument name: %s" x)))

(defn pop-if [pred coll]
  (if (and (not (empty? coll)) (pred (peek coll)))
    (pop coll) coll))

(defn split-line* [argv]
  (reduce
    (fn [[[opt & args] & br :as blocks] [f s & r :as x]]
      (let [blocks (pop-if empty? blocks)]
        (cond
          ; Long option (e.g. --help)
          (and (= f \-) (= s \-))
            (conj blocks [x])

          ; Short option (e.g. -h)
          (and (= f \-) (= (count x) 2))
            (conj blocks [x])

          ; Short option but joined with it's parameter (e.g. -Dskip=true)
          (= f \-)
            (conj blocks [(str f s) (apply str r)] [])

          ; Parameter
          :else
            (conj br (vec (concat [opt] (conj (vec args) x)))))))
    '()
    argv))

(defn split-line [argv]
  (let [aseq (reverse
               (pop-if empty? (split-line* argv)))]
    (when (every? #(not= nil (first %)) aseq)
      aseq)))

(defn parse* [argv opts]
  (reduce
    (fn [[parsed unknown] [n & r :as x]]
      (let [n (argument-name n)
            {mfn :merge cfn :convert :as opt} (get opts n)]
        (if opt
          [(merge-with mfn parsed {n (cfn r)}) unknown]
          [parsed (conj unknown x)])))
    [{} []]
    (split-line argv)))

(defn options-map [opts]
  (into {}
    (apply concat (vals (into {} opts)))))

(defn expanded-options-map [opts-map]
  (into {}
    (mapcat (fn [[[k1 k2] v]] [[k1 v] [k2 v]]) opts-map)))

(defn names-map [opts]
  (into {}
    (mapcat
      (fn [[k1 k2]]
        [(when k1 [k1 (or k2 k1)]) (when k2 [k2 k2])])
      (keys opts))))

; XXX takes only :options part from options
(defn parse [argv opts]
  (let [opts  (options-map opts)
        names (names-map opts)
        [p u] (parse* argv (expanded-options-map opts))]
    [(into {}
       (map (fn [[k v]] [(keyword (get names k)) v]) p))
     (apply concat u)]))

(defn parse-all [argv opts]
  (let [[p u] (parse argv opts)]
    (if (seq u)
      (throwf "Faild to parse command line arguments: %" (str/join " " u))
      p)))
