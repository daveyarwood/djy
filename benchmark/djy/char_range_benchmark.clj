(ns djy.char-range-benchmark
  (:require [criterium.core :refer :all]
            [djy.char :as char :refer (char-range)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking char-range for BMP characters...\n")

(quick-bench (doall (char-range 10000 60000)) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking mapping clojure.core/char over the same range...\n")

(quick-bench (doall (map char (range 10000 60001))) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking char-range for supplementary characters...\n")

(quick-bench (doall (char-range 100000 150000)) :verbose)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking the equivalent Java interop for the same range...\n")

(quick-bench (doall (map #(apply str (Character/toChars %))
                         (range 100000 150000)))
             :verbose)
