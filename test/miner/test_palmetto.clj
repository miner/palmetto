(ns miner.test-palmetto
  (:require [clojure.test :refer :all]
            [miner.palmetto :refer :all]))

(deftest show-info
  (testing "Show test info"
    (println)
    (println "  ** Test Info **")
    (println "  Palmetto" (nth (clojure.edn/read-string (slurp  "project.clj")) 2))
    (println "  Clojure" (clojure-version))
    (println)
    true))
