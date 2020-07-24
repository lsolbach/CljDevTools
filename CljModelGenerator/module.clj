[
 :module "tools.codegen"
 :project "org.soulspace.clj"
 :type :framework
 :version "0.6.0"
 :description "The tools.codegen component is a generator framework for model driven development."
 :plugins [["org.soulspace.baumeister/ClojurePlugin, 0.6.5"]
           ["org.soulspace.baumeister/ClojureTestPlugin, 0.6.5"]
           ["org.soulspace.baumeister/PackagePlugin, 0.6.5"]]
 :dependencies [["org.clojure/clojure, 1.10.1"]
                ["org.soulspace.clj/clj.java, 0.8.0"]
                ["org.soulspace.template/TemplateEngine, 1.0.2"]
                ["org.soulspace.modelling/UML14ModelBuilder, 0.5.0"]]
 :log-level :debug]
