(ns djy.char-test
  (:require [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer (defspec)]
            [djy.char :as char]))

(def gen-bmp-char
  (gen/fmap char (gen/choose 0 65535)))

(def gen-stringified-bmp-char
  (gen/fmap str gen-bmp-char))

(def gen-high-surrogate
  (gen/fmap char (gen/choose 55296 56319)))

(def gen-low-surrogate
  (gen/fmap char (gen/choose 56320 57343)))

(def gen-supplementary-char
  (gen/fmap #(apply str %) (gen/tuple gen-high-surrogate gen-low-surrogate)))

(defspec code-point-of-an-integer-is-the-integer-itself
  (prop/for-all [code-point (gen/choose 0 1114111)]
    (= code-point
       (char/code-point-of code-point))))

(defspec code-point-of-a-bmp-character-is-its-code-point
  (prop/for-all [bmp-char gen-bmp-char]
    (= (int bmp-char)
       (char/code-point-of bmp-char))))

(defspec code-point-of-a-stringified-bmp-char-is-the-code-point-of-the-char
  (prop/for-all [str-bmp-char gen-stringified-bmp-char]
    (= (int (first str-bmp-char))
       (char/code-point-of str-bmp-char))))

(defspec code-point-of-a-supplementary-char-is-the-code-point-at-index-0
  (prop/for-all [supplementary-char gen-supplementary-char]
    (= (.codePointAt supplementary-char 0)
       (char/code-point-of supplementary-char))))
