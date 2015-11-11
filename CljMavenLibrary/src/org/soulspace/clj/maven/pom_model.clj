;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.maven.pom-model
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx]
            [org.soulspace.clj.maven.pom-dsl :as pom])
  (:use [org.soulspace.clj.xml marshalling]
        [org.soulspace.clj.application string-property])
  (:import [org.soulspace.clj.xml.marshalling.XMLMarshalling]))

; FIXME hack
(defn pom-name [dependency]
  (if (nil? (:classifier dependency))
    (:artifact-id dependency)
    (str (:artifact-id dependency) "-" (:classifier dependency))))

(defn pom-exclusion-data [exclusion]
  "Creates exclusion data from a pom exclusion."
  [(:group-id exclusion) (:artifact-id exclusion)]
  )

; TODO return all fields
(defn pom-dependency-data [dependency]
  "Creates dependency data from a pom dependency."
  [[(:group-id dependency) (:artifact-id dependency) (:version dependency)
    (pom-name dependency) (:type dependency)]
   (:scope dependency) (:optional dependency) 
   (map pom-exclusion-data (:exclusions dependency))])

(defn parse-pom-properties [zipped]
  "Creates property map for pom properties"
  ; zip to properties if any and return a map of child element names and their contents
  (if-let [properties (zx/xml1-> zipped :properties)]
    (if-let [props (zip/children properties)]
      (loop [ps props property-map {}]
        (if (seq ps)
          (recur (rest ps) (assoc property-map (:tag (first ps)) (first (:content (first ps)))))
          property-map))
      {})
    {}))

(defrecord Prerequisites
  [maven]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/prerequisites {}
                       (when (:maven this) (pom/maven (:maven this))))))

(defrecord IssueManagement
  [system url]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/issue-management {}
                  (when (:system this) (pom/system {} (:system this)))
                  (when (:url this) (pom/url {} (:url this))))))

(defrecord CiManagement
  [system url notifiers]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/ci-management {}
                  (when (:system this) (pom/system {} (:system this)))
                  (when (:url this) (pom/url {} (:url this)))
                  (when (seq (:notifiers this)) (pom/notifiers {} (map to-xml (:notifiers this)))))))

(defrecord Parent
  [group-id artifact-id version]
  XMLMarshalling
  (from-xml [this xml]
    (let [group-id (zx/xml1-> xml :groupId zx/text)
          artifact-id (zx/xml1-> xml :artifactId zx/text)
          version (zx/xml1-> xml :version zx/text)]
      (Parent. group-id artifact-id version)))
  (to-xml [this] 
    (pom/parent {}
                (pom/group-id {} (:group-id this))
                (pom/artifact-id {} (:artifact-id this))
                (pom/version {} (:version this)))))

(defrecord Dependency
  [group-id artifact-id version type classifier scope system-path exclusions optional]
  XMLMarshalling
  (from-xml [this xml] 
    (let [group-id (zx/xml1-> xml :groupId zx/text)
          artifact-id (zx/xml1-> xml :artifactId zx/text)
          version (zx/xml1-> xml :version zx/text)
          type (zx/xml1-> xml :type zx/text)
          classifier (zx/xml1-> xml :classifier zx/text)
          scope (zx/xml1-> xml :scope zx/text)
          system-path (zx/xml1-> xml :systemPath zx/text)
          ; FIXME create exclusions with from-xml
          exclusions (map (partial parse-pom-exclusion prop-map) (zx/xml-> xml :exclusions :exclusion))
          optional (zx/xml1-> xml :optional zx/text)
          ]
      (Dependency. group-id artifact-id version type classifier scope system-path exclusions optional))
  (to-xml [this]
    (pom/dependency {}
                    (pom/group-id {} (:group-id this))
                    (pom/artifact-id {} (:artifact-id this))
                    (when (:version this) (pom/version {} (:version this)))
                    (when (:type this) (pom/type {} (:type this)))
                    (when (:classifier this) (pom/classifier {} (:classifier this)))
                    (when (:scope this) (pom/scope {} (:scope this)))
                    (when (:system-path this) (pom/system-path {} (:system-path this)))
                    (when (:optional this) (pom/optional {} (:optional this)))
                    (when (seq (:exclusions this))
                      (pom/exclusions {} (map to-xml (:exclusions this)))))))

(defrecord Exclusion
  [group-id artifact-id]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this] 
    (pom/exclusion {}
                 (when (:group-id this) (pom/group-id {} (:group-id this)))
                 (when (:artifact-id this) (pom/artifact-id {} (:artifact-id this))))))

(defrecord Contributor
  [name email url organization organization-url roles timezone properties]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/contributor {}
                     (when (:name this) (pom/name {} (:name this)))
                     (when (:email this) (pom/email {} (:email this)))
                     (when (:url this) (pom/url {} (:url this)))
                     (when (:organization this) (pom/organization {} (:organization this)))
                     (when (:organization-url this) (pom/organization-url {} (:organization-url this)))
                     (when (seq (:roles this))
                       (pom/roles {} (map to-xml (:roles this))))
                     (when (:timezone this) (pom/timezone {} (:timezone this)))
                     (when (seq (:properties this))
                       (pom/properties {} ))))) ; TODO
  
(defrecord Developer
  [id name email url organization organization-url roles timezone properties]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/developer {}
                   (when (:id this) (pom/id {} (:id this)))
                   (when (:name this) (pom/name {} (:name this)))
                   (when (:email this) (pom/email {} (:email this)))
                   (when (:url this) (pom/url {} (:url this)))
                   (when (:organization this) (pom/organization {} (:organization this)))
                   (when (:organization-url this) (pom/organization-url {} (:organization-url this)))
                   (when (seq (:roles this))
                     (pom/roles {} (map to-xml (:roles this))))
                   (when (:timezone this) (pom/timezone {} (:timezone this)))
                   (when (seq (:properties this))
                     (pom/properties {} ))))) ; TODO

(defrecord Role
  [role]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/role {} (:role this))))

(defrecord Profile
  [id activation build modules repositories plugin-repositories dependencies
   reports reporting dependency-management distribution-management properties]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/profile {}
                 (when (:id this) (pom/id {} (:id this)))
                 (when (:activation this) (to-xml (:activation this)))
                 (when (:build this) (to-xml (:build this)))
                 (when (seq (:modules this))
                   (pom/modules {} (map to-xml (:modules this))))
                 (when (seq (:repositories this))
                   (pom/repositories {} (map to-xml (:repositories this))))
                 (when (seq (:plugin-repositories this))
                   (pom/plugin-repositories {} (map to-xml (:plugin-repositories this))))
                 (when (seq (:dependencies this))
                   (pom/dependencies {} ((map to-xml (:dependencies this)))))
                 (when (seq (:reports this))
                   (pom/reports {} (map to-xml (:reports this))))
                 (when (:reporting this) (to-xml (:reporting this)))
                 (when (:dependency-management this) (to-xml (:dependency-management this)))
                 (when (:distribution-management this) (to-xml (:distribution-management this)))
                 (when (seq (:properties this))
                   (pom/properties {} ))))) ; TODO

(defrecord Activation
  [active-by-default jdk os property file]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/activation {}
                    (when (:active-by-default this) (pom/active-by-default {} (:active-by-default this)))
                    (when (:jdk this) (pom/jdk {} (:jdk this)))
                    (when (:os this) (to-xml (:os this)))
                    (when (:property this) (to-xml (:property this)))
                    (when (:file this) (to-xml (:file this))))))
  
(defrecord ActivationFile
  [missing exists]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/activationfile {}
                        (when (:missing this) (pom/missing {} (:missing this)))
                        (when (:exists this) (pom/exists {} (:exists this))))))

(defrecord ActivationProperty
  [name value]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/activationproperty {}
                            (when (:name this) (pom/name {} (:name this)))
                            (when (:value this) (pom/value {} (:value this))))))

(defrecord ActivationOS
  [name family arch version]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/activationos {}
                      (when (:name this) (pom/name {} (:name this)))
                      (when (:family this) (pom/family {} (:family this)))
                      (when (:arch this) (pom/arch {} (:arch this)))
                      (when (:version this) (pom/version {} (:version this))))))

(defrecord DependencyManagement
  [dependencies]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/dependency-management {}
                              (when (seq (:dependencies this))
                                (pom/dependencies {} (map to-xml (:dependencies this)))))))

(defrecord Reporting
  [exclude-defaults output-directory plugins]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/reporting {}
                   (when (:exclude-defaults this) (pom/exclude-defaults {} (:exclude-defaults this)))
                   (when (:output-directory this) (pom/output-directory {} (:output-directory this)))
                   (when (:plugins this) (pom/output-directory {} (:plugins this))))))

(defrecord ReportPlugin
  [group-id artifact-id version inherited configuration report-sets]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/report-plugin {}
                      (when (:group-id this) (pom/group-id {} (:group-id this)))
                      (when (:artifact-id this) (pom/artifact-id {} (:artifact-id this)))
                      (when (:version this) (pom/version {} (:version this)))
                      (when (:inherited this) (pom/inherited {} (:inherited this)))
                      (when (:configuration this) (to-xml (:configuration this)))
                      (when (seq (:report-sets this)) (pom/report-sets {} (map to-xml (:report-sets this)))))))

(defrecord ReportSet
  [id configuration inherited reports]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/report-set {}
                   (when (:id this) (pom/id {} (:id this)))
                   (when (:configuration this) (to-xml (:configuration this)))
                   (when (:inherited this) (pom/inherited {} (:inherited this)))
                   (when (seq (:reports this)) (pom/reports {} (map to-xml (:reports this)))))))

(defrecord Report
  [name]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/report {} (:name this))))

(defrecord BuildBase
  [default-goal resources test-resources directory final-name filters plugin-management plugins]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/build-base {}
                   )))

(defrecord Plugin
  [group-id artifact-id version extensions executions dependencies goals inherited configuration]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/plugin {}
                (when (:group-id this) (pom/group-id {} (:group-id this)))
                (when (:artifact-id this) (pom/artifact-id {} (:artifact-id this)))
                (when (:version this) (pom/version {} (:version this)))
                (when (seq (:extensions this)) (pom/extensions {} (map to-xml (:extensions this))))
                (when (seq (:executions this)) (pom/executions {} (map to-xml (:executions this))))
                (when (seq (:dependencies this)) (pom/dependencies {} (map to-xml (:dependencies this))))
                (when (seq (:goals this)) (pom/goals (map to-xml (:goals this))))
                (when (:inherited this) (pom/inherited {} (:inherited this)))
                (when (:configuration this) (to-xml (:configuration this))))))

(defrecord PluginExecution
  [id phase goals inherited configuration]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/plugin-execution {}
                         )))

(defrecord PluginManagement
  [plugins]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/plugin-management {} (when (seq (:plugins this)) (pom/plugins {} (map to-xml (:plugins this)))))))

(defrecord Goal
  [goal]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/goal {} (:goal this))))

(defrecord Resource
  [target-path filtering directory includes excludes]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/resource {}
                )))

(defrecord Include
  [pattern]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/include {} (:pattern this))))

(defrecord Exclude
  [pattern]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/exclude {} (:pattern this))))

(defrecord DistributionManagement
  [repository snapshot-repository site download-url relocation status]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/distribution-management {}
                                (when (:repository this) (to-xml (:repository this)))
                                (when (:snapshot-repository this) (to-xml (:snapshot-repository this)))
                                (when (:site this) (to-xml (:site this)))
                                (when (:download-url this) (pom/download-url {} (:download-url this)))
                                (when (:relocation this) (to-xml (:relocation this)))
                                (when (:status this) (pom/status {} (:status this))))))

(defrecord Site
  [id name url]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/site {}
              (when (:id this) (pom/id {} (:id this)))
              (when (:name this) (pom/name {} (:name this)))
              (when (:url this) (pom/url {} (:url this))))))

(defrecord Relocation
  [group-id artifact-id version message]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/relocation {}
                    (when (:group-id this) (pom/group-id {} (:group-id this)))
                    (when (:artifact-id this) (pom/artifact-id {} (:artifact-id this)))
                    (when (:version this) (pom/version {} (:version this)))
                    (when (:message this) (pom/message {} (:message this))))))

(defrecord DeploymentRepository
  [unique-version id name url layout]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/deployment-repository {}
                    (when (:unique-version this) (pom/unique-version {} (:unique-version this)))
                    (when (:id this) (pom/id {} (:id this)))
                    (when (:name this) (pom/name {} (:name this)))
                    (when (:url this) (pom/url {} (:url this)))
                    (when (:layout this) (pom/layout {} (:layout this))))))

(defrecord Repository
  [releases snapshots id name url layout]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/repository {}
                    (when (:releases this) (to-xml (:releases this)))
                    (when (:snapshots this) (to-xml (:snapshots this)))
                    (when (:id this) (pom/id {} (:id this)))
                    (when (:name this) (pom/name {} (:name this)))
                    (when (:url this) (pom/url {} (:url this)))
                    (when (:layout this) (pom/layout {} (:layout this))))))

(defrecord RepositoryPolicy
  [enabled update-policy checksum-policy]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/repository-policy {}
                          (when (:enabled this) (pom/enabled {} (:enabled this)))
                          (when (:update-policy this) (pom/update-policy {} (:update-policy this)))
                          (when (:checksum-policy this) (pom/checksum-policy {} (:checksum-policy this))))))

(defrecord MailingList
  [name subscribe unsubscribe post archive other-archives]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/mailing-list {}
                     (when (:name this) (pom/name {} (:name this)))
                     (when (:subscribe this) (pom/subscribe {} (:subscribe this)))
                     (when (:unsubscribe this) (pom/unsubscribe {} (:unsubscribe this)))
                     (when (:post this) (pom/post {} (:post this)))
                     (when (:archive this) (pom/archive {} (:archive this)))
                     (when (seq (:resources this)) (pom/resources {} (map to-xml (:resources this)))))))

(defrecord OtherArchive
  [archive]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/other-archive {} (:archive this))))

(defrecord Build
  [source-directory script-source-directory test-source-directory
   output-directory test-output-directory extensions default-goal
   resources test-resources directory final-name filters]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/build {}
               (when (:source-directory this) (pom/source-directory {} (:source-directory this)))
               (when (:script-source-directory this) (pom/script-source-directory {} (:script-source-directory this)))
               (when (:test-source-directory this) (pom/test-source-directory {} (:test-source-directory this)))
               (when (:output-directory this) (pom/output-directory {} (:output-directory this)))
               (when (:test-output-directory this) (pom/test-output-directory {} (:test-output-directory this)))
               (when (seq (:extensions this)) (pom/extensions {} (map to-xml (:extensions this))))
               (when (:default-goal this) (pom/default-goal {} (:default-goal this)))
               (when (seq (:resources this)) (pom/resources {} (map to-xml (:resources this))))
               (when (:directory this) (pom/directory {} (:directory this)))
               (when (:final-name this) (pom/final-name {} (:final-name this)))
               (when (seq (:filters this)) (pom/filters {} (map to-xml (:filters this)))))))

(defrecord Filter
  [filter]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/filter {} (:filter this))))

(defrecord TestResource
  [target-path filtering directory includes excludes]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/test-resource {}
                   (when (:target-path this) (pom/target-path {} (:target-path this)))
                   (when (:filtering this) (pom/filtering {} (:filtering this)))
                   (when (:directory this) (pom/directory {} (:directory this)))
                   (when (seq (:includes this)) (pom/includes {} (map to-xml (:includes this))))
                   (when (seq (:excludes this)) (pom/excludes {} (map to-xml (:excludes this)))))))

(defrecord Extension
  [group-id artifact-id version]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/extension {}
                   (when (:group-id this) (pom/group-id {} (:group-id this)))
                   (when (:artifact-id this) (pom/artifact-id {} (:artifact-id this)))
                   (when (:version this) (pom/version {} (:version this))))))

(defrecord Notifier
  [type send-on-error send-on-failure send-on-success send-on-warning address configuration]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/notifier {}
                  (when (:type this) (pom/type {} (:type this)))
                  (when (:send-on-error this) (pom/send-on-error {} (:send-on-error this)))
                  (when (:send-on-failure this) (pom/send-on-failure {} (:send-on-failure this)))
                  (when (:send-on-success this) (pom/send-on-success {} (:send-on-success this)))
                  (when (:send-on-warning this) (pom/send-on-warning {} (:send-on-warning this)))
                  (when (:address this) (pom/address {} (:address this)))
                  (when (:configuration this) (to-xml (:configuration this))))))

(defrecord License
  [name url distribution comments]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/license {}
                 (when (:name this) (pom/name (:name this)))
                 (when (:url this) (pom/url (:url this)))
                 (when (:distribution this) (pom/distribution (:distribution this)))
                 (when (:url this) (pom/url (:url this))))))

(defrecord Scm
  [connection developer-connection tag url]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/scm {}
             (when (:connection this) (pom/connection (:connection this)))
             (when (:developer-connection this) (pom/developer-connection (:developer-connection this)))
             (when (:tag this) (pom/tag (:tag this)))
             (when (:url this) (pom/url (:url this))))))

(defrecord Module
  [module]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this] ))

(defrecord Organization
  [name url]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this]
    (pom/organization {}
                      (when (:name this) (pom/name {} (:name this)))
                      (when (:url this) (pom/url {} (:url this)))
                      )))

(defrecord Configuration
  [items]
  XMLMarshalling
  (from-xml [this xml] )
  (to-xml [this] ))

(defrecord Project
  [parent model-version group-id artifact-id packaging name version description url prerequisites
   issue-management ci-management inception-year mailing-lists developers contributors licenses
   scm organization build profiles modules repositories plugin-repositories 
   dependencies reports reporting dependency-management distribution-management properties
   parent-pom]
  XMLMarshalling
  (from-xml [this zipper] 
    (let [parent (from-xml (map->Parent {}) (zx/xml1-> zipper :parent))
          model-version (zx/xml1-> zipper :modelVersion zx/text)
          group-id (zx/xml1-> zipper :groupId zx/text)
          artifact-id (zx/xml1-> zipper :artifactId zx/text)
          packaging (zx/xml1-> zipper :packaging zx/text)
          name (zx/xml1-> zipper :name zx/text)
          version (zx/xml1-> zipper :version zx/text)
          description (zx/xml1-> zipper :description zx/text)
          url (zx/xml1-> zipper :url zx/text)
          prerequisites (from-xml (map->Prerequisites {}) (zx/xml1-> zipper :prerequisites))
          issue-management  (from-xml (map->IssueManagement {}) (zx/xml1-> zipper :issue-management))
          ci-management (from-xml (map->CiManagement {}) (zx/xml1-> zipper :ci-management))
          inception-year (zx/xml1-> zipper :inceptionYear zx/text)
          mailing-lists (map #(from-xml (map->MailingList {}) %) (zx/xml-> zipper :mailingLists :mailingList))
          developers (map #(from-xml (map->Developer {}) %) (zx/xml-> zipper :developers :developer))
          contributors (map #(from-xml (map->Contributor {}) %) (zx/xml1-> zipper :contributors :contributor))
          licenses (map #(from-xml (map->License {}) %) (zx/xml1-> zipper :licenses :license))
          scm (from-xml (map->Scm {}) (zx/xml1-> zipper :scm))
          organization (from-xml (map->Organization {}) (zx/xml1-> zipper :organization))
          build (from-xml (map->Build {}) (zx/xml1-> zipper :build))
          profiles (map #(from-xml (map->Profile {}) %) (zx/xml-> zipper :profiles :profile))
          modules (map #(from-xml (map->Module {}) %) (zx/xml-> zipper :modules :module))
          repositories (map #(from-xml (map->Repository {}) %) (zx/xml-> zipper :repositories :repository))
          plugin-repositories (map #(from-xml (map->Repository {}) %) (zx/xml1-> zipper :plugin-repositories :plugin-repository))
          dependencies (map #(from-xml (map->Dependency {}) %) (zx/xml1-> zipper :dependencies :dependency))
          reports (map #(from-xml (map->Report {}) %) (zx/xml1-> zipper :reports :report)) ; TODO check
          reporting (from-xml (map->Reporting {}) (zx/xml1-> zipper :reporting))
          dependency-management (from-xml (map->DependencyManagement {}) (zx/xml1-> zipper :dependencyManagement))
          distribution-management (from-xml (map->DistributionManagement {}) (zx/xml1-> zipper :distributionManagement))
          properties (parse-pom-properties (zx/xml1-> zipper :properties))]
          (Project. parent model-version group-id artifact-id packaging name version description url prerequisites
                issue-management ci-management inception-year mailing-lists developers contributors licenses
                scm organization build profiles modules repositories plugin-repositories 
                dependencies reports reporting dependency-management distribution-management properties
                nil)))
  (to-xml [this]
    (pom/project {}
                 (when (:model-version this) (pom/model-version {} (:model-version this)))
                 (when (:group-id this) (pom/group-id {} (:group-id this)))
                 (pom/artifact-id {} (:artifact-id this))
                 (when (:version this) (pom/version {} (:version this)))
                 (when (:name this) (pom/name {} (:name this)))
                 (when (:description this) (pom/description {} (:description this)))
                 (when (:inception-year this) (pom/inception-year {} (:inception-year this)))
                 (when (:url this) (pom/name {} (:url this)))
                 (when (:parent this) (to-xml (:parent this)))
                 (when (seq (:dependencies this))
                   (pom/dependencies {} (map to-xml (:dependencies this))))
                 (when (seq (:dependencyManagement this))
                   (pom/dependency-management {} (map to-xml (:dependencies this))))
                 (when (seq (:properties this))
                   (pom/properties {} )) ; TODO
                 )))

;
;
;
(defn new-pom 
  ([model-version group-id artifact-id version packaging parent name description url inception-year
    dependencies dependency-management properties parent-pom]
    (Project. parent model-version group-id artifact-id packaging name version description url []
              nil nil inception-year nil nil nil nil nil nil nil nil nil nil nil
              dependencies nil nil dependency-management nil properties
              parent-pom))
  )

(defn new-pom-dependency
  "Create pom dependency."
  ([group-id artifact-id]
    (new-pom-dependency group-id artifact-id nil "jar" nil "compile" nil nil false))
  ([group-id artifact-id version]
    (new-pom-dependency group-id artifact-id version "jar" nil "compile" nil nil false))
  ([group-id artifact-id version type classifier scope system-path exclusions optional]
    (Dependency. group-id artifact-id version
                        (if (nil? type) "jar" type) classifier
                        (if (nil? scope) "compile" scope) system-path exclusions
                        (if (nil? optional) false optional))))

(defn new-pom-exclusion
  "Create pom exclusion."
  ([group-id]
    (new-pom-exclusion group-id nil))
  ([group-id artifact-id]
    (Exclusion. group-id artifact-id)))


;
; pom parsing
;
(defn parse-pom-exclusion [prop-map exclusion]
  "Returns the exclusion data of a POM dependency."
  (let [e ((juxt
             #(replace-properties prop-map (zx/xml1-> % :groupId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :artifactId zx/text)))
            exclusion)]
    (apply new-pom-exclusion e)))

(defn parse-pom-dependency [prop-map dep]
  "Returns the dependency data of a POM dependency."
  (let [group-id (replace-properties prop-map (zx/xml1-> dep :groupId zx/text))
        artifact-id (replace-properties prop-map (zx/xml1-> dep :artifactId zx/text))
        version (replace-properties prop-map (zx/xml1-> dep :version zx/text))
        type (replace-properties prop-map (zx/xml1-> dep :type zx/text))
        classifier (replace-properties prop-map (zx/xml1-> dep :classifier zx/text))
        scope (replace-properties prop-map (zx/xml1-> dep :scope zx/text))
        system-path (replace-properties prop-map (zx/xml1-> dep :systemPath zx/text))
        exclusions (map (partial parse-pom-exclusion prop-map) (zx/xml-> dep :exclusions :exclusion))
        optional (replace-properties prop-map (zx/xml1-> dep :optional zx/text))
        ]
    (new-pom-dependency group-id artifact-id version type classifier scope system-path exclusions optional)))

(defn parse-pom-parent [prop-map zipped]
  "Returns the parent POM artifact data if it exists."
  (if-let [parent (zx/xml1-> zipped :parent)]
    (let [p ((juxt
             #(replace-properties prop-map (zx/xml1-> % :groupId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :artifactId zx/text))
             #(replace-properties prop-map (zx/xml1-> % :version zx/text)))
            parent)]
      p)))

(defn parse-pom
  ([zipped]
    (parse-pom zipped nil))
  ([zipped parent-pom]
    "Returns the relevant data of the POM."
    (let [; merge parent properties with properties from pom
          prop-map (merge (:properties parent-pom) (parse-pom-properties zipped))
          ; merge properties with common project properties
          p-map (merge prop-map {:project.groupId (replace-properties prop-map (zx/xml1-> zipped :groupId zx/text)
                                                                       (:project.groupId prop-map))
                                 :project.artifactID (replace-properties prop-map (zx/xml1-> zipped :artifactId zx/text)
                                                                          (:project.artifactID prop-map))
                                 :project.version (replace-properties prop-map (zx/xml1-> zipped :version zx/text)
                                                                       (:project.version prop-map))
                                 :pom.groupId (replace-properties prop-map (zx/xml1-> zipped :groupId zx/text)
                                                                   (:pom.groupId prop-map))
                                 :pom.artifactID (replace-properties prop-map (zx/xml1-> zipped :artifactId zx/text)
                                                                      (:pom.artifactID prop-map))
                                 :pom.version (replace-properties prop-map (zx/xml1-> zipped :version zx/text)
                                                                       (:pom.version prop-map))})
          ; read pom data with property replacement
          model-version (replace-properties p-map (zx/xml1-> zipped :modelVersion zx/text))
          group-id (replace-properties p-map (zx/xml1-> zipped :groupId zx/text))
          artifact-id (replace-properties p-map (zx/xml1-> zipped :artifactId zx/text))
          version (replace-properties p-map (zx/xml1-> zipped :version zx/text))
          packaging (replace-properties p-map (zx/xml1-> zipped :packaging zx/text))
          parent (parse-pom-parent p-map zipped)
          name (replace-properties p-map (zx/xml1-> zipped :name zx/text))
          description (replace-properties p-map (zx/xml1-> zipped :description zx/text))
          url (replace-properties p-map (zx/xml1-> zipped :url zx/text))
          inception-year (replace-properties p-map (zx/xml1-> zipped :inceptionYear zx/text))
          dependencies (map (partial parse-pom-dependency p-map) (zx/xml-> zipped :dependencies :dependency))
          dependencyManagement (map (partial parse-pom-dependency p-map)
                                    (zx/xml-> zipped :dependencyManagement :dependencies :dependency))
          ]
      (new-pom model-version group-id artifact-id version packaging parent name description url inception-year
               dependencies dependencyManagement p-map parent-pom))))
  
;TODO use prop-map from parent pom
(defn pom-parent? [zipped]
  (not (nil? (zx/xml1-> zipped :parent))))

(defn pom-parent-data [prop-map zipped]
  (parse-pom-parent (parse-pom-properties prop-map zipped) zipped))

(defn pom-dependencies-data
  "Returns the dependencies data of the POM."
  [pom]
  (let [dependencies (:dependencies pom)]
    (map pom-dependency-data dependencies)))
