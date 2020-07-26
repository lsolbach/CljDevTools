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
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx])
  (:use [org.soulspace.clj.xml zip]
        [org.soulspace.tools.sonar common metrics-model]))

(defn metrics-api
  "Returns the URL for the metrics API."
  [sonar-base-url]
  (str sonar-base-url "api/metrics"))

(defn metrics-query-url
  "Returns the url for a metrics query."
  ([param-map]
   (str metrics-api "?" (query-parameters param-map)))
  ([param-map metric]
   (str metrics-api "/" metric "?" (query-parameters param-map))))

(defn get-metrics
  "Returns a sequence of the metrics matching the parameters."
  ([param-map]
   (parse-metrics (xml-zipper (metrics-query-url (merge param-map {:format :xml})))))
  ([param-map metric]
   (parse-metrics (xml-zipper (metrics-query-url (merge param-map {:format :xml}) metric)))))
