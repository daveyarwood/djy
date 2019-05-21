(set-env!
 :source-paths #{"src" "test" "benchmark"}
 :resource-paths #{"benchmark"}
 :dependencies '[[org.clojure/clojure "1.6.0"]
                 [org.clojure/test.check "0.7.0" :scope "test"]
                 [adzerk/bootlaces "0.1.11" :scope "test"]
                 [adzerk/boot-test "1.0.4" :scope "test"]
                 [criterium "0.4.3" :scope "test"]])

(require '[adzerk.bootlaces :refer :all]
         '[adzerk.boot-test :refer :all])

(def +version+ "0.2.0")
(bootlaces! +version+)

(deftask bench
  "Run benchmarks.
  (This just runs all of the namespaces -- Criterium does all the heavy lifting.)"
  [n namespaces NAMESPACE #{sym} "Symbols of the namespaces to benchmark."]
  (with-pre-wrap fileset
    (doseq [ns namespaces] (require ns))
    fileset))

(task-options!
  pom {:project 'djy
       :version +version+
       :description "A library of character utility functions for Clojure"
       :url "https://github.com/daveyarwood/djy"
       :scm {:url "https://github.com/daveyarwood/djy"}
       :license {"name" "Eclipse Public License"
                 "url" "http://www.eclipse.org/legal/epl-v10.html"}}
  test  {:namespaces '#{djy.char-test}}
  bench {:namespaces '#{djy.char-benchmark djy.char-range-benchmark
                        djy.text-processing-benchmark}})
