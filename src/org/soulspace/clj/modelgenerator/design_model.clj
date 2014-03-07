;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.modelgenerator.design-model
  (:use [clojure.java.io :exclude [delete-file]]
        [clojure.set :only [intersection]]
        [org.soulspace.clj string namespace])
  (:import [java.io File]
           [org.soulspace.template.impl TemplateEngineImpl]
           [org.soulspace.template.datasource.impl BeanDataSourceImpl]
           [org.soulspace.modelling.repository ModelRepository]
           [org.soulspace.modelling.repository.elements Model ModelElement]
           [org.soulspace.modelling.repository.impl ModelRepositoryImpl]
           [org.soulspace.modelling.uml14.impl Uml14RepositoryImpl Xmi12ReaderImpl]
           [org.soulspace.modelling.repository.builder.uml14.impl Uml14ModelBuilderImpl]))

(defn print-element [element]
  (println (.getElementType element) (.getId element) (.getName element) (.getParentElement element)))

; model specific functions
(defn element-namespace [gen element]
  (if (:useNameAsNamespace gen)
    (.getQualifiedName element)
    (.getNamespace element)))

(defn base-name [name-str element]
  (if (starts-with "[" name-str)
    (cond
      (or (= name-str "[EMPTY]") (= name-str "[]"))
      ""
      (= name-str "[ELEMENT_NAME]")
      (.getName element)
      (= name-str "[ELEMENT_NAMESPACE]")
      (.getNamespace element)
      (= name-str "[ELEMENT_QNAME]")
      (.getQualifiedName element)
      (= name-str "[ELEMENT_ID]")
      (.getId element)
      (= name-str "[PARENT_NAME]")
      (if-let [parent (.getParentElement element)]
        (.getName parent)
        "[PARENT_NAME]")
      (= name-str "[PARENT_NAMESPACE]")
      (if-let [parent (.getParentElement element)]
        (.getNamespace parent)
        "[PARENT_NAMESPACE]")
      (= name-str "[PARENT_QNAME]")
      (if-let [parent (.getParentElement element)]
        (.getQualifiedName parent)
        "[PARENT_QNAME]")
      (= name-str "[PARENT_ID]")
      (if-let [parent (.getParentElement element)]
        (.getId parent)
        "[PARENT_ID]")
      (= name-str "[MODEL_NAME]")
      (loop [candidate element]
        (if (and (not (nil? candidate)) (= (.getElementType candidate) "Model"))
          (.getName candidate)
          (if (nil? candidate)
            "[MODEL_NAME]"
            (recur (.getParentElement candidate))))))
    name-str))

(defn namespace-path [gen element]
  ; must be consistent with method namespace() in model/lib.tinc
  (str 
    (when (:subdir gen)
      (str (:subdir gen) "/"))
    (when (:namespacePrefix gen)
      (str (ns-to-path (:namespacePrefix gen)) "/"))
    (if (:baseNamespace gen)
      (if (:useNameAsNamespace gen)
        (str (ns-to-path (base-name (:baseNamespace gen) element)) "/" (.getName element) "/")
        (str (ns-to-path (base-name (:baseNamespace gen) element)) "/"))
      (if-not (nil? (element-namespace gen element))
        (str (ns-to-path (element-namespace gen element)) "/")))
    (when (:namespaceSuffix gen)
      (str (ns-to-path (:namespaceSuffix gen)) "/"))))
  
(defn name-path [gen element]
  (str
    (:prefix gen)
    (if (:baseName gen)
      (base-name (:baseName gen) element)
      (.getName element))
    (:suffix gen)
    (when (:extension gen)
      (str "." (:extension gen)))))

(defn model-element? [e]
  (let [result (not (.getIsProfileElement e))]
;    (println "model-element?" result)
    result))

(defn stereotype-set [stereotypes]
  (cond
    (set? stereotypes)
    stereotypes
    (string? stereotypes)
    #{stereotypes}
    :default
    (set stereotypes)))

(defn generate-for-stereotype? [gen element]
  (if-not (seq (:stereotypes gen))
    true ; no stereotype constraint
    (let [s-el (into #{} (map #(.getName %) (.getStereotypeSet element)))
          s-gen (stereotype-set (:stereotypes gen))
          result (cond
                   (nil? s-gen)
                   true ; unconstrained
                   (and (contains? s-gen "ALL") (seq s-el))
                   true ; for any element with a stereotype set
                   (and (contains? s-gen "NONE") (empty? s-el))
                   true ; for any element without stereotypes
                   (set? s-gen) ; for any element with a stereotype from the set of stereotypes
                   (not (empty? (intersection s-gen s-el)))
                   :default
                   false)]
      ;(println "generate for stereotypes gen" s-gen "element" s-el "result" result)
      result)))

(defn equal-values? [m1 m2 k]
  (= (m1 k) (m2 k)))

(defn generate-for-tagged-value? [gen element]
  (if (seq (:taggedValues gen))
    (let [t-el (into {} (.getTaggedValueMap element))
          t-gen (:taggedValues gen)
          key-list (keys t-gen)]
      (every? true? (map (partial equal-values? t-el t-gen) key-list)))
    true))
  
(defn generate-for-namespace? [gen element]
  (let [element-ns (element-namespace gen element)
        ; TODO enhance for seqs in :namespaceIncludes and :namespaceExcludes
        result (cond
                 (and (empty? (:namespaceIncludes gen)) (empty? (:namespaceExcludes gen)))
                 true ; unconstrained
                 (and (not (empty? (:namespaceIncludes gen)))
                      (starts-with (:namespaceIncludes gen) element-ns))
                 true ; on include list
                 (and (not (empty? (:namespaceExcludes gen)))
                      (starts-with (:namespaceIncludes gen) element-ns))
                 false ; on exclude list
                 :default true)]
    ; (println "generate for namespace" result)
    result))

(defn must-generate? [gen element]
  ; (println "checking generation for" (.getName element))
  (let [result (and
                 (model-element? element)
                 (generate-for-stereotype? gen element)
                 (generate-for-tagged-value? gen element)
                 (generate-for-namespace? gen element))]
    ; (println "must generate for element" element "?" result)
    result))
        
(defn element-seq [gen model]
  ; (println "getting elements of type" (:element gen) "for generator" gen)
  (let [elements (cond
    (= "ActionSequence" (:element gen)) (seq (.getActionSequenceList model))
    (= "Actor" (:element gen)) (seq (.getActorList model))
    (= "Association" (:element gen)) (seq (.getAssociationList model))
    (= "AssociationClass" (:element gen)) (seq (.getAssociationClassList model))
    (= "AssociationEnd" (:element gen)) (seq (.getAssociationEndList model))
    (= "Attribute" (:element gen)) (seq (.getAttributeList model))
    (= "CallAction" (:element gen)) (seq (.getCallActionList model))
    (= "CallEvent" (:element gen)) (seq (.getCallEventList model))
    (= "Class" (:element gen)) (seq (.getClassList model))
    (= "Comment" (:element gen)) (seq (.getCommentList model))
    (= "Component" (:element gen)) (seq (.getComponentList model))
    (= "CompositeState" (:element gen)) (seq (.getCompositeStateList model))
    (= "Constraint" (:element gen)) (seq (.getConstraintList model))
    (= "CreateAction" (:element gen)) (seq (.getCreateActionList model))
    (= "DataType" (:element gen)) (seq (.getDataTypeList model))
    (= "Dependency" (:element gen)) (seq (.getDependencyList model))
    (= "DestroyAction" (:element gen)) (seq (.getDestroyActionList model))
    (= "Enumeration" (:element gen)) (seq (.getEnumerationList model))
    (= "Expression" (:element gen)) (seq (.getExpressionList model))
    (= "Extend" (:element gen)) (seq (.getExtendList model))
    (= "ExtensionPoint" (:element gen)) (seq (.getExtensionPointList model))
    (= "FinalState" (:element gen)) (seq (.getFinalStateList model))
    (= "Flow" (:element gen)) (seq (.getFlowList model))
    (= "Generalization" (:element gen)) (seq (.getGeneralizationList model))
    (= "Guard" (:element gen)) (seq (.getGuardList model))
    (= "Include" (:element gen)) (seq (.getIncludeList model))
    (= "Interface" (:element gen)) (seq (.getInterfaceList model))
    (= "Model" (:element gen)) (seq (.getModelList model))
    (= "Multiplicity" (:element gen)) (seq (.getMultiplicityList model))
    (= "Node" (:element gen)) (seq (.getNodeList model))
    (= "Operation" (:element gen)) (seq (.getOperationList model))
    (= "Package" (:element gen)) (seq (.getPackageList model))
    (= "Parameter" (:element gen)) (seq (.getParameterList model))
    (= "Pseudostate" (:element gen)) (seq (.getPseudostateList model))
    (= "ReturnAction" (:element gen)) (seq (.getReturnActionList model))
    (= "SendAction" (:element gen)) (seq (.getSendActionList model))
    (= "Signal" (:element gen)) (seq (.getSignalList model))
    (= "SignalEvent" (:element gen)) (seq (.getSignalEventList model))
    (= "SimpleState" (:element gen)) (seq (.getSimpleStateList model))
    (= "State" (:element gen)) (seq (.getStateList model))
    (= "StateMachine" (:element gen)) (seq (.getStateMachineList model))
    (= "Stereotype" (:element gen)) (seq (.getStereotypeList model))
    (= "SubmachineState" (:element gen)) (seq (.getSubmachineStateList model))
    (= "SubState" (:element gen)) (seq (.getSubStateList model))
    (= "Subsystem" (:element gen)) (seq (.getSubsystemList model))
    (= "SynchState" (:element gen)) (seq (.getSynchStateList model))
    (= "TagDefinition" (:element gen)) (seq (.getTagDefinitionList model))
    (= "TaggedValue" (:element gen)) (seq (.getTaggedValueList model))
    (= "TerminateAction" (:element gen)) (seq (.getTerminateActionList model))
    (= "TimeEvent" (:element gen)) (seq (.getTimeEventList model))
    (= "Transition" (:element gen)) (seq (.getTransitionList model))
    (= "TimeEvent" (:element gen)) (seq (.getTimeEventList model))
    (= "UninterpretedAction" (:element gen)) (seq (.getUninterpretedActionList model))
    (= "UseCase" (:element gen)) (seq (.getUseCaseList model))
    :default (println "TODO: add" (:element gen) "to element-seq")
    ; TODO add other elements (if any?) 
    )]
    ;(if-not (seq elements)
    ;  (println "INFO: No elements found in model for generator" gen "!"))
    elements))

; UML/XMI repository initialization
(defn create-uml-repository []
  (Uml14RepositoryImpl.))

(defn create-xmi-reader [uml-repository]
  (Xmi12ReaderImpl. uml-repository))

(defn create-model-repository []
  (ModelRepositoryImpl.))

(defn create-uml-modelbuilder [uml-repository model-repository]
  (Uml14ModelBuilderImpl. uml-repository model-repository))

(defn load-profile [xmi-reader profile]
  (.loadProfile xmi-reader profile)
  xmi-reader)

(defn load-profiles [xmi-reader profiles]
  (if (seq profiles)
    (.loadProfiles xmi-reader (into-array profiles)))
  xmi-reader)

(defn load-model [xmi-reader xmi]
  (.loadModel xmi-reader xmi)
  xmi-reader)

(defn initialize-xmi [ctx]
  (let [xmi-reader (create-xmi-reader (create-uml-repository))]
    (load-profiles xmi-reader (map as-file (:profiles ctx)))
    (load-model xmi-reader (as-file (str (:model ctx) ".xmi")))
    (.getXmiRepository xmi-reader)))

(defn initialize-model [ctx]
  (let [uml-repository (initialize-xmi ctx)
        model-repository (create-model-repository)]
    (let [model-builder (create-uml-modelbuilder uml-repository model-repository)]
      (.build model-builder))))
