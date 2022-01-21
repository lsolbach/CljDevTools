(ns org.soulspace.tools.maven-xml
  (:require [clojure.java.io :as io]
            [clojure.data.xml :as xml]
            [org.soulspace.tools.maven-xml-parser :as mvn]))

;;;
;;; 
;;;
(defn parse-xml
  "Parses the source XML input with the given parse function."
  [parse-fn source]
  (with-open [input (io/input-stream source)]
    (->> input
         (xml/parse)
         (parse-fn))))

;;;
;;; read the maven xml 
;;;
(defn read-pom-xml
  "Returns a map with the contents of the maven POM referenced by source."
  [source]
  (parse-xml mvn/parse-pom-xml source))

(defn read-settings-xml
  "Returns a map with the contents of the maven settings referenced by source."
  [source]
  (parse-xml mvn/parse-settings-xml source))

(defn read-metadata-xml
  "Returns a map with the contents of the maven metadata referenced by source."
  [source]
  (parse-xml mvn/parse-metadata-xml source))


(defn print-dependencies
  )

(comment
  (read-pom-xml "/home/soulman/tmp/repository/org/soulspace/clj/clj.base/0.8.3/clj.base-0.8.3.pom")
  (read-settings-xml "/home/soulman/.m2/settings.xml")
  (read-metadata-xml "/home/soulman/.m2/repository/org/soulspace/clj/clj.base/maven-metadata-clojars.xml")
  )


