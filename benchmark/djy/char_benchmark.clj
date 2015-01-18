(ns djy.char-benchmark
  (:require [criterium.core :refer :all]
            [djy.char :as char :refer (char')]))

(defn bmp-code-points
  "Generates n random code points in the BMP range."
  [n]
  (repeatedly n #(rand-int 65536)))

(defn supp-code-points
  "Generates n random code points in the supplementary range."
  [n]
  (let [supp-range (range 65536 1114112)]
    (repeatedly n #(rand-nth supp-range))))

(defn code-points
  "Generates n random code points."
  [n]
  (repeatedly n #(rand-int 1114112)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking conversion of ints to chars with char'...\n")

(let [cp (code-points 1000)]
  (quick-bench (doall (map char' cp)) :verbose))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking conversion of BMP ints to chars with char'...\n")

(let [cp (bmp-code-points 1000)]
  (quick-bench (doall (map char' cp)) :verbose))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking conversion of BMP ints to chars with clojure.core/char...\n")

(let [cp (bmp-code-points 1000)]
  (quick-bench (doall (map char cp)) :verbose))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking conversion of supplementary ints to chars with char'...\n")

(let [cp (supp-code-points 1000)]
  (quick-bench (doall (map char' cp)) :verbose))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(println "\nBenchmarking conversion of supplementary ints to chars with Java inter-op...\n")

(let [cp (supp-code-points 1000)]
  (quick-bench (doall (map #(apply str (Character/toChars %)) cp)) :verbose))
