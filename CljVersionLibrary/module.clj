[
 :module "CljVersionLibrary"
 :project "org.soulspace.clj"
 :type :library
 :version "0.4.3"
 :description "The CljVersionLibrary contains abstractions and functions for versioning."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/ClojureTestPlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.clojure/clojure, 1.5.1"]]
 ]