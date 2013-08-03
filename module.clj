[
 :module "CljArtifactLibrary"
 :project "org.soulspace.clj"
 :type :library
 :version "0.4.3"
 :description "The CljArtifactLibrary contains abstractions and functions for the handling of artifacts."
 :plugins ["global" "dependencies" "clojure" "package"] ; "clojuretest"
 :dependencies [[["org.clojure" "clojure" "1.5.1"]]
                [["org.soulspace.clj" "CljLibrary" "0.4.0"]]
                [["org.soulspace.clj" "CljVersionLibrary" "0.4.3"]]]
 ]