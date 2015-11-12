(ns org.soulspace.clj.eclipse.xml.test.classpath-test
;  (:import [org.soulspace.clj.eclipse.xml.classpath-model Classpath Classpathentry Attributes Attribute])
  (:use [clojure.test]
        [org.soulspace.clj.xml marshalling]
        [org.soulspace.clj.eclipse.xml.classpath-model]
        [org.soulspace.clj.eclipse.xml.classpath-dsl])
  (:require [clojure.data.xml :as xml]))

(deftest marshalling-test
  (is (= (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
              "<classpath>"
              "<classpathentry kind=\"out\" path=\"bin\"><attributes><attribute name=\"a\" value=\"1\"></attribute></attributes></classpathentry>"
              "<classpathentry kind=\"src\" path=\"src\"></classpathentry>"
              "</classpath>")
         (xml/emit-str
           (to-xml (->Classpath
                     [(->Classpathentry "out" "bin" (->Attributes [(->Attribute "a" "1")]))
                      (->Classpathentry "src" "src" nil)]))))))