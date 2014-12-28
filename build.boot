(set-env!
 :source-paths #{"src" "test"}
 :dependencies '[[org.clojure/clojure "1.6.0"]
                 [adzerk/bootlaces "0.1.8" :scope "test"]
                 [adzerk/boot-test "1.0.3" :scope "test"]])

(require '[adzerk.bootlaces :refer :all]
         '[adzerk.boot-test :refer :all])

(def +version+ "0.1.2")
(bootlaces! +version+)

(task-options!
  pom {:project 'djy
       :version +version+
       :description "A library of character utility functions for Clojure"
       :url "https://github.com/daveyarwood/djy"
       :scm {:url "https://github.com/daveyarwood/djy"}
       :license {:name "Eclipse Public License"
                 :url "http://www.eclipse.org/legal/epl-v10.html"}}
  test {:namespaces '#{djy.char-test}})
