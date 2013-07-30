[
 :module "CljModelGenerator"
 :project "org.soulspace.clj"
 :type :framework
 :version "0.4.0"
 :description "Generator framework for model driven software development"
 :plugins ["global" "sdeps" "clojure" "package"]
 :dependencies [[["org.clojure" "clojure" "1.5.1"]]
                [["org.soulspace.clj" "CljLibrary" "0.3.0"]]
                [["org.soulspace.clj" "CljJavaLibrary" "0.2.0"]]
                [["oro" "oro" "2.0.8"]]
                [["org.soulspace.template" "TemplateEngine" "1.0.1"]]
                [["org.soulspace.modelling" "UML14Repository" "0.3.0"]]
                [["org.soulspace.modelling" "ModelRepository2" "0.3.0"]]
                [["org.soulspace.modelling" "UML14ModelBuilder" "0.3.0"]]]
 :log-level :info
 ]
