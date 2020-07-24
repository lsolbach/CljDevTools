(defproject org.soulspace.clj/CljMavenLibrary "0.5.3"
  :description "The tools.maven library contains code to process Maven POMs and metadata."
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.soulspace.clj/clj.base "0.8.0"]
                 [org.soulspace.clj/xml.core "0.5.0"]
                 [org.soulspace.clj/xml.dsl "0.5.0"]]
  :test-paths ["unittest"])
