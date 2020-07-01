;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.eclipse.xml.project-model
  (:use [org.soulspace.clj.xml marshalling zip])
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx]
            [org.soulspace.clj.eclipse.xml.project-dsl :as dsl]))

(defrecord ProjectDescription
  [name comment projects build-spec natures]
  XMLMarshalling
  (to-xml [this]
    (dsl/project-description
        {}
      (to-xml name)
      (to-xml comment)
      (dsl/projects
       (if (seq projects)
           (map to-xml projects)))
      (dsl/build-spec
        (if (seq build-spec)
          (map to-xml build-spec)))
      (dsl/natures
          (if (seq natures)
            (map to-xml projects))))))


(defrecord Name
  [content]
  XMLMarshalling
  (to-xml [this]
    (dsl/name {} content)))


(defrecord Project
  []
  XMLMarshalling
  (to-xml [this]))



(defrecord BuildCommand
  [name arguments]
  XMLMarshalling
  (to-xml [this]))



(defrecord Nature
  [content]
  XMLMarshalling
  (to-xml [this]
    (dsl/nature {} content)))


;
; unmarshal XML with multi function, which dispatches on the tag keyword
;
(defmulti unmarshal-xml current-zipper-tag)
