[
 :module "CljArtifactLibrary"
 :project "org.soulspace.clj"
 :type :library
 :version "0.4.4"
 :description "The CljArtifactLibrary contains abstractions and functions for the handling of artifacts."
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/ClojureTestPlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.clojure/clojure, 1.7.0"]
                ["org.soulspace.clj/CljVersionLibrary, 0.4.4"]
                ["org.soulspace.clj/CljLibrary, 0.6.1"]]
 ]