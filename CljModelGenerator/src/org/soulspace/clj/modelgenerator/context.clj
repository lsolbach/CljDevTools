;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.modelgenerator.context)

(defn create-generation-context 
  ([model profiles template-dirs dest backup generators]
    {:model model
     :destDir dest
     :backupDir backup
     :templateDirs template-dirs
     :profiles profiles
     :generators generators}))
