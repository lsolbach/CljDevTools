(defproject org.soulspace.clj/tools.sonar "0.2.0"
  :description "The tools.sonar component provides a client for the SonarQube REST API."
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.soulspace.clj/xml.core "0.5.0"]
                 [org.soulspace.clj/xml.dsl "0.5.0"]]
  :test-paths ["unittest"])
