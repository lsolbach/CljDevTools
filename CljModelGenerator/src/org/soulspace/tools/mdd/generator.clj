;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.tools.mdd.generator
  (:use
    [clojure.java.io :exclude [delete-file]]
    [clojure.string :only [join]]
    [org.soulspace.clj string file file-search]
    [org.soulspace.clj.java beans]
    [org.soulspace.tools.mdd design-model template context protected-areas]))

; TODO load the specified model as library to support different models with the generator (then we have a real generator framework)
; TODO define a protocol for the model repositories

; TODO load the template engine as library to support different template engines
; TODO define a protocol for template engines

; Generator
(defn get-path
  "Returns the path for the generated element."
  [dest gen element]
  (str dest "/" (namespace-path gen element) (name-path gen element)))

(defn create-namespace-path
  "Creates the path by creating all neccessary directories."
  [pathname]
  (let [file (as-file pathname)]
    (if (not (exists? file))
      (.mkdirs file))))

(defn suppress-write?
  ""
  [gen result])
  ;TODO implement
  ; :suppressWritePattern regex
  ; :suppressWrite EMPTY, WHITESPACE


(defn write-generated-artifact
  "Write the generated artifact to file."
  [pathname result]
  (let [file (as-file pathname)
        parent (parent-dir file)]
    ; TODO check suppress-write
    (when-not (exists? parent)
      (create-dir parent))
    ; TODO add encoding from generator
    ; (with-open ((writer pathname)))
    (spit pathname result)))

(defn build-context
  "Build a context map which is accessible from within the templates."
  [ctx-map]
  (loop [ctx {"Timestamp" (str (java.util.Date.))} src (keys ctx-map)]
    (if-not (seq src)
      ctx
      (let [k (first src)
            v (k ctx-map)]
        (recur (assoc ctx (first-upper-case (name k)) v) (rest src))))))

; TODO create functions in template.clj for the direct java calls here
; TODO to abstract from the concrete template engine implementation
(defn generate-for-element
  "Call the TemplateEngine's generate method for the given element and generator context."
  ([engine gen element]
   (let [ds (create-datasource element)]
     (.add ds "GENERATOR_CONTEXT" (build-context gen))
     (.generate engine ds)))
  ([engine gen element protected-areas datasource]
   (let [ds (create-datasource element datasource)]
     (.add ds "GENERATOR_CONTEXT" (build-context gen))
     (when (seq protected-areas)
       (.add ds "PROTECTED_AREAS" protected-areas))
     (.generate engine ds))))

(defn generate-for-generator
  "Generate for a given generator of the generation context."
  [ctx gen model model-ds]
  (let [engine (get-template-engine ctx gen)]
    (doseq [element (element-seq gen model)]
      (when (must-generate? gen element)
        (let [path (get-path (:destDir ctx) gen element)
              protected-areas (read-protected-areas gen path)]
          ; TODO check for protected areas and read them
          (let [result (generate-for-element engine gen element protected-areas model-ds)] ; TODO add model-ds?
            (write-generated-artifact path result)))))))

(defn generate-all
  "Generate for all configured generators in the generation context."
  ([ctx]
    ; Load the model specified by the context and call all generators in the generation context.
   (generate-all ctx (:generators ctx)))
  ([ctx generators]
    ; Load the model specified by the context and call all given generators.
   (let [model (initialize-model ctx)
         model-ds (create-datasource model)]
     (doseq [gen generators]
       ;(println "calling generator" gen)
       (generate-for-generator ctx gen model model-ds)))))
