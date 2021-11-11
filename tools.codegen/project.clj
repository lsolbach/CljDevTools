(defproject org.soulspace.clj/tools.codegen "0.6.0"
  :description "The tools.codegen component is a generator framework for Model Driven Development."
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.soulspace.clj/clj.java "0.8.0"]
                 [org.soulspace.template/TemplateEngine "1.0.2"]
                 [org.soulspace.modelling/UML14ModelBuilder "0.5.0"]]
  :test-paths ["unittest"])
