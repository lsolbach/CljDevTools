(ns org.soulspace.tools.project-info
  (:require [clojure.data.json :as json]
            [clojure.data.xml :as xml]))

;;;;
;;;; external project information
;;;;

;;;
;;; Github handling
;;;

(def github-pattern #"https://github.com/(.*)")
(def github-root "https://github.com")

(defn github-url?
  "Returns truthy, if the url is a github project url."
  [url]
  (re-matches github-pattern url))

(defn github-releases-url
  "Returns the URL for the releases of the project."
  [p]
  (str github-root "/" p "/releases"))

(defn github-contributors-url
  "Returns the URL for contributors graph of the project."
  [p]
  (str github-root "/" p "/graphs/contributors"))

(comment
  (github-releases-url "lsolbach/CljBase")
  (github-contributors-url "lsolbach/CljBase"))

;;;
;;; Apache handling
;;;

(def apache-pattern #"https://(.*)\.apache\.org.*")

(defn apache-url?
  "Returns truthy, if the url is a apache project url."
  [url]
  (re-matches apache-pattern url))

;;;
;;; SPDX license handling
;;;

(def spdx-root "https://spdx.org")

(defn osi-approved?
  "Returns true, if the license is approved by the Open Software Initiative."
  [l]
  (= (:isOsiApproved l) true))

(defn fsf-libre?
  "Returns true, if the license is approved by the Free Software Foundation."
  [l]
  (= (:isFsfLibre l) true))

(defn deprecated-id?
  "Returns true, if the license is approved by the Free Software Foundation."
  [l]
  (= (:isDeprecatedLicenseId l) true))

(defn spdx-licenses
  "Fetch the licenses from SPDX."
  []
  (-> (slurp (str spdx-root "/licenses/licenses.json"))
      (json/read-str :key-fn keyword)
      (:licenses)))

(defn spdx-license
  "Fetch the license from SPDX"
  [spdx-id]
  (-> (slurp (str spdx-root "/licenses/" spdx-id ".json"))
      (json/read-str :key-fn keyword)))

(defn spdx-exceptions
  "Fetch the exceptions from SPDX."
  []
  (-> (slurp (str spdx-root "/licenses/exceptions.json"))
      (json/read-str :key-fn keyword)
      (:licenses)))

(defn spdx-exception
  "Fetch the exception from SPDX"
  [spdx-id]
  (-> (slurp (str spdx-root "/licenses/" spdx-id ".json"))
      (json/read-str :key-fn keyword)))

(comment
  (println (spdx-licenses))
  (println (spdx-exceptions))
  (println (spdx-license "Apache-2.0"))
  (println (spdx-exception "Classpath-exception-2.0"))
  )


;;;
;;; OpenHub handling
;;;

;; Docs
; https://github.com/blackducksoftware/ohloh_api
; https://github.com/blackducksoftware/ohloh_api/blob/master/reference/project.md

;; URLs
; https://www.openhub.net/projects.xml
; https://www.openhub.net/projects/{project_id}.xml

(def openhub-root "https://www.openhub.net")
(def openhub-api-key "") ; TODO set from ENV

(defn openhub-projects-xml
  "Fetches the projects from openhub.net."
  []
  (-> (slurp (str openhub-root "/projects.xml"))
      (xml/parse)))

(defn openhub-project-xml
  "Fetches the project from openhub.net."
  [id]
  (-> (slurp (str openhub-root "/projects/" id ".xml"))
      (xml/parse)))

(comment
  (openhub-projects-xml))