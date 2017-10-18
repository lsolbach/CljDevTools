(defproject org.soulspace.clj/CljSonarLibrary "0.1.2"
  :description "The CljSonarLibrary provides access to the Sonar web service API"
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.soulspace.clj/CljXmlLibrary "0.4.1"]]
  :test-paths ["unittest"])
