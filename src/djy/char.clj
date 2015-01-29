(ns ^{:doc "A library of character-related utility functions for Clojure.

        Note: There are several functions in this library that may collide with other
        namespaces. Recommended usage is:
            (ns your.namespace
              (:require [djy.char :as char :refer (...)]))

        Many of these functions are polymorphic in that they can accept as an argument
        either a character, an integer representing a Unicode code point, or a string.
        If the argument is a string, the function uses the Unicode code point found at
        index 0 of the string, and the rest of the string (if the string happens to be
        longer than one Unicode code point) is ignored, e.g.:

            (code-point-of '\ud800\udc00') ;=> 65536
            (code-point-of 'hello')        ;=> 104 (the code point of 'h')
            [imagine that the single quotes above are double quotes]

        When dealing with supplementary Unicode characters with code points higher than
        U+FFFF, the character must be represented either as a String (e.g. '\ud800\udc00')
        or as a code point (e.g. 65536). This is due to the inherent limitations of 16-
        bit Java characters and the fact that Clojure character literals are built on top
        of them.

        Glossary:

        * Code Point: any value in the Unicode codespace; that is, the range of integers
             from 0 to 10FFFF (in decimal: 1114111). Not all code points are assigned to
             encoded characters.
        * Basic Multilingual Plane (BMP): Unicode characters in the range 0000-FFFF.
        * Supplementary characters: Unicode characters with code points > FFFF. Java
              represents these characters as a string containing two 16-bit characters,
              a high-surrogate and a low-surrogate (in that order).
        * High-surrogates: characters in the range D800-DBFF
        * Low-surrogates: characters in the range DC00-DFFF
        * ASCII: characters in the range 0000-007F (0-127)
        * Latin-1: characters in the range 0080-00FF (0-255)"
      :author "Dave Yarwood"}
  djy.char
  (:require [clojure.string :as str])
  (:refer-clojure :exclude [next symbol?]))

;;; Utility functions ;;;

(defprotocol HasCodePoint
  (code-point-of [this] "Returns the Unicode code point of a character."))

(extend-protocol HasCodePoint
  java.lang.Number
    (code-point-of [n] {:pre [(<= 0 n 1114111)]} n)
  java.lang.Character
    (code-point-of [ch] (int ch))
  java.lang.String
    (code-point-of [^String s] {:pre [(not (empty? s))]} (.codePointAt s 0)))

(defn char'
  "Like clojure.core/char, returns the character at a given code point.

   Whereas char will throw an error if the code point is greater than 65535 (U+FFFF),
   char' will return the supplemental character (in string form) at that code point."
  {:added "1.6"}
  [n]
  {:pre [(<= 0 n 1114111)]}
  (try
    (char n)
    (catch IllegalArgumentException e
      (apply str (Character/toChars n)))))

(defn char<
  "Like clojure.core/<, but it converts its arguments (which are each expected to be
   a character, a code point, or a string containing a supplementary character) to code
   points. Useful for determining whether a collection of characters is in order by code
   point."
  {:added "1.6"}
  [& chs]
  (apply < (map code-point-of chs)))

(defn char>
  "Like clojure.core/>, but it converts its arguments (which are each expected to be
   a character, a code point, or a string containing a supplementary character) to code
   points. Useful for determining whether a collection of characters is in order by code
   point."
  {:added "1.6"}
  [& chs]
  (apply > (map code-point-of chs)))

(defn char<=
  "Like clojure.core/<=, but it converts its arguments (which are each expected to be
   a character, a code point, or a string containing a supplementary character) to code
   points. Useful for determining whether a collection of characters is in order by code
   point."
  {:added "1.6"}
  [& chs]
  (apply <= (map code-point-of chs)))

(defn char>=
  "Like clojure.core/<=, but it converts its arguments (which are each expected to be
   a character, a code point, or a string containing a supplementary character) to code
   points. Useful for determining whether a collection of characters is in order by code
   point."
  {:added "1.6"}
  [& chs]
  (apply >= (map code-point-of chs)))

(defn prev
  "Returns the character before the one given, i.e. the character whose code point is one
   less than the one given. Returns a string if the resulting character is supplementary."
  {:added "1.6"}
  [ch]
  (char' (dec (code-point-of ch))))

(defn next
  "Returns the character after the one given, i.e. the character whose code point is one
   greater than the one given. Returns a string if the resulting character is supplementary."
  {:added "1.6"}
  [ch]
  (char' (inc (code-point-of ch))))

(defn char-range
  "Given two characters or code points, returns the range (inclusive) between them.
   e.g. (char-range a z) => (a b c d e ... x y z) [imagine these are all characters]
   Represents supplementary characters as strings.

   As this function uses clojure.core/range internally, the result is a lazy seq.

   Emulates the behavior of clojure.core/range when given different numbers of
   arguments. For example, an optional third argument can be provided as the step
   (defaults to 1)."
  {:added "1.6"}
  ([]
    (cons (char 0) (lazy-seq (map (comp char' inc) (range)))))
  ([end]
    (char-range 0 end 1))
  ([start end]
    (char-range start end 1))
  ([start end step]
    (map char' (range (code-point-of start) (inc (code-point-of end)) step))))

(declare supplementary?)

(defn char-seq
  "Generates a lazy seq of characters from a string, appropriately handling
   supplementary characters by representing them as strings. Equivalent to (seq s),
   except that supplementary characters are represented in string form rather than
   broken up into their surrogate characters."
  {:added "1.6"}
  [s]
  {:pre [(string? s)]}
  (when (seq s)
    (let [supplementary? (supplementary? (code-point-of s))]
      (cons (if supplementary? (subs s 0 2) (first s))
            (lazy-seq (char-seq (subs s (if supplementary? 2 1))))))))

(defn surrogates
  "Returns a seq containing the surrogate pair (high, low) for a supplementary character
   (in string form) or code point. If given a BMP character or code point, returns a seq
   containing just that character."
  {:added "1.6"}
  [ch]
  (seq (Character/toChars (code-point-of ch))))

(defn unicode-block-of
  "Returns the name of the Unicode block for the character or code point."
  {:added "1.6"}
  [ch]
  (str (java.lang.Character$UnicodeBlock/of (code-point-of ch))))

(comment
  "This function requires JDK >= 1.7"
  (defn name
    "Returns the Unicode name of the character or code point, or nil if the code point is
     unassigned."
    {:added "1.6"}
    [ch]
    (Character/getName (code-point-of ch)))
)

;;; Boolean functions ;;;

(defn defined?
  "Determines whether a character or code point is defined in Unicode, i.e. it has an entry
   in the UnicodeData file, or has a value in a range defined by the UnicodeData file."
  {:added "1.6"}
  [ch]
  (Character/isDefined (code-point-of ch)))

(defn bmp?
  "Determines whether a character or code point is in the Basic Multilingual Plane (BMP),
   which includes characters U+0000 through U+FFFF."
  {:added "1.6"}
  [ch]
  (<= 0 (code-point-of ch) 65535))

(defn supplementary?
  "Determines whether a character or code point is a supplementary character,
   i.e. code point in the range U+10000 through U+10FFFF."
  {:added "1.6"}
  [ch]
  (<= 65536 (code-point-of ch) 1114111))

(defn high-surrogate?
  "Determines whether a character or code point is in the high-surrogates range,
   i.e. U+D800 through U+DBFF."
  {:added "1.6"}
  [ch]
  (<= 55296 (code-point-of ch) 56319))

(defn low-surrogate?
  "Determines whether a character or code point is in the low-surrogates range,
   i.e. U+DC00 through U+DFFF."
  {:added "1.6"}
  [ch]
  (<= 56320 (code-point-of ch) 57343))

(defn surrogate?
  "Determines whether or not a character/code point is a high- or low-surrogate."
  {:added "1.6"}
  [ch]
  (let [cp (code-point-of ch)]
    (or
      (<= 55296 cp 56319)
      (<= 56320 cp 57343))))

(defn ascii?
  "Determines whether a character or code point is an ASCII character."
  {:added "1.6"}
  [ch]
  (<= 0 (code-point-of ch) 127))

(defn latin-1?
  "Determines whether a character or code point is in the Latin-1 charset."
  {:added "1.6"}
  [ch]
  (<= 0 (code-point-of ch) 255))

(defn control?
  "Determines whether a character or code point is an ISO control character."
  {:added "1.6"}
  [ch]
  (let [cp (code-point-of ch)]
    (or
      (<= 0 cp 31)
      (<= 127 cp 159))))

(comment
  "This function requires JDK >= 1.7"
  (defn alphabetic?
    "Determines whether a character or code point is Alphabetic, as defined by the
     Unicode standard."
    {:added "1.6"}
    [ch]
    (Character/isAlphabetic (code-point-of ch)))
)

(defn letter?
  "Determines whether a character or code point is a letter."
  {:added "1.6"}
  [ch]
  (Character/isLetter (code-point-of ch)))

(defn digit?
  "Determines whether a character or code point is a digit. A character is a digit if its
   general category type is DECIMAL_DIGIT_NUMBER; this includes many ranges of characters
   including ISO-LATIN-1 digits, full-width digits, Devanagari digits, etc."
  {:added "1.6"}
  [ch]
  (Character/isDigit (code-point-of ch)))

(defn letter-or-digit?
  "Determines whether a character or code point is a letter or digit."
  {:added "1.6"}
  [ch]
  (let [cp (code-point-of ch)]
    (or (Character/isLetter cp) (Character/isDigit cp))))

(defn oct-digit?
  "Determines whether a character or code point is an octal digit (0-7).
   Returns true only for ISO-LATIN-1 digits."
  {:added "1.6"}
  [ch]
  (<= 48 (code-point-of ch) 55))

(defn hex-digit?
  "Determines whether a character or code point is a hexadecimal digit.
   Returns true only for ISO-LATIN-1 characters."
  {:added "1.6"}
  [ch]
  (let [cp (code-point-of ch)]
    (or
      (<= 48 cp 57)     ; 0-9
      (<= 65 cp 70)     ; A-F
      (<= 97 cp 102)))) ; a-f

(comment
  "This function requires JDK >= 1.7"
  (defn ideograph?
    "Determines whether a character or code point is a CKJV ideograph."
    {:added "1.6"}
    [ch]
    (Character/isIdeographic (code-point-of ch)))
)

(defn whitespace?
  "Determines whether a character or code point is a Java whitespace character."
  {:added "1.6"}
  [ch]
  (Character/isWhitespace (code-point-of ch)))

(defn punctuation?
  "Determines whether a character or code point is a punctuation character, according to
   the Unicode standard."
  {:added "1.6"}
  [ch]
  (contains? #{Character/CONNECTOR_PUNCTUATION, Character/DASH_PUNCTUATION,
               Character/START_PUNCTUATION, Character/END_PUNCTUATION,
               Character/INITIAL_QUOTE_PUNCTUATION, Character/FINAL_QUOTE_PUNCTUATION,
               Character/OTHER_PUNCTUATION}
             (Character/getType (code-point-of ch))))

(defn mark?
  "Determines whether a character or code point is a mark character, according to the
   Unicode standard."
  {:added "1.6"}
  [ch]
  (contains? #{Character/COMBINING_SPACING_MARK, Character/ENCLOSING_MARK,
               Character/NON_SPACING_MARK}
             (Character/getType (code-point-of ch))))

(defn symbol?
  "Determines whether a character or code point is a symbol character, according to the
   Unicode standard."
  {:added "1.6"}
  [ch]
  (contains? #{Character/MATH_SYMBOL, Character/CURRENCY_SYMBOL,
               Character/MODIFIER_SYMBOL, Character/OTHER_SYMBOL}
             (Character/getType (code-point-of ch))))

(defn separator?
  "Determines whether a character or code point is a separator, according to the Unicode
   standard."
  {:added "1.6"}
  [ch]
  (contains? #{Character/LINE_SEPARATOR, Character/PARAGRAPH_SEPARATOR,
               Character/SPACE_SEPARATOR}
             (Character/getType (code-point-of ch))))


(defn lower-case?
  "Determines whether a character or code point is a lower-case letter."
  {:added "1.6"}
  [ch]
  (Character/isLowerCase (code-point-of ch)))

(defn upper-case?
  "Determines whether a character or code point is an upper-case letter."
  {:added "1.6"}
  [ch]
  (Character/isUpperCase (code-point-of ch)))

(defn title-case?
  "Determines whether a character or code point is a title-case letter."
  {:added "1.6"}
  [ch]
  (Character/isTitleCase (code-point-of ch)))

;;; Case conversion functions ;;;

(defn lower-case
  "Converts a character to its lower-case counterpart, if it has one.
   Expects its argument to be a BMP character or code point."
  {:added "1.6"}
  [ch]
  {:pre [(or (char? ch) (number? ch))]}
  (Character/toLowerCase ch))

(defn upper-case
  "Converts a character to its upper-case counterpart, if it has one.
   Expects its argument to be a BMP character or code point."
  {:added "1.6"}
  [ch]
  {:pre [(or (char? ch) (number? ch))]}
  (Character/toUpperCase ch))

(defn title-case
  "Converts a character to its lower-case counterpart, if it has one.
   Expects its argument to be a BMP character or code point."
  {:added "1.6"}
  [ch]
  {:pre [(or (char? ch) (number? ch))]}
  (Character/toTitleCase ch))
