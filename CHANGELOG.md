# CHANGELOG

## 0.2.0 (2019-05-20)

* Added a couple of convenience functions, `category-int` and `category`, which
  return the category of a character.

  Thanks, @nibe, for the contribution!

## 0.1.4 (2015-03-21)

* @solicode noticed an opportunity for a minor performance boost by avoiding using sets/`contains?`. Now using his `in?` macro, which expands the collection of things we're checking to a series of checking each individual thing for equality to the thing we're looking for.

* Added type hints, with minimal performance gains.

## 0.1.3 (2015-02-08)

* Enhanced the usability of char-range by making it behave more like clojure.core/range.
* Added comprehensive tests and benchmarks.
