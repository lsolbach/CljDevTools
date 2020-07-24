[
 :module "tools.maven"
 :project "org.soulspace.clj"
 :type :library
 :version "0.6.0"
 :description "The tools.maven library contains code to process Maven POMs and metadata."
 :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
 :scm-url "https://github.com/lsolbach/CljDevTools"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]
           ["org.soulspace.baumeister/DistributionPlugin"]]
 :dependencies [["org.clojure/clojure, 1.10.1"]
                ["org.clojure/data.zip, 0.1.1"]
                ["org.soulspace.clj/clj.base, 0.8.0"]
                ["org.soulspace.clj/xml.core, 0.5.0"]
                ["org.soulspace.clj/xml.dsl, 0.5.0"]]]
