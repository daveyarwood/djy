(ns djy.text-processing-benchmark
  (:require [criterium.core :refer :all]
            [djy.char :as char :refer (char-seq code-point-of)]
            [clojure.java.io :as io]))

(def i18nguy
  "Source of a website containing 124 supplementary characters.
   Roughly 15,000 total characters in length."
  (slurp "http://www.i18nguy.com/unicode/supplementary-test.html"))

(def bmp-chars-string
  (slurp (io/resource "sample/bmp-chars.txt")))

(def supplementary-chars-string
  (slurp (io/resource "sample/supplementary-chars.txt")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println (str "\nBenchmarking char-seq on the source of\n"
              "http://www.i18nguy.com/unicode/supplementary-test.html...\n"))

(quick-bench (doall (char-seq i18nguy)) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking clojure.core/seq on the same text...\n")

(quick-bench (doall (seq i18nguy)) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking code-point-of on the same text...\n")

(quick-bench (doall (map code-point-of i18nguy)) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking char-seq on a string of all BMP characters...\n")

(quick-bench (doall (char-seq bmp-chars-string)) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking clojure.core/seq on the same text...\n")

(quick-bench (doall (seq bmp-chars-string)) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking code-point-of on the same text...\n")

(quick-bench (doall (map code-point-of bmp-chars-string)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println (str "\nBenchmarking char-seq on a string of the first 65,536 "
              "supplementary characters...\n"))

(quick-bench (doall (char-seq supplementary-chars-string)) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking clojure.core/seq on the same text...\n")

(quick-bench (doall (seq supplementary-chars-string)) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking code-point-of on the same text...\n")

(quick-bench (doall (map code-point-of supplementary-chars-string)) :verbose)
