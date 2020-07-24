;;
;;   Copyright (c) Ludger Solbach. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file license.txt at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
;;

(ns org.soulspace.tools.eclipse.test.classpath-test
  (:use [clojure.test]
        [org.soulspace.clj.xml marshalling zip]
        [org.soulspace.tools.eclipse classpath-dsl classpath-model])
  (:require [clojure.data.xml :as xml]))

(def xml1 (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    "<classpath>"
                    "<classpathentry kind=\"out\" path=\"bin\">"
                    "<attributes><attribute name=\"a\" value=\"1\"></attribute></attributes>"
                    "</classpathentry>"
                    "<classpathentry kind=\"src\" path=\"src\"></classpathentry>"
                    "</classpath>"))

(deftest to-xml-test
  (is (= xml1
         (xml/emit-str
           (to-xml (->Classpath
                     [(->Classpathentry "out" "bin" (->Attributes [(->Attribute "a" "1")]))
                      (->Classpathentry "src" "src" nil)]))))))

(deftest from-xml-test
  (is (= (->Classpath
           [(->Classpathentry "out" "bin" (->Attributes [(->Attribute "a" "1")]))
            (->Classpathentry "src" "src" nil)]))
      (from-xml
        (map->Classpath {})
        (xml-zipper xml1))))


(deftest unmarshalling-test
  (is (= (->Classpath
           [(->Classpathentry "out" "bin" (->Attributes [(->Attribute "a" "1")]))
            (->Classpathentry "src" "src" nil)]))
      (unmarshal-xml
        (xml-zipper xml1))))

