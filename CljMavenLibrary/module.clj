[
 :module "CljMavenLibrary"
 :project "org.soulspace.clj"
 :type :library
 :version "0.5.2"
 :description "The CljMavenLibrary contains abstractions and functions for maven POMs and metadata."
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]
           ["org.soulspace.baumeister/DistributionPlugin"]]
 :dependencies [["org.clojure/clojure, 1.7.0"]
                ["org.clojure/data.zip, 0.1.1"]
                ["org.soulspace.clj/CljXmlLibrary, 0.4.1"]
                ["org.soulspace.clj/CljApplicationLibrary, 0.5.2"]]
 ]
