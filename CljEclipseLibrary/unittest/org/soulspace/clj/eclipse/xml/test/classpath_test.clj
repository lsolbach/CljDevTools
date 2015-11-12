(ns org.soulspace.clj.eclipse.xml.test.classpath-test
  (:use [clojure.test]
        [org.soulspace.clj.xml marshalling zip]
        [org.soulspace.clj.eclipse.xml classpath-dsl classpath-model])
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

