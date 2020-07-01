;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.modelgenerator.protected-areas
  (:use [clojure.java.io]))

; TODO dump protected areas as clojure map per generated file in a checked in dir
; TODO  so that the generated filed has not to be checked in

(defn begin-pattern [area-marker]
  "Returns the regex pattern for the begin of a protected area based on the area marker."
  (re-pattern (str "^.*" area-marker "-BEGIN\\((.*)\\).*$")))

(defn end-pattern [area-marker area-id]
  "Returns the regex pattern for the end of a protected area based on the area marker and area id."
  (re-pattern (str "^.*" area-marker "-END\\(" area-id "\\).*$")))

(defn read-lines
  "Reads the file given and returns a non lazy sequence a of its lines."
  ([file]
   (with-open [rdr (reader file)]
     (doall (line-seq (reader))))))

; TODO line separator is encoding dependent!?!
(defn parse-protected-areas [area-marker lines]
  "Parse the lines into a protected area map."
  (let [begin-re (begin-pattern area-marker)]
    (loop [remaining-lines lines area-id nil area-content "" area-map {}]
      (if (seq remaining-lines)
        (if (nil? area-id)
          (if-let [begin-matches (re-seq begin-re (first remaining-lines))]
            (recur (rest remaining-lines) (nth (first begin-matches) 1) "" area-map) ; line starting a protected area
            (recur (rest remaining-lines) nil "" area-map)) ; line outside any protected areas
          (if-let [end-match (re-matches (end-pattern area-marker area-id) (first remaining-lines))]
            (recur (rest remaining-lines) nil "" (assoc area-map area-id area-content)) ; line ending a protected area
            (recur (rest remaining-lines) area-id (str area-content (first remaining-lines) "\n") area-map))) ; line inside a protected area
        area-map)))) ; no more lines, return area map

(defn read-protected-areas [gen path]
  "Reads the given path and returns the proected areas as a map."
  (if-let [area-marker (:protectedArea gen)]
    (parse-protected-areas area-marker (read-lines path))))
