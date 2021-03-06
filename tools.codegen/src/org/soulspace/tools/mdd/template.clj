;;
;;   Copyright (c) Ludger Solbach. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file license.txt at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
;;

(ns org.soulspace.tools.mdd.template
  (:use [org.soulspace.clj file file-search])
  (:import [java.io File]
           [org.soulspace.template.impl TemplateEngineImpl]
           [org.soulspace.template.datasource.impl BeanDataSourceImpl]))

; TemplateEngine functions
(defn get-template-files [ctx gen]
  (let [searchpath (build-searchpath (:templateDirs ctx))]
    ; TODO check for missing files
    (let [includes (map (file-locator searchpath "tinc") (:includes gen))
          template ((file-locator searchpath "tmpl") (:template gen))]
      (conj (vec includes) template))))

(defn dump-datasource [ds]
  (org.soulspace.template.util.DataSourceUtil/dump ds))

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
    ;(println "TEMPLATES" template-files)
    (.loadTemplates engine (into-array template-files))
    engine))
