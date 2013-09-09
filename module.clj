[
 :module "CljEclipseLibrary"
 :project "org.soulspace.clj"
 :type :library
 :version "0.1.0"
 :description "The CljEclipseLibrary provides clojure abstractions for Eclipse."
 :project-lead "Ludger Solbach"
 :provider "soulspace.org"
 :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
 :plugins ["global" "dependencies" "clojure" "clojuretest" "package" "release"]
 :dependencies [["org.clojure/clojure, 1.5.1"]
                ["org.clojure/data.zip, 0.1.1"]
                ["org.soulspace.clj/CljXmlLibrary, 0.3.0"]]
 ]
