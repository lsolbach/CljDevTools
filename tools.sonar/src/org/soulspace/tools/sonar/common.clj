;;
;;   Copyright (c) Ludger Solbach. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file license.txt at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
;;

(ns org.soulspace.tools.sonar.common
  (:use [clojure.string :only [join split]]))

(def xml-query-map {:format :xml}) ; base query map for xml formatted results

(defn query-parameter
  "Generates a query parameter from a key/value pair."
  [kv]
  (let [[k v] kv]
    (cond
      (or (keyword? v) (string? v)) (str (name k) "=" (name v))
      (number? v) (str (name k) "=" v)
      (sequential? v) (str (name k) "=" (join "," (map name v))))))

(defn query-parameters
  "Generates a query parameter string from the parameter map."
  [param-map]
  (join "&" (map query-parameter (seq param-map))))

