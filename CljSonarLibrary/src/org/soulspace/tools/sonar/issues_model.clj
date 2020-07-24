;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.tools.sonar.issues-model
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx])
  (:use [clojure.string :only [split]]
        [org.soulspace.clj.string :only [parse-number]]))

(defn parse-issue-component
  "Parses the component of an issue into a map."
  [component]
  (let [[group-id artifact-id branch source] (split component #"[:]")]
    {:group-id group-id
     :artifact-id artifact-id
     :branch branch
     :source source}))

(defn parse-paging
  [zipper]
  "Returns a map with the paging data of the issues page."
  {:page-index (parse-number (zx/xml1-> zipper :pageIndex zx/text))
   :page-size (parse-number (zx/xml1-> zipper :pageSize zx/text))
   :total (parse-number (zx/xml1-> zipper :total zx/text))
   :pages (parse-number (zx/xml1-> zipper :pages zx/text))})

(defn parse-comment
  [zipper]
  "Returns a map with the data of a comment."
  {:key (zx/xml1-> zipper :key zx/text)
   :login (zx/xml1-> zipper :login zx/text)
   :html-text (zx/xml1-> zipper :htmlText zx/text)
   :createdAt (zx/xml1-> zipper :createdAt zx/text)})

(defn parse-issue
  "Returns a map with the data of an issue."
  [zipper]
  {:key (zx/xml1-> zipper :key zx/text)
   :component (parse-issue-component (zx/xml1-> zipper :component zx/text))
   :project (zx/xml1-> zipper :project zx/text)
   :rule (zx/xml1-> zipper :rule zx/text)
   :status (zx/xml1-> zipper :status zx/text)
   :resolution (zx/xml1-> zipper :resolution zx/text)
   :severity (zx/xml1-> zipper :severity zx/text)
   :message (zx/xml1-> zipper :message zx/text)
   :line (zx/xml1-> zipper :line zx/text)
   :creation-date (zx/xml1-> zipper :creationDate zx/text)
   :update-date (zx/xml1-> zipper :updateDate zx/text)
   :comments (map parse-comment (zx/xml-> zipper :comments :comment))})

(defn parse-component
  "Returns a map with the data of a component."
  [zipper]
  {:key (zx/xml1-> zipper :key zx/text)
   :qualifier (zx/xml1-> zipper :qualifier zx/text)
   :name (zx/xml1-> zipper :name zx/text)
   :long-name (zx/xml1-> zipper :longName zx/text)})

(defn parse-project
  "Returns a map with the data of a project."
  [zipper]
  {:key (zx/xml1-> zipper :key zx/text)
   :qualifier (zx/xml1-> zipper :qualifier zx/text)
   :name (zx/xml1-> zipper :name zx/text)
   :long-name (zx/xml1-> zipper :longName zx/text)})

(defn parse-rule
  "Returns a map with the data of a rule."
  [zipper]
  {:key (zx/xml1-> zipper :key zx/text)
   :name (zx/xml1-> zipper :name zx/text)
   :desc (zx/xml1-> zipper :desc zx/text)
   :status (zx/xml1-> zipper :status zx/text)})

(defn parse-user
  "Returns a map with the data of a user."
  [zipper]
  {:login (zx/xml1-> zipper :login zx/text)
   :name (zx/xml1-> zipper :name zx/text)
   :active (zx/xml1-> zipper :active zx/text)
   :email (zx/xml1-> zipper :email zx/text)})

(defn parse-issues
  "Returns a map with the data of an issue page."
  [zipper]
  {:security-exclusions (zx/xml1-> zipper :securityExclusions zx/text)
   :max-results-reached (zx/xml1-> zipper :maxResultsReached zx/text)
   :paging (parse-paging (zx/xml1-> zipper :paging))
   :issues (map parse-issue (zx/xml-> zipper :issues :issue))
   :components (map parse-component (zx/xml-> zipper :components :component))
   :projects (map parse-project (zx/xml-> zipper :projects :project))
   :rules (map parse-rule (zx/xml-> zipper :rules :rule))
   :users (map parse-user (zx/xml-> zipper :users :user))})

