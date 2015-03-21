# 0.1.4

* @solicode noticed an opportunity for a minor performance boost by avoiding using sets/`contains?`. Now using his `in?` macro, which expands the collection of things we're checking to a series of checking each individual thing for equality to the thing we're looking for.

* Added type hints, with minimal performance gains.

# 0.1.3

* Enhanced the usability of char-range by making it behave more like clojure.core/range.
* Added comprehensive tests and benchmarks.
