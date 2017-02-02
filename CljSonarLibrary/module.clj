[
 :module "CljSonarLibrary"
 :project "org.soulspace.clj"
 :type :library
 :version "0.1.2"
 :description "The CljSonarLibrary provides access to the Sonar web service API."
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/ClojureTestPlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.clojure/clojure, 1.8.0"]
                ["org.clojure/data.zip, 0.1.1"]
                ["org.soulspace.clj/CljXmlLibrary, 0.4.1"]]
 ]
