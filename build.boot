(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.6.0"]
                  [adzerk/bootlaces "0.1.8" :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.1")
(bootlaces! +version+)

(task-options!
  pom {:project 'djy
       :version +version+
       :description "A library of character utility functions for Clojure"
       :url "https://github.com/daveyarwood/djy"
       :scm {:url "https://github.com/daveyarwood/djy"}
       :license {:name "Eclipse Public License"
                 :url "http://www.eclipse.org/legal/epl-v10.html"}})
