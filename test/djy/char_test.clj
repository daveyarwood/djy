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

(def gen-char
  (gen/fmap char/char' (gen/choose 0 1114111)))

(def gen-hebrew-char
  (gen/fmap char (gen/choose 1424 1535)))

(def gen-cherokee-char
  (gen/fmap char (gen/choose 5024 5119)))

(def gen-upper-case-letter
  (gen/fmap char (gen/choose (int \A) (int \Z))))

(def gen-lower-case-letter
  (gen/fmap char (gen/choose (int \a) (int \z))))

;;; testing code-point-of

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

;;; testing char'

(defspec code-points-0-65535-are-chars
  (prop/for-all [bmp-code-point (gen/choose 0 65535)]
    (char? (char/char' bmp-code-point))))

(defspec code-points-65536-1114111-are-strings
  (prop/for-all [supplementary-code-point (gen/choose 65536 1114111)]
    (string? (char/char' supplementary-code-point))))

(defspec code-point-of-and-char'-are-inverse-operations
  (prop/for-all [code-point (gen/choose 0 1114111)]
    (= code-point (char/code-point-of (char/char' code-point)))))

;;; testing char comparator functions

(defspec char-less-than-or-equal-to
  (prop/for-all [characters (gen/such-that not-empty (gen/vector gen-char))]
    (apply char/char<= (sort-by char/code-point-of characters))))

(defspec char-less-than
  (prop/for-all [characters (gen/such-that #(and (not-empty %)
                                            (= (count %) (count (distinct %))))
                              (gen/vector gen-char))]
    (apply char/char< (sort-by char/code-point-of characters))))

;;; testing char/prev

(defspec char-prev
  (prop/for-all [characters (gen/bind
                              (gen/such-that #(> (char/code-point-of %) 0)
                                gen-char)
                              #(gen/tuple
                                 (gen/return %)
                                 (gen/return (char/prev %))))]
    (= (char/code-point-of (first characters))
       (inc (char/code-point-of (second characters))))))

;;; testing char-range

(def gen-some-chars
  "A subset of characters spanning the BMP and supplementary ranges.
   Using this instead of gen-char so the test won't spend an eternity
   trying to build a huge character range if the characters happen to
   be far apart (which is very probable)."
  (gen/fmap char/char' (gen/choose 50000 70000)))

(defspec char-range-is-like-range-for-characters
  (prop/for-all [[a b] (gen/such-that #(>= (count %) 2)
                         (gen/vector gen-some-chars))]
    (= (range (char/code-point-of a) (inc (char/code-point-of b)))
       (map char/code-point-of (char/char-range a b)))))

;;; testing char-seq

(defspec char-seq-provides-accurate-character-counts
  (prop/for-all [characters (gen/vector gen-char)]
    (= (count characters)
       (count (char/char-seq (apply str characters))))))

;;; testing surrogates

(defspec surrogates-constitute-a-character
  (prop/for-all [character gen-char]
    (= (str character)
       (apply str (char/surrogates character)))))

;;; testing unicode-block-of

(defspec unicode-block-of-alphanumeric-chars-is-basic-latin
  (prop/for-all [alphanumeric-char gen/char-alphanumeric]
    (= (char/unicode-block-of alphanumeric-char) "BASIC_LATIN")))

(defspec unicode-block-of-hebrew-chars-is-hebrew
  (prop/for-all [hebrew-char gen-hebrew-char]
    (= (char/unicode-block-of hebrew-char) "HEBREW")))

(defspec unicode-block-of-cherokee-chars-is-cherokee
  (prop/for-all [cherokee-char gen-cherokee-char]
    (= (char/unicode-block-of cherokee-char) "CHEROKEE")))

(defspec all-chars-belong-to-a-unicode-block
  (prop/for-all [character gen-char]
    (string? (char/unicode-block-of character))))

;;; testing category

(defspec category-lower-case-letter
  (prop/for-all [lower-case-letter gen-lower-case-letter]
    (= (char/category lower-case-letter)
       "Ll")))

(defspec category-upper-case-letter
  (prop/for-all [upper-case-letter gen-upper-case-letter]
    (= (char/category upper-case-letter)
       "Lu")))

;;; testing boolean functions

(defspec defined?
  (prop/for-all [character gen-char]
    (contains? #{true false} (char/defined? character))))

(defspec bmp?
  (prop/for-all [bmp-char gen-bmp-char]
    (char/bmp? bmp-char)))

(defspec supplementary?
  (prop/for-all [supplementary-char gen-supplementary-char]
    (char/supplementary? supplementary-char)))

(defspec high-surrogate?
  (prop/for-all [high-surrogate gen-high-surrogate]
    (char/high-surrogate? high-surrogate)))

(defspec low-surrogate?
  (prop/for-all [low-surrogate gen-low-surrogate]
    (char/low-surrogate? low-surrogate)))

(defspec surrogate?
  (prop/for-all [surrogate (gen/one-of [gen-high-surrogate gen-low-surrogate])]
    (char/surrogate? surrogate)))

(defspec letter-or-digit?
  (prop/for-all [character gen/char-alphanumeric]
    (char/letter-or-digit? character)))

;;; testing case conversion functions

(defspec lower-case
  (prop/for-all [upper-case-letter gen-upper-case-letter]
    (= (char/lower-case upper-case-letter)
       (Character/toLowerCase upper-case-letter))))

(defspec upper-case
  (prop/for-all [lower-case-letter gen-lower-case-letter]
    (= (char/upper-case lower-case-letter)
       (Character/toUpperCase lower-case-letter))))
