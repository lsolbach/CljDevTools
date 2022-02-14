(ns org.soulspace.tools.repo
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [org.httpkit.client :as http]
            [org.soulspace.clj.namespace :as nsp]
            [org.soulspace.clj.file :as file]
            [org.soulspace.clj.property-replacement :as prop]
            [org.soulspace.tools.maven-xml :as mvnx]))

;;;
;;; version handling
;;;

(defn digits?
  "Tests if s contains only digits."
  [s]
  (if (empty? s)
    false
    (every? #(Character/isDigit %) s)))

(defn split-version
  "Splits a version string into revision components."
  ([version]
   (split-version version #"[.]"))
  ([version re]
   (if (empty? version)
     nil
     (str/split version re))))

(defn compare-revision
  "Compares two revision components c1 and c2."
  [c1 c2]
  ; compare numerically or lexically based on type
  (if (and (digits? c1) (digits? c2))
    (compare (Long/valueOf c1) (Long/valueOf c2))
    (compare c1 c2)))

(defn compare-version
  "Compares to versions v1 and v2."
  [v1 v2]
  (if (or (nil? v1) (nil? v2))
    (compare v1 v2)
    (loop [c1 (split-version v1)
           c2 (split-version v2)]
          ; split the versions and compare them part for part
      (if (and (seq c1) (seq c1))
        (if (not= (first c1) (first c2))
          (compare-revision (first c1) (first c2))
          (recur (rest c1) (rest c2)))
        (compare-revision (first c1) (first c2))))))

(defn lesser-version?
  "Returns true, if v1 is less than v2."
  [v1 v2]
  (< (compare-version v1 v2) 0.0))

(defn greater-version?
  "Returns true, if v1 is greater than v2."
  [v1 v2]
  (> (compare-version v1 v2) 0.0))

(defn same-version?
  "Returns true, if both versions are the same."
  [v1 v2]
  (= (compare-version v1 v2) 0))

(defn contains-version?
  "Returns true if the version v is contained in the version range given by from and to or as a map."
  ([r v]
   (contains-version? (:from r) (:to r) v))
  ([from to v]
   (cond
     (empty? v) false
     (and (empty? from) (empty? to)) true
     (and (empty? from) (lesser-version? v to)) true
     (and (empty? to) (not (lesser-version? v from))) true
     (and (not (lesser-version? v from)) (lesser-version? v to)) true
     :default false)))

; TODO version patterns and version pattern to version range conversion

;;;
;;; artifact adressing and comparing
;;;
;;
;; artifacts are described by maps containing at least a key :artifact-id
;; and optional keys :group-id and :version
;;
(s/def ::artifact (s/keys :req-un [::artifact-id]
                          :opt-un [::group-id ::version]))

(defn artifact-key
  "Returns a string key for the artifact a, disregarding a version."
  [a]
  (str (:group-id a) "/" (:artifact-id a)))

(defn artifact-version-key
  "Returns a string key for the artifact a including a version."
  [a]
  (str (artifact-key a) "[" (:version a) "]"))

(defn same-artifact?
  "Returns true, when the artifacts a1 and a2 have the same artifact key."
  [a1 a2]
  (= (artifact-key a1) (artifact-key a2)))

(defn same-artifact-version?
  "Returns true, when the artifacts a1 and a2 have the same artifact version key."
  [a1 a2]
  (= (artifact-version-key a1) (artifact-version-key a2)))

;;;
;;; repository handling
;;;
;;
;; single repository functions
;;
(s/def ::repository (s/keys :req-un [::id ::url ::name]
                            :opt-un [::username ::password ::releases ::snapshots ::layout]))

(def user-home (System/getProperty "user.home"))
(def local-repository (str user-home "/.m2/repository"))

(def hash-schemes #{"asc" "md5" "sha1"})

(defn artifact-relative-path
  "Returns the path to the artifact."
  [a]
  (str (nsp/ns-to-path (str (:group-id a))) "/" (:artifact-id a)))

(defn artifact-version-relative-path
  "Returns the path to the artifact."
  [a]
  (str (artifact-relative-path a) "/" (:version a)))

(defn artifact-local-path
  "Returns the path of the artifact in the local filesystem."
  ([a]
   (str local-repository "/" (artifact-relative-path a))))

(defn artifact-version-local-path
  "Returns the path of the artifact in the local filesystem."
  ([a]
   (str local-repository "/" (artifact-version-relative-path a))))

(defn artifact-filename
  "Returns the filename of the artifact in the repository.
   Takes classifier and extension as an optional parameter."
  ([a]
   (artifact-filename nil "jar" a))
  ([extension a]
   (artifact-filename nil extension a))
  ([classifier extension a]
   (str (:artifact-id a)
        (when classifier (str "-" classifier))
        "-" (:version a)
        "." extension)))

(defn artifact-url
  "Returns the URL of the artifact on the remote server.
   Takes classifier and extension as an optional parameter."
  ([repo a]
   (artifact-url repo "jar" a))
  ([repo extension a]
   (str (:url repo) "/" (artifact-version-relative-path a)
        "/" (artifact-filename extension a)))
  ([repo classifier extension a]
   (str (:url repo) "/" (artifact-version-relative-path a)
        "/" (artifact-filename classifier extension a))))

(defn artifact-metadata-url
  "Returns the URL to the maven metadata of the artifact."
  [repo a]
  (str (:url repo) "/" (nsp/ns-to-path (str (:group-id a)))
       "/" (:artifact-id a) "/maven-metadata.xml"))

(defn local-artifact?
  "Checks if the repository has the local artifact a.
   Takes classifier and extension as an optional parameter."
  ([a]
   (local-artifact? nil "jar" a))
  ([extension a]
   (local-artifact? nil extension a))
  ([classifier extension a]
   (file/is-dir? (io/as-file (artifact-version-local-path classifier extension a)))))

(defn remote-artifact?
  "Checks if the repository has the remote artifact a.
   Takes classifier and extension as an optional parameter."
  ([repo a]
   (remote-artifact? repo nil "jar" a))
  ([repo extension a]
   (remote-artifact? repo nil extension a))
  ([repo classifier extension a]
   (= 200 (:status @(http/request {:url (artifact-url repo classifier extension a)
                                   :method :head
                                   :timeout 1000})))))

(defn download-artifact
  "Downloads the artifact a from the repository.
   Takes classifier and extension as an optional parameter."
  ([repo a]
   (download-artifact repo nil "jar" a))
  ([repo extension a]
   (download-artifact repo nil extension a))
  ([repo classifier extension a]
   (let [url (io/as-url (artifact-url repo classifier extension a))
         local-path (artifact-version-local-path a)]
     (when-not (file/exists? local-path) ; missing local directory
       (file/create-dir (io/as-file local-path))) ; create it
     (io/copy (io/input-stream url)
              (io/as-file (str local-path "/" (artifact-filename classifier extension a)))))))

;;
;; repositories
;;

(def central {:id "central" :url "https://repo.maven.apache.org/maven2"
              :name "Central Repository" :snapshots {:enabled "false"}
              :layout "default"})
(def clojars {:id "clojars" :name "Clojars" :url "https://repo.clojars.org"
              :layout "default"})
(def repositories (atom [clojars central]))

;;
;; functions using the reopsitories list
;;

(defn add-repository
  "Adds a repository to the list of configured repositories."
  [repo]
  {:pre [(s/valid? ::repository repo)]}
  (swap! repositories conj repo))

(defn cache-artifact
  "Downloads and caches an artifact including it's associated pom."
  ([a]
   (cache-artifact a "jar"))
  ([a ext]
   (println "caching artifact" a)
   (loop [repos @repositories]
     (when-let [repo  (first repos)]
       (if (remote-artifact? repo a)
         (do ; TODO download checksums and common classified artifacts too
           (println "downloading from repo" (:name repo))
           (download-artifact repo a "pom")
           (download-artifact repo a ext)
           (println "artifact cached"))
         (recur (rest repos)))))))

(defn read-artifact-pom
  "Returns the POM of the artifact."
  [a]
  (when-not (local-artifact? a)
    (cache-artifact a))
  (mvnx/read-pom-xml (str (artifact-version-local-path a) "/" (artifact-filename "pom" a))))

(defn merge-build-section
  "Merges the build of parent POM p1 and child POM p2"
  [p1 p2]
  {:default-goal (if (contains? p2 :default-goal)
                   (:default-goal p2)
                   (:default-goal p1)) ; TODO check
   :directory (if (contains? p2 :directory)
                (:directory p2)
                (:directory p1))
   :source-directory (if (contains? p2 :source-directory)
                       (:source-directory p2)
                       (:source-directory p1))
   :script-source-directory (if (contains? p2 :script-source-directory)
                              (:script-source-directory p2)
                              (:script-source-directory p1))
   :test-source-directory (if (contains? p2 :test-source-directory)
                            (:test-source-directory p2)
                            (:test-source-directory p1))
   :output-directory (if (contains? p2 :output-directory)
                       (:output-directory p2)
                       (:output-directory p1))
   :test-output-directory (if (contains? p2 :test-output-directory)
                            (:test-output-directory p2)
                            (:test-output-directory p1))
   :extensions (into [] (concat (:extensions p1) (:extensions p2)))
   :final-name (if (contains? p2 :final-name)
                 (:final-name p2)
                 (:final-name p1))
   :filters (into [] (concat (:filters p1) (:filters p2)))
   :resources (into [] (concat (:resources p1) (:resources p2)))
   :test-resources (into [] (concat (:test-resources p1) (:test-resources p2)))
   :plugins (into [] (concat (:plugins p1) (:plugins p2)))
   :plugin-management (merge-with concat
                                  (:plugin-management p1)
                                  (:plugin-management p2))})

(defn merge-reporting-section
  "Merges the reporting of parent POM p1 and child POM p2"
  [p1 p2]
  ; TODO
  )

(defn merge-poms
  "Merges a parent POM p1 and a child POM p2."
  ([])
  ([p1 p2]
   ; p1 is the parent of p2, so p2 is more specific
   {:model-version (:model-version p2)
    :group-id (:group-id p2)
    :artifact-id (:artifact-id p2)
    :version (:version p2)
    :packaging (:packaging p2)
    :dependencies (into [] (concat (:dependencies p1) (:dependencies p2)))
    :parent (:parent p2)
    :modules (:modules p2)
    :properties (merge (:properties p1) (:properties p2))
    :build (merge-build-section p1 p2)
    :reporting (merge-reporting-section p1 p2)
    :description (:description p2)
    :url (:url p2)
    :inception-year (:inception-year p2)
    :licenses (if (contains? p2 :licenses)
                (:licenses p2)
                (:licenses p1)) ; TODO check
    :organization (:organization p2)
    :developers (into [] (concat (:developers p1) (:developers p2)))
    :contributors (into [] (concat (:contributors p1) (:contributors p2)))
    :issue-management (if (contains? p2 :issue-management)
                        (:issue-management p2)
                        (:issue-management p1)) ; TODO check
    :ci-management (if (contains? p2 :ci-management)
                     (:ci-management p2)
                     (:ci-management p1)) ; TODO check
    :mailing-lists (if (contains? p2 :mailing-lists)
                     (:mailing-lists p2)
                     (:mailing-lists p1)) ; TODO check
    :scm (if (contains? p2 :scm)
           (:scm p2)
           (:scm p1)) ; TODO check
    :prerequisites (:prerequisites p2)
    :repositories (into [] (concat (:repositories p1) (:repositories p2)))
    :plugin-repositories (into [] (concat (:plugin-repositories p1)
                                          (:plugin-repositories p2)))
    :distribution-management (if (contains? p2 :distribution-management)
                               (:distribution-management p2)
                               (:distribution-management p1)) ; TODO check
    :profiles (into [] (concat (:profiles p1) (:profiles p2)))}))

(defn replace-properties-in-pom
  "Returns the map for of the POM with the properties replaced with their values."
  [pom]
  (let [properties (:properties pom)]
    (->> (dissoc pom :properties) ; remove properties from POM
        ; use walk/prewalk to replace the string values in the pom
        ;   with the property replaced values
         (walk/prewalk #(if (string? %)
                          (prop/replace-properties-recursive properties %)
                          %))))) ; recursive replacement to handle nested properties
;    (assoc pom :properties properties))) ; reinsert properties

(defn managed-versions
  "Transforms the dependency manangement list into a map of artifact keys to versions."
  [pom]
  (into {} (map #([(artifact-key %) (:version %)]) (:dependency-management pom))))

(comment
  (prop/replace-properties {:version "1.5"} "${version}")
  (prop/replace-properties-recursive {:coords "a/b [${version}]"
                                      :version "1.5"} "${coords}")
)

(defn pom-for-artifact
  "Builds the project object model map for the artifact by loading and merging the POM and it's parent POM's, if any."
  [a]
  (loop [artifact a poms []]
    (println "reading pom for" artifact)
    (let [pom (read-artifact-pom artifact)]
      (if-let [parent (:parent pom)]
        (recur parent (conj poms pom)) ; add parent pom to the vector 
        ;(apply merge (conj poms pom))
        (->> (conj poms pom)
             (reverse)
             (reduce merge-poms)
             (replace-properties-in-pom))))))

;;;
;;; dependencies
;;;
;;
;; a dependency is an extension of artifacts with optional :scope and :exclusions keys
;;
; TODO complete
(s/def ::exclusion (s/keys :req [::artifact-id]
                           :opt [::group-id ::version]))
(s/def ::dependency (s/keys :req [::artifact-id]
                            :opt [::group-id ::version ::classifier ::type ::scope ::system-path ::optional ::exclusions]))

;;;
;;; artifact dependency handling
;;;

(defn resolved?
  "Checks, if the artifact is already resolved."
  [resolved a]
  (contains? resolved (artifact-key a))) ; TODO artifack-version-key?

(defn cycle?
  "Checks, if the artifact produces a cycle."
  [a])

(defn excluded?
  "Checks, if the artifact is in the set of exclusions."
  [exclusions a]
  (contains? exclusions (artifact-key a)))

(defn exclude-artifact
  "Returns the exclusions collection with the artifact key added."
  [exclusions a]
  (let [k (artifact-key a)]
    (if (contains? k)
      exclusions ; artifact key already contained
      (conj exclusions k) ; not contained, add artifact key 
      )))

(defn build-dependency-node
  "Creates a node for the dependency tree."
  [a]
  {:group-id (:group-id a)
   :artifact-id (:artifact-id a)
   :version (:version a)
   :exclusions #{}
   :dependencies []
   :scope ""}
  )

; transitive dependency resolution with depth first search
; build up exclusions on the way down and inclusions on the way up
(defn resolve-dependencies
  "Resolves the (transitive) dependencies of the artifact."
  [a]
  (println (artifact-version-key a))
  (let [pom (pom-for-artifact a)
        dependencies (:dependencies pom)
        exclusions (get-in pom [:dependencies :exclusions])]
    (loop [deps dependencies includes []]
      (if (seq deps)
        (let [dep (first deps)]
          (println dep)
          (resolve-dependencies (first deps)))
        (recur (rest deps) includes))) ; FIXME build dep node and include
    ))

(comment
  (str "a-" nil "-b")
  (artifact-relative-path {:group-id "a" :artifact-id "b" :version "1.0"})
  (artifact-filename {:group-id "a" :artifact-id "b" :version "1.0"})
  (artifact-local-path {:group-id "a" :artifact-id "b" :version "1.0"})
  (artifact-url {:id "clojars" :remote "http://repo.clojars.org"}
                {:group-id "a" :artifact-id "b" :version "1.0"})
  (artifact-metadata-url {:id "clojars" :remote "http://repo.clojars.org"}
                         {:group-id "a" :artifact-id "b" :version "1.0"})
  (local-artifact? {:group-id "org.soulspace.clj" :artifact-id "clj.base"
                    :version "0.8.3"})
  (download-artifact {:id "clojars" :remote "http://repo.clojars.org"
                      :local "/home/soulman/tmp/repository"}
                     {:group-id "org.soulspace.clj" :artifact-id "clj.base"
                      :version "0.8.3"})
  (cache-artifact {:id "clojars" :remote "http://repo.clojars.org"
                   :local "/home/soulman/tmp/repository"}
                  {:group-id "org.soulspace.clj" :artifact-id "clj.base"
                   :version "0.8.3"})
  (artifact-relative-path {:group-id "org.soulspace.clj"
                           :artifact-id "clj.base"
                           :version "0.8.3"})
  (read-artifact-pom {:group-id "org.soulspace.clj" :artifact-id "clj.base"
                      :version "0.8.3"})
  (pom-for-artifact {:group-id "org.soulspace.clj" :artifact-id "clj.base"
                     :version "0.8.3"})
  (pom-for-artifact {:group-id "commons-codec" :artifact-id "commons-codec"
                     :version "1.13"})
  (slurp (str "http://repo.clojars.org/"
              (artifact-relative-path {:group-id "org.soulspace.clj"
                                       :artifact-id "clj.base"
                                       :version "0.8.3"})))
  (slurp "http://repo.clojars.org/org/soulspace/clj/clj.base/")
  @(http/head "http://repo.clojars.org/org/soulspace/clj/clj.base/")
  @(http/request {:url "http://repo.clojars.org/org/soulspace/clj/clj.base/"
                  :method :head
                  :timeout 500})
  (resolve-dependencies {:group-id "org.soulspace.clj" :artifact-id "clj.base"
                         :version "0.8.3"})
  (slurp "/home/soulman/.m2/repository/org/codehaus/jsr166-mirror/jsr166y/1.7.0/jsr166y-1.7.0.pom")
  )

