;;
;;   Copyright (c) Ludger Solbach. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file license.txt at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
;;

(ns org.soulspace.tools.sonar.metrics-api
  (:require [org.soulspace.xml.zip :as sxml]
            [org.soulspace.tools.sonar.common :as c]
            [org.soulspace.tools.sonar.metrics-model :as mm]))

(defn metrics-api
  "Returns the URL for the metrics API."
  [sonar-base-url]
  (str sonar-base-url "api/metrics"))

(defn metrics-query-url
  "Returns the url for a metrics query."
  ([param-map]
   (str metrics-api "?" (c/query-parameters param-map)))
  ([param-map metric]
   (str metrics-api "/" metric "?" (c/query-parameters param-map))))

(defn get-metrics
  "Returns a sequence of the metrics matching the parameters."
  ([param-map]
   (mm/parse-metrics (sxml/xml-zipper (metrics-query-url (merge param-map {:format :xml})))))
  ([param-map metric]
   (mm/parse-metrics (sxml/xml-zipper (metrics-query-url (merge param-map {:format :xml}) metric)))))
