(ns org.soulspace.tools.info
  (:require [clojure.data.json :as json]
            [clojure.data.xml :as xml]))

;;;;
;;;; external project information
;;;;

;;;
;;; Github handling
;;;

(def github-root "https://github.com")

(defn github-url?
  "Returns true, if the url is a github url."
  [url]

  )

(defn releases-url
  "Returns the URL for the releases of the project."
  [p]
  (str github-root "/" p "/releases"))

(defn contributors-url
  "Returns the URL for contributors graph of the project."
  [p]
  (str github-root "/" p "/graphs/contributors"))

;(defn contributors
;  "Fetches the contributors for the git repository from GitHub."
;  [p]
;  (-> (slurp (contributors-url p))))

(comment
  (contributors "lsolbach/CljBase"))

;;;
;;; Apache handling
;;;

(defn apache-url?
  ""
  [url]

  )

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

(comment
  (println (spdx-licenses))
  (println (spdx-license "Apache-2.0")))


;;;
;;; OpenHub handling
;;;

; https://github.com/blackducksoftware/ohloh_api
; https://github.com/blackducksoftware/ohloh_api/blob/master/reference/project.md


; https://www.openhub.net/projects.xml
; https://www.openhub.net/projects/{project_id}.xml

(def openhub-root "https://www.openhub.net")

(defn projects-xml
  "Fetches the projects from openhub.net."
  []
  (-> (slurp (str openhub-root "/projects.xml"))
      (xml/parse)))

(defn project-xml
  "Fetches the project from openhub.net."
  [id]
  (-> (slurp (str openhub-root "/projects/" id ".xml"))
      (xml/parse)))

