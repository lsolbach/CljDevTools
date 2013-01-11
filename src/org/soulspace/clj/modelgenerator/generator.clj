(ns org.soulspace.clj.modelgenerator.generator
  (:use
    [clojure.java.io :exclude [delete-file]]
    [clojure.string :only [join]]
    [org.soulspace.clj.lib string file file-search]
    [org.soulspace.clj.java beans]
    [org.soulspace.clj.modelgenerator design-model template context protected-areas]))

; TODO load specified model as library to support different models with the generator (then we have a real generator framework)
; TODO define a protocol for the model repositories

; TODO load template engine as library to support different template engines
; TODO define a protocol for template engines

; Generator
(defn get-path [dest gen element]
  (str dest "/" (namespace-path gen element) (name-path gen element)))

(defn create-namespace-path [pathname]
  (let [file (as-file pathname)]
    (if (not (exists? file))
      (.mkdirs file))))

(defn suppress-write? [gen result]
  ;TODO implement
  ; :suppressWritePattern regex
  ; :suppressWrite EMPTY, WHITESPACE
  )

(defn write-generated-artifact [pathname result]
  ;(println "writing file " pathname)
  (let [file (as-file pathname)
        parent (parent-dir file)]
    ; TODO check suppress-write
    (when-not (exists? parent)
      (create-dir parent))
    ; TODO add encoding from generator
    ; (with-open ((writer pathname)))
    (spit pathname result)))

(defn build-context [ctx-map]
  "build a context map which is accessible from within the templates"
  (loop [ctx {} src (keys ctx-map)]
    (if-not (seq src)
      ctx
      (let [k (first src)
            v (k ctx-map)]
        (recur (assoc ctx (first-upper-case (name k)) v) (rest src))))))

; TODO create functions in template.clj for the direct java calls here
; TODO to abstract from the concrete template engine implementation
(defn generate-for-element
  ([engine gen element]
    (let [ds (create-datasource element)]
      (.add ds "GenContext" (build-context gen))
      (.generate engine ds)))
  ([engine gen element datasource]
    (let [ds (create-datasource element datasource)]
      (.add ds "GenContext" (build-context gen))
      (.generate engine ds))))

(defn generate-for-generator [ctx gen model model-ds]
;  (println "Getting template engine for" (:template gen))
  (let [engine (get-template-engine ctx gen)]
    (doseq [element (element-seq gen model)]
      (when (must-generate? gen element)
        ; TODO check for protected areas and read them
        (let [result (generate-for-element engine gen element)] ; TODO add model-ds?
          (write-generated-artifact (get-path (:destDir ctx) gen element) result))))))

(defn generate-all
  ([ctx]
    (generate-all ctx (:generators ctx)))
  ([ctx generators]
    "load the model specified by the context and generate for every given generator"
;    (println "Generator called" generators)
    (let [model (initialize-model ctx)
          model-ds (create-datasource model)]
      (doseq [gen generators]
;        (println "calling generator" gen)
        (generate-for-generator ctx gen model model-ds)))))
