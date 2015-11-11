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
  (:use [org.soulspace.clj.xml marshalling])
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
            (map to-xml projects)))))
    (from-xml [this xml]
      ; FIXME check zipping here
      (let [name (zx/xml1-> xml :name zx/text)
            comment (zx/xml1-> xml :comment zx/text)
            projects (zx/xml1-> xml :projects zx/text)
            build-spec (zx/xml1-> xml :buildSpec zx/text)
            natures (zx/xml1-> xml :natures zx/text)]
        (ProjectDescription. name comment projects build-spec natures))
      ))

(defrecord Name
  [content]
  XMLMarshalling
  (to-xml [this]
    (dsl/name {} content))
  (from-xml [this xml]
    ))

(defrecord Project
  []
  XMLMarshalling
  (to-xml [this]
    )
  (from-xml [this xml]
    ))

(defrecord BuildCommand
  [name arguments]
  XMLMarshalling
  (to-xml [this]
    )
  (from-xml [this xml]
    ))

(defrecord Nature
  [content]
  XMLMarshalling
  (to-xml [this]
    (dsl/nature {} content))
  (from-xml [this xml]
    ))

