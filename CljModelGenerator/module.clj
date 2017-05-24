[
 :module "CljModelGenerator"
 :project "org.soulspace.clj"
 :type :framework
 :version "0.5.4"
 :description "Generator framework for model driven software development"
 :plugins [["org.soulspace.baumeister/ClojurePlugin, 0.6.5"]
           ["org.soulspace.baumeister/ClojureTestPlugin, 0.6.5"]
           ["org.soulspace.baumeister/PackagePlugin, 0.6.5"]]
 :dependencies [["org.clojure/clojure, 1.8.0"]
                ["org.soulspace.clj/CljJavaLibrary, 0.6.1"]
                ["org.soulspace.template/TemplateEngine, 1.0.2"]
                ["org.soulspace.modelling/UML14ModelBuilder, 0.5.0"]]
 :log-level :debug
 ]
