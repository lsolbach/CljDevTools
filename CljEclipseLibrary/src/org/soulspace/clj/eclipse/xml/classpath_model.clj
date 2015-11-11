;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.eclipse.xml.classpath-model
  (:use [org.soulspace.clj.xml.marshalling])
  (:require [org.soulspace.clj.eclipse.xml.classpath-dsl :as dsl]
            [clojure.data.xml :as xml]))

(defrecord Classpath
  [classpathentries]
    XMLMarshalling
    (to-xml [this]
      (dsl/classpath
        {}
        (if (seq classpathentries)
          (map to-xml classpathentries))))
    (from-xml [this xml]
      ))

(defrecord Classpathentry
  [^:attribute kind ^:attribute path attributes]
  XMLMarshalling
  (to-xml [this]
    (dsl/classpathentry 
      {:kind (:kind this) :path (:path this)}
      (dsl/attributes
        {}
        (if (seq attributes)
          (map (to-xml attributes))))))
  (from-xml [this xml]
    ))
    
(defrecord Attribute
  [name value]
  XMLMarshalling
  (to-xml [this]
    (dsl/attribute
      {:name (:name this) :value (:value this)}))
  (from-xml [this xml]
    ))