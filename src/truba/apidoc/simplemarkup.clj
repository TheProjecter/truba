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
  truba.apidoc.simplemarkup)

(def markup-re
  (re-pattern
    (str
      "(?:(.*?)"
      "(?:"
      "(?:(?<!\\w)\\*([^\\*]+)\\*(?=\\W))|"       ; strong   (e.g. *text*)
      "(?:(?<!\\w)_([^_]+)_(?=\\W))|"             ; emphasis (e.g. _text_)
      "(?:(?<!\\w)\\?([^\\?]+)\\?(?=\\W))|"       ; citation (e.g. ?citation?)
      "(?:(?<!\\w)-([^-]+)-(?=\\W))|"             ; deleted  (e.g. -text-)
      "(?:(?<!\\w)\\+([^\\+]+)\\+(?=\\W))|"       ; inserted (e.g. +text+)
      "(?:\\^([^\\^]+)\\^(?=\\W))|"               ; superscript (e.g. ^sup^)
      "(?:~([^~]+)~(?=\\W))|"                     ; subscript (e.g. ~sub~)
      "(?:(?<!\\w)(?:\\[([^\\]]*)\\]:([^\\s]*)))" ; urls (e.g. [Clojure]:http://clojure.org)
      "))"
      "|(.*)")))

(defn parse* [s]
  (mapcat
    (fn [[_ & xs]]
      (let [t (cond
                (not (empty? (nth xs 0))) (nth xs 0)
                (not (empty? (nth xs 10))) (nth xs 10))
            x (cond
                (nth xs 1) [:strong (nth xs 1)]
                (nth xs 2) [:emp    (nth xs 2)]
                (nth xs 3) [:cite   (nth xs 3)]
                (nth xs 4) [:del    (nth xs 4)]
                (nth xs 5) [:ins    (nth xs 5)]
                (nth xs 6) [:sup    (nth xs 6)]
                (nth xs 7) [:sub    (nth xs 7)]
                (nth xs 8) [:a      (nth xs 8) (nth xs 9)])]
        (cond
          (and t x) [t x], t [t], x [x])))
    (re-seq markup-re s)))

(defn parse [s]
  (let [res (parse* (str " " s " "))

        ; Strip leading space
        [n & m] res
        res (cond
              (= " " n)   m
              (string? n) (.substring n 1)
              :else       res)

        ; Strip trailing space
        [n m] [(butlast res) (last res)]
        res (cond
              (= " " m)   n
              (string? m) (.substring m 0 (dec (count m)))
              :else       res)])
   res)
