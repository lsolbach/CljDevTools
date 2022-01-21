(defproject org.soulspace.clj/tools.codegen "0.6.0"
  :description "The tools.codegen component is a generator framework for Model Driven Development."
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  ; use deps.edn dependencies
  :plugins [[lein-tools-deps "0.4.5"]]
  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]
  :lein-tools-deps/config {:config-files [:install :user :project]}

  :test-paths ["test"]
  :deploy-repositories [["clojars" {:sign-releases false :url "https://clojars.org/repo"}]])
