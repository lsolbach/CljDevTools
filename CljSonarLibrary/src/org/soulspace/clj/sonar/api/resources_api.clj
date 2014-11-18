;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.sonar.api.resources-api
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx])
  (:use [org.soulspace.clj.xml zip]
        [org.soulspace.clj.sonar.api common resources-model]))

(defn resources-api
  "Returns the URL for the resources API."
  [sonar-base-url]
  (str sonar-base-url "api/resources"))

(defn resource-query-url
  "Returns the url for a resource query."
  ([param-map]
    (str resources-api "?" (query-parameters param-map))))

(defn get-resources
  "Returns a map of the resources matching the parameters."
  ([param-map]
    (parse-resources (xml-zipper (resource-query-url (merge param-map {:format :xml})))))
  ([release tier metrics]
    (parse-resources (xml-zipper (resource-query-url release tier metrics)))))
