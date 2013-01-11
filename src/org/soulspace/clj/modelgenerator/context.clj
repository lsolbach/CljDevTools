(ns org.soulspace.clj.modelgenerator.context)

(defn create-generation-context 
  ([model profiles template-dirs dest backup generators]
    {:model model
     :destDir dest
     :backupDir backup
     :templateDirs template-dirs
     :profiles profiles
     :generators generators}))
