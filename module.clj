[
 :module "CljModelGenerator"
 :project "org.soulspace.clj"
 :type :framework
 :version "0.4.0"
 :description "Generator framework for model driven software development"
 :plugins ["global" "dependencies" "clojure" "clojuretest" "package"]
 :dependencies [[["org.clojure" "clojure" "1.5.1"]]
                [["org.soulspace.clj" "CljJavaLibrary" "0.2.0"]]
                [["org.soulspace.template" "TemplateEngine" "1.0.1"]]
                [["org.soulspace.modelling" "UML14ModelBuilder" "0.3.0"]]]
 :log-level :info
 ]
