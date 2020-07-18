[
 :module "CljMavenLibrary"
 :project "org.soulspace.clj"
 :type :library
 :version "0.5.3"
 :description "The CljMavenLibrary contains abstractions and functions for maven POMs and metadata"
 :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
 :scm-url "https://github.com/lsolbach/CljDevTools"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]
           ["org.soulspace.baumeister/DistributionPlugin"]]
 :dependencies [["org.clojure/clojure, 1.8.0"]
                ["org.clojure/data.zip, 0.1.1"]
                ["org.soulspace.clj/CljXmlLibrary, 0.4.3"]
                ["org.soulspace.clj/CljApplicationLibrary, 0.6.0"]]]
