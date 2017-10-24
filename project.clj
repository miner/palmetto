(defproject com.velisco/palmetto "0.1.0-SNAPSHOT"
  :description "Palmetto"
  :min-lein-version "2.0"
  :dependencies [[org.clojure/clojure "1.9.0-beta2"]
                 [org.clojure/data.csv "0.1.4"]
                 [enlive "1.1.6"]]
  :profiles {:dev {:dependencies [[criterium "0.4.4"]]}
             :snapshot {:dependencies [[org.clojure/clojure "1.9.0-master-SNAPSHOT"]]}
             }
  :global-vars {*warn-on-reflection* true}
  ;; :repl-options {:init-ns miner.palmetto.repl}
  :url "https://github.com/miner/palmetto"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"} )




