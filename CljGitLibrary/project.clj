(defproject org.soulspace.clj/CljGitLibrary "0.1.1"
  :description "The CljGitLibrary provides a clojure api on JGit"
  :url "https://github.com/lsolbach/CljDevTools"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.eclipse.jgit/org.eclipse.jgit "3.2.0.201312181205-r"]
                 [org.soulspace.clj/CljJavaLibrary "0.6.1"]]
  :test-paths ["unittest"])
