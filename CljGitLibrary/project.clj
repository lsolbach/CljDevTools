(defproject org.soulspace.clj/tools.git "0.2.0"
  :description "The tools.git component provides a Clojure api for JGit."
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.eclipse.jgit/org.eclipse.jgit "3.2.0.201312181205-r"]
                 [org.soulspace.clj/clj.java "0.8.0"]]
  :test-paths ["unittest"])
