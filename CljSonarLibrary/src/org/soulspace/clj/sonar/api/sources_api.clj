;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.sonar.api.sources-api
  (:use [org.soulspace.clj.xml zip]
        [org.soulspace.clj.sonar.api common sources-model]))

(defn sources-api
  "Returns the URL for the sources API."
  [sonar-base-url]
  (str sonar-base-url "api/sources"))

(defn sources-query-url
  "Returns the url for a resource query."
  ([param-map]
   (str sources-api "?" (query-parameters param-map))))
