# What's this?

This is a library of character utility functions for Clojure, inspired by useful
built-in string and character libraries from other languages, most significantly 
Haskell's Data.Char library.

It is currently somewhat cumbersome to work with characters in Clojure. 
Complicating matters is the inherent complexity of dealing with supplementary 
characters in the JVM; Java characters are 16-bit, allowing characters in the 
Unicode range 0000-FFFF to be expressed as single characters. This range is 
called the Basic Multilingual Plane (BMP), however the range of existent 
characters has since expanding, bringing about the need for 32-bit characters. 
Java's way of representing these supplementary characters is via pairs of 16-bit 
characters, for a combined total of 32 bits. 

This library aims to provide convenient wrappers for standard Java Character 
library functions, as well as some new utility functions to facilitate working 
with characters.

Many of these functions are polymorphic in nature, by way of a single 
multimethod, `code-point-of`, which can take as an argument a character, an
integer representing a Unicode code point, or a string beginning with a 
supplementary character (i.e. two 16-bit Java characters). The focus in doing
this is ease of use by the end-user. 

Among the new utility functions is `char'` (on analogy with clojure.core's `+'`
and other "enhanced" arithmetic operators that support arbitrary precision), an
extension of `clojure.core/char` that will return a string containing a 
supplementary character if provided with a codepoint above U+FFFF, 
e.g. `(char' 135641)` => `𡇙` 

Another convenient function is `char-range`, which returns the range (inclusive)
between two characters, e.g. `(char-range \a \z)` => `(\a \b \c ... \x \y \z)`.
This provides a concise, readable syntax for representing ranges of characters,
as compared to, e.g., `(map char (range (int \a) (inc (int \z))))`. As a bonus,
this function also supports supplementary characters, as it uses `char'` 
internally.

My hope is that this library will end up in clojure.contrib or (my pipe dream)
as a part of Clojure proper as "clojure.char."

Feedback and suggests would be very welcome. I plan to start a discussion about
this on the Clojure dev Google group -- will post a link here once I do that.

Enjoy!

- Dave Yarwood, 10/8/14

# To do:

* Add a "mirror" function, which returns the mirror counterpart of a character
if it has one. 

* Write comprehensive, automated tests.

* Refactor to add pre-/post-conditions.
