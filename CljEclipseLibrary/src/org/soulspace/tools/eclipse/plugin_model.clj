;;
;;   Copyright (c) Ludger Solbach. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file license.txt at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
;;

(ns org.soulspace.tools.eclipse.plugin-model
  (:require [org.soulspace.clj.xml.marshalling :as m]
            [org.soulspace.tools.eclipse.plugin-dsl :as p]))

(defrecord Plugin
  [extension-point extension]
  XMLMarshalling
  (to-xml [this])

  XMLUnmarshalling
  (from-xml [this xml]))


(defrecord Fragment
  [extension-point-list extension-list]
  XMLMarshalling
  (to-xml [this])

  XMLUnmarshalling
  (from-xml [this xml]))


(defrecord ExtensionPoint
  [^:attribute name ^:attribute id ^:attribute schema]
  XMLMarshalling
  (to-xml [this])

  XMLUnmarshalling
  (from-xml [this xml]))


(defrecord Extension
  [^:attribute point ^:attribute id ^:attribute name]
  XMLMarshalling
  (to-xml [this])

  XMLUnmarshalling
  (from-xml [this xml]))
