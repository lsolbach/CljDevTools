;;
;;   Copyright (c) Ludger Solbach. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file license.txt at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
;;

(ns org.soulspace.tools.git.porcelain
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [org.soulspace.clj.file :as f]
            [org.soulspace.clj.string :as sstr]
            [org.soulspace.clj.java.beans :as b]
            [org.soulspace.tools.git.api :as git])
  (:import [org.eclipse.jgit.api Git]
           [org.eclipse.jgit.storage.file FileRepositoryBuilder]))

(def ^{:dynamic true} *git*)

(defn fn-name
  "Converts the command name to a valid function name."
  [cmd]
  (str (str/lower-case (sstr/camel-case-to-hyphen cmd)) "-command"))

(defn method-name
  "Converts the command name to the java method name."
  [cmd]
  (str "." (sstr/first-lower-case cmd)))

(defn doc-name
  "Converts the command name to string name for the docs."
  [cmd]
  (sstr/from-camel-case \space cmd))

(defmacro defcommand
  "Defines a function that creates a git command."
  [cmd]
  (let [cmd-fn-name (symbol (fn-name cmd))
        cmd-method-name (symbol (method-name cmd))
        cmd-doc-name (doc-name cmd)]
    `(defn ~cmd-fn-name
       ~(str "Creates a " cmd-doc-name " command.")
       ([]
        (~cmd-fn-name *git*))
       ([^Git git#]
        (~cmd-method-name git#)))))

(defmacro defcommands
  "Defines functions for all the given git commands."
  [cmds]
  `(do ~@(map (fn [cmd] `(defcommand ~cmd)) cmds)))

(defcommands ["Add" "Apply" "Archive" "Blame" "BranchCreate" "BranchDelete" "BranchList" "BranchRename" "Checkout"
              "CherryPick" "Clean"
              ;"Clone"
              "Commit" "Describe" "Diff" "Fetch" "Gc"
              ;"Init"
              "Log"
              ;"LsRemote"
              "Merge" "NameRev" "NoteAdd" "NotesList" "NoteRemove" "NoteShow" "Pull" "Push" "Rebase" "Reflog"
              "Reset" "Revert" "Rm" "StashApply" "StashCreate" "StashDrop" "StashList" "Status"
              "SubmoduleAdd" "SubmoduleInit" "SubmoduleStatus" "SubmoduleSync" "SubmoduleUpdate"
              "Tag" "TagDelete" "TagList"])


(defn git-command-factory
  "Build a git context for the repository."
  [repository]
  (Git. repository))

(defn close
  "Closes the git context."
  [git]
  (.close git))

(defn wrap
  ""
  [git repo]
  (.wrap git repo))

(defn open
  ""
  ([file]
   (Git/open file))
  ([file fs]
   (Git/open file fs)))

(defn configure-command
  "Configures a git command with properties given as props and the items if given."
  ([cmd props]
   (b/set-properties! cmd props)
   cmd)
  ([cmd props items]
   (b/add-properties! (configure-command cmd props) items)
   cmd))

(defn call-command
  ""
  [command]
  (.call command))

(defn call
  ""
  ([f]
   (.call (f)))
  ([f props]
   (.call (configure-command (f) props)))
  ([f props items]
   (.call (configure-command (f) props items))))

(defn git-add
  "Add items to the repository."
  ([props]
   (call (add-command) props))
  ([props items]
   (call (add-command) props items)))

(defn git-apply
  "Apply"
  ([props]
   (call (apply-command) props))
  ([props items]
   (call (apply-command) props items)))

(defn git-archive
  "Archive."
  ([props]
   (call (archive-command) props))
  ([props items]
   (call (archive-command) props items)))

(defn git-blame
  "Blame."
  ([props]
   (call (blame-command) props))
  ([props items]
   (call (blame-command) props items)))

(defn git-branch-create
  "Create a branch."
  ([props]
   (call (branch-create-command) props))
  ([props items]
   (call (branch-create-command) props items)))

(defn clone-command
  "Creates a clone command."
  ([]
   (Git/cloneRepository))
  ([^Git git]
   (clone-command)))

(defn init-command
  "Creates an init command."
  ([]
   (Git/init))
  ([^Git git]
   (init-command)))

; TODO check: static call or *git* binding?
(defn ls-remote-command
  "Creates a ls remote command."
  ([]
   (Git/lsRemoteRepository))
  ([^Git git]
   (.lsRemote git)))

; ls-remote-repository

; TODO check use of open, (if open is used, provide a named :file-system fs parameter)
(defmacro with-git
  "Creates a git context for the repository denoted by file, executes the body and closes the git context."
  [file & body]
  `(binding [*git* (io/open (io/as-file ~file))]
    ~@body
    (io/close *git*)))
