(defproject org.soulspace.clj/CljMavenLibrary "0.5.3"
  :description "The CljMavenLibrary contains abstractions and functions for maven POMs and metadata"
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.soulspace.clj/CljXmlLibrary "0.4.3"]
                 [org.soulspace.clj/CljApplicationLibrary "0.6.0"]]
  :test-paths ["unittest"])
