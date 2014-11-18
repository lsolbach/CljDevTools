;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.sonar.api.issues-api
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx])
  (:use [org.soulspace.clj.xml zip]
        [org.soulspace.clj.sonar.api common issues-model]))

(def severities [:BLOCKER :CRITICAL :MAJOR :MINOR :INFO])

(defn issue-search-api
  "Returns the URL for the issues API."
  [sonar-base-url]
  (str sonar-base-url "api/issues/search"))

(defn issue-query-url
  "Returns the url for an issue search query."
  ([param-map]
    (str issue-search-api "?" (query-parameters param-map))))

(defn component-to-file
  [component ext]
  (str (:source component) ext))

(defn last-issues-page?
  "Checks if these issues page is the last page of the query"
  [issues]
  (>= (:page-index (:paging issues)) (:pages (:paging issues))))

(defn append-issues-page
  "Appends a new issue page map to an issue map."
  [i1 i2]
  {:paging (:paging i2)
   :issues (concat (:issues i1) (:issues i2))
   :components (concat (:components i1) (:components i2))
   :projects (concat (:projects i1) (:projects i2))
   :rules (concat (:rules i1) (:rules i2))
   :users (concat (:users i1) (:users i2))})

(defn get-issues-page
  "Returns a map with the information of a page of issues."
  [param-map page]
  (parse-issues (xml-zipper (issue-query-url (assoc param-map :pageIndex page)))))

(defn get-issues
  "Returns a map of all issues matching the parameters. Sonar issue paging is handled transparently."
  [param-map]
  (loop [page 1
         issues (get-issues-page param-map page)]
    ;(println "Paging" (:pageIndex (:paging issues)) "/" (:pages (:paging issues)))
    (if (last-issues-page? issues)
      issues
      (recur (+ page 1)
             (append-issues-page issues (get-issues-page param-map (+ page 1)))))))

