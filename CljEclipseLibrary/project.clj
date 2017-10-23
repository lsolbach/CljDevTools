(defproject org.soulspace.clj/CljEclipseLibrary "0.1.1"
  :description "The CljEclipseLibrary provides clojure abstractions for Eclipse project files"
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.soulspace.clj/CljXmlLibrary "0.4.2"]]
  :test-paths ["unittest"])