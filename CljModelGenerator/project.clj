(defproject org.soulspace.clj/CljModelGenerator "0.5.5"
  :description "Generator framework for model driven software development"
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.soulspace.clj/CljJavaLibrary "0.7.0"]
                 [org.soulspace.template/TemplateEngine "1.0.2"]
                 [org.soulspace.modelling/UML14ModelBuilder "0.5.0"]]
  :test-paths ["unittest"])
