;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.sonar.api.resources-model
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx])
  )

(defn parse-msr
  "Returns a map with the metrics data of a resource."
  [zipper]
  {:key (zx/xml1-> zipper :key zx/text)
   :val (zx/xml1-> zipper :val zx/text)
   :frmt_val (zx/xml1-> zipper :frmt_val zx/text)})

(defn parse-resource
  "Returns a map with the data of a resource."
  [zipper]
  {:id (zx/xml1-> zipper :id zx/text)
   :key (zx/xml1-> zipper :key zx/text)
   :name (zx/xml1-> zipper :name zx/text)
   :lname (zx/xml1-> zipper :lname zx/text)
   :branch (zx/xml1-> zipper :branch zx/text)
   :scope (zx/xml1-> zipper :scope zx/text)
   :qualifier (zx/xml1-> zipper :qualifier zx/text)
   :lang (zx/xml1-> zipper :lang zx/text)
   :version (zx/xml1-> zipper :version zx/text)
   :date (zx/xml1-> zipper :date zx/text)
   :msr (map parse-msr (zx/xml-> zipper :msr))})

(defn parse-resources
  "Returns a map with the data of a resources response."
  [zipper]
  (map parse-resource (zx/xml-> zipper :resource)))

