(defproject om-canvas "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.cemerick/pprng "0.0.3-SNAPSHOT"]
                 [om "0.7.1"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "om-canvas"
              :source-paths ["src"]
              :compiler {
                :output-to "om_canvas.js"
                :output-dir "out"
                :libs [""]
                :optimizations :none
                :source-map true}}]})
