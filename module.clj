[
 :module "CljMavenLibrary"
 :project "org.soulspace.clj"
 :type :library
 :version "0.5.1"
 :description "The CljMavenLibrary contains abstractions and functions for maven POMs and metadata."
 :plugins ["global" "dependencies" "clojure" "clojuretest" "package"]
 :dependencies [["org.clojure/clojure, 1.5.1"]
                ["org.clojure/data.zip, 0.1.1"]
                ["org.soulspace.clj/CljXmlLibrary, 0.3.0"]
                ["org.soulspace.clj/CljApplicationLibrary, 0.5.1"]
                ]
 ]
