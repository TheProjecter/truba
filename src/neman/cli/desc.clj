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
       ["Easy to use options descriptions for neman.cli."
        [:sa "neman.cli" "neman.main"]]}
 neman.cli.desc
 (:use neman.ex))

(defn to-option-name
  {:apidoc
     ["Convert option name description string to pair of [short long]
      option names."

      "Valid option name strings:"
      [:table
        ["h"     "Gets converted to [\"h\" nil]."]
        ["help"  "Gets converted to [nil \"help\"]"]
        ["&help" "Gets converted to [\"h\" \"help\"]"]
        ["&Help" "Gets converted to [\"H\" \"help\"]"]]

      [:arg "x" "Input string"]
      [:res "Pair of short and long option names."]]}
  [x]
  (cond
    (re-find #"[^a-zA-Z&]" x)
      (throwf "Invalid name description: %s " x)

    (vector? x)
      x

    (and (string? x) (= 1 (count x)))
      [x nil]

    (string? x)
      [(second (re-find #"&(.)" x)) (.toLowerCase (.replace x "&" ""))]

    :else
      (throwf "Unknown name description: %s" x)))

(def arg-re
  #"(?:<([a-z-^>]+)>([:=,;]?+)(?:<([a-z-]+)>)?+([*+]?+))")

(defn split-args* [x]
  (let [[res p1 sep p2 num] (re-matches arg-re x)
        p   (remove nil? [p1 p2])
        sep (when-not (empty? sep)
              sep)
        num (if (empty? p)
              0
              (or (first num) 1))]
    (cond
      (not res)
        (throwf "Invalid arguments syntax, failed to parse: %s" x)

      (and (= 2 (count p)) (or (= num \+) (= num \*)))
        (throwf "Can't use '*' and '+' with pair properties: %s" x)

      :else
        [p sep num])))

(def default-args
  {:name '() :sep nil :num 0})

(defn split-args [x]
  (if (empty? x)
    default-args
    (zipmap [:name :sep :num] (split-args* x))))

(defn to-option-args
  {:apidoc
    ["Convert option argument description string to form required by neman.cli"

     "Valid separators for arguments are ':', '=', ',' and ';'."
     [:table
       ["<arg>"   "Option takes one argument"]
       ["<arg>*"  "Option takes any number of arguments"]
       ["<arg>+"  "Option takes one or more arguments"]
       ["<arg>:*" "Option takes any number of arguments separated with ':'"]
       ["<arg>=<val>" "Option takes pair of arguments separated with '='"]]

     [:arg "x" "Argument description string."]
     [:res "Argument map constructed from description string."]]}
  [x]
  (cond
    (string? x) (split-args x)
    (vector? x) (merge (split-args (first x)) (apply hash-map (next x)))
    :else       default-args))

(defn create-convert-fn* [{{:keys [name sep num]} :args}]
  (fn [[f & r :as xs]]
    (let [cn (count name)]
      (cond
        (and (= 0 cn) (= 0 num))
          (if (empty? xs)
            true
            (throwf "Too many arguments: %s" xs))

        (and sep (= 1 cn) (= \* num))
          (seq (.split f (str sep)))

        (and (= 1 cn) (= \+ num))
          (if-not (empty? xs)
            xs
            (throwf "Missing arguments."))

        (and (= 1 cn) (= 1 num))
          (if-not (or (not f) (seq r))
            f
            (throwf "Invalid number of arguments: %s" xs))

        (and sep (= 2 cn))
          (let [[k v] (.split f (str sep))]
            (if (and k v (empty? (seq r)))
              {(keyword k) v}
              (throwf "Invalid arguments: %s" xs)))

        :else
          xs))))

(defn create-convert-fn [{cfn :convert :as specs}]
  (assoc specs
    :convert (or cfn (create-convert-fn* specs))))

(defn create-merge-fn* [{{:keys [num name]} :args}]
  (condp = num
    0  second
    1  (condp = (count name)
         1 second
         2 merge)
    \+ concat
    \* concat))

(defn create-merge-fn [{mfn :merge :as specs}]
  (assoc specs
    :merge (or mfn (create-merge-fn* specs))))

; XXX how will this one go?
(defn create-post-fn [{pfm :post-parse :as specs}]
  (assoc specs :post-parse
    (or pfm
        identity)))

(def create-option-fns
  (comp create-convert-fn create-merge-fn create-post-fn))

(defn create-option [{:keys [name args] :as specs}]
  [(to-option-name name)
   (-> specs
     (assoc :args (to-option-args args)) (dissoc :name) create-option-fns)])

;;
;; Prepare user specs.
;;

(defn convert-options [specs]
  (reverse
    (reduce
      (fn [[[f s] & r :as gs] x]
        (if-not (map? x)
          (conj gs [x []])
          (conj r [f (conj (or s []) (create-option x))])))
      []
      specs)))

(defn convert-specs [specs]
  (update-in specs [:options] convert-options))
