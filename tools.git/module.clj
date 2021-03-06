[
 :module "tools.git"
 :project "org.soulspace.clj"
 :type :library
 :version "0.2.0"
 :description "The tools.git component provides a Clojure api for JGit."
 :project-lead "Ludger Solbach"
 :provider "soulspace.org"
 :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
 :scm-url "https://github.com/lsolbach/CljDevTools"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/ClojureTestPlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.clojure/clojure, 1.8.0"]
                ["org.eclipse.jgit/org.eclipse.jgit, 3.2.0.201312181205-r"]
                ["org.soulspace.clj/clj.java, 0.8.0"]]]
