;;
;;   Copyright (c) Ludger Solbach. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file license.txt at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
;;

(ns org.soulspace.tools.sonar.resources-api
  (:require [org.soulspace.xml.zip :as sxml]
            [org.soulspace.tools.sonar.common :as c]
            [org.soulspace.tools.sonar.resources-model :as rm]))

(defn resources-api
  "Returns the URL for the resources API."
  [sonar-base-url]
  (str sonar-base-url "api/resources"))

(defn resource-query-url
  "Returns the url for a resource query."
  ([param-map]
   (str resources-api "?" (c/query-parameters param-map))))

(defn get-resources
  "Returns a map of the resources matching the parameters."
  [param-map]
  (rm/parse-resources (sxml/xml-zipper (resource-query-url (merge param-map {:format :xml})))))
  