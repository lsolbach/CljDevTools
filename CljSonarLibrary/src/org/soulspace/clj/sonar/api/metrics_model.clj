;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.sonar.api.metrics-model
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx]))


(defn parse-metric
  "Returns a map with the data of a metric."
  [zipper]
  {:key (zx/xml1-> zipper :key zx/text)
   :name (zx/xml1-> zipper :name zx/text)
   :description (zx/xml1-> zipper :description zx/text)
   :domain (zx/xml1-> zipper :domain zx/text)
   :qualitative (zx/xml1-> zipper :qualitative zx/text)
   :direction (zx/xml1-> zipper :direction zx/text)
   :user-managed (zx/xml1-> zipper :user_managed zx/text)
   :val-type (zx/xml1-> zipper :val_type zx/text)
   :hidden (zx/xml1-> zipper :hidden zx/text)})

(defn parse-metrics
  [zipper]
  "Returns a map with the data of a metrics response."
  (map parse-metric (zx/xml-> zipper :metric)))
