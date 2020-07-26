[
 :module "tools.sonar"
 :project "org.soulspace.clj"
 :type :library
 :version "0.2.0"
 :description "The tools.sonar component provides a client for the SonarQube REST API."
 :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
 :scm-url "https://github.com/lsolbach/CljDevTools"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/ClojureTestPlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.clojure/clojure, 1.8.0"]
                ["org.clojure/data.zip, 0.1.1"]
                ["org.soulspace.clj/xml.core, 0.5.0"]
                ["org.soulspace.clj/xml.dsl, 0.5.0"]]]
