;;
;;   Copyright (c) Ludger Solbach. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file license.txt at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
;;

(ns org.soulspace.tools.git.api
  (:require [clojure.java.io :as io]
            [org.soulspace.clj.java.beans :as b])
  (:import [org.eclipse.jgit.revwalk RevWalk]
           [org.eclipse.jgit.storage.file FileRepositoryBuilder]))

(defn build-file-repository
  "Builds a file repository for the path."
  [path]
  (let [b (FileRepositoryBuilder.)]
    (.setGitDir b (io/as-file path))
    (.readEnvironment b)
    (.findGitDir b)
    (.build b)))

(defn rev-walk
  "Creates a rev walk."
  [repo]
  (RevWalk. repo))

(defn rev-commit
  "Returns the commit with the given id on the rev walk."
  [walk id]
  (.parseCommit walk id))

(defn rev-tag
  "Returns the tag with the given id on the rev walk."
  [walk id]
  (.parseTag walk id))

(defn rev-tree
  "Returns the tree with the given id on the rev walk."
  [walk id]
  (.parseTree walk id))
