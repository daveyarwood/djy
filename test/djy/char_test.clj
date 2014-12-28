(ns djy.char-test
  (:require [clojure.test :refer :all]
            [djy.char :as char]))

(defn- rand-range
  "Generates a random integer between two integers x and y, exclusive."
  [x y]
  (+ (rand-int (- y x)) x))

(defn- random-supplementary-char
  "Generates a string containing a random supplementary character (U+10000-U+10FFFF)."
  []
  (let [high-surrogate (char (rand-range 55296 56320))
        low-surrogate  (char (rand-range 56320 57344))]
    (str high-surrogate low-surrogate)))

(deftest input-test
  (testing "code-point-of"
    (testing "with code points from 0000-10FFFF (0-1114111)"
      (let [code-points (repeatedly 100 #(rand-range 0 1114112))]
        (is (= code-points (map char/code-point-of code-points)))))
    (testing "with BMP character literals (0-65535)"
      (let [code-points (repeatedly 100 #(rand-range 0 65536))
            bmp-chars   (map char code-points)]
        (is (= code-points (map char/code-point-of bmp-chars)))))
    (testing "with BMP characters in string form"
      (let [code-points (repeatedly 100 #(rand-range 0 65536))
            bmp-strings (map (comp str char) code-points)]
        (is (= code-points (map char/code-point-of bmp-strings)))))
    (testing "with supplementary characters (as strings)"
      (let [supplementary-chars (repeatedly 100 #(random-supplementary-char))]
        (is (= (map #(.codePointAt % 0) supplementary-chars)
               (map char/code-point-of supplementary-chars)))))))
