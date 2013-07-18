(ns org.soulspace.clj.modelgenerator.template
  (:use
    [org.soulspace.clj file file-search]
    )
  (:import
    [java.io File]
    [org.soulspace.template.impl TemplateEngineImpl]
    [org.soulspace.template.datasource.impl BeanDataSourceImpl]))

; TemplateEngine functions
(defn get-template-files [ctx gen]
  (let [searchpath (build-searchpath (:templateDirs ctx))]
    ; TODO check for missing files
    (let [includes (map (file-locator searchpath "tinc") (:includes gen))
          template ((file-locator searchpath "tmpl") (:template gen))]
      (conj (vec includes) template))))

(defn create-datasource 
  ([]
    (BeanDataSourceImpl.))
  ([data]
    (BeanDataSourceImpl. data))
  ([data datasource]
    (BeanDataSourceImpl. data datasource)))

(defn create-template-engine []
  (TemplateEngineImpl.))

(defn get-template-engine [ctx gen]
  (let [engine (create-template-engine)
        template-files (get-template-files ctx gen)]
    ;(println "template files" template-files)
    (.loadTemplates engine (into-array template-files))
    engine))
