[![Clojars Project](http://clojars.org/djy/latest-version.svg)](http://clojars.org/djy)

# What's this?

This is a library of character utility functions for Clojure, inspired by useful
built-in string and character libraries from other languages, most significantly
Haskell's Data.Char library.

It is currently somewhat cumbersome to work with characters in Clojure.
Complicating matters is the inherent complexity of dealing with supplementary
characters in the JVM; Java characters are 16-bit, allowing characters in the
Unicode range 0000-FFFF to be expressed as single characters. This range is
called the Basic Multilingual Plane (BMP), however the range of existent
characters has since expanded, bringing about the need for 32-bit characters.
Java's way of representing these supplementary characters is via pairs of 16-bit
characters, for a combined total of 32 bits.

This library aims to provide convenient wrappers for standard Java Character
library functions, as well as some new utility functions to facilitate working
with characters.

Many of these functions are polymorphic in nature, by way of a `HasCodePoint` 
protocol exposing a `code-point-of` function, which can take as an argument a 
character, an integer representing a Unicode code point, or a string beginning 
with a supplementary character (i.e. two 16-bit Java characters). This allows
us to work with BMP and supplementary characters without having to think about
whether they are BMP or supplementary -- they're just characters™.

Among the new utility functions is `char'` (on analogy with clojure.core's `+'`
and other "enhanced" arithmetic operators that support arbitrary precision), an
extension of `clojure.core/char` that will return a string containing a
supplementary character if provided with a codepoint above U+FFFF,
e.g. `(char' 135641) => "𡇙"`"

Another convenient function is `char-range`, which returns the range (inclusive)
between two characters, e.g. `(char-range \a \z)` => `(\a \b \c ... \x \y \z)`.
This provides a concise, readable syntax for representing ranges of characters,
as compared to, e.g., `(map char (range (int \a) (inc (int \z))))`. As a bonus,
this function also supports supplementary characters, as it uses `char'`
internally.

My hope is that this library will end up in clojure.contrib or (my pipe dream)
as a part of Clojure proper as "clojure.char."

Any feedback and suggestions would be very welcome -- feel free to join the
[discussion](https://groups.google.com/forum/#!topic/clojure-dev/CVT5nqCz9XI)
going on the Clojure dev Google group.

Enjoy!

\- Dave Yarwood, 10/8/14

# To do:

* Write comprehensive, automated tests.
* Remedy potential performance issues caused by dynamic type introspection,
[as noted by Mikera](https://groups.google.com/d/msg/clojure-dev/CVT5nqCz9XI/8oKdlmbOYk4J).
