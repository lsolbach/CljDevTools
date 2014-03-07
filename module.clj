[
 :module "CljModelGenerator"
 :project "org.soulspace.clj"
 :type :framework
 :version "0.5.0"
 :description "Generator framework for model driven software development"
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/ClojureTestPlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.clojure/clojure, 1.5.1"]
                ["org.soulspace.clj/CljJavaLibrary, 0.5.0"]
                ["org.soulspace.template/TemplateEngine, 1.0.2"]
                ["org.soulspace.modelling/UML14ModelBuilder, 0.5.0"]]
 :log-level :debug
 ]
