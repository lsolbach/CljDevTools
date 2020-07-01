;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.clj.modelgenerator.test.protected-areas
  (:use [clojure.test]
        [clojure.string :only [join]]
        [org.soulspace.clj.modelgenerator.protected-areas]))

(def test-lines1
  ["No UFOs anywhere."
   "// PA-BEGIN(area-51)"
   "UFOs!"
   "And aliens with big eyes, by the way!"
   "// PA-END(area-51)"
   "Still no UFOs!"
   "// PA-BEGIN(space)?"
   "Possible that aliens exist."
   "// PA-END(space)?"])


(def test-lines2
  ["No UFOs anywhere."
   "// PA-BEGIN(area-51)"
   "UFOs!"
   "And aliens with big eyes, by the way!"
   "// PA-END(space)?"
   "Still no UFOs!"
   "// PA-BEGIN(space)?"
   "Possible that aliens exist."
   "// PA-END(area-51)"])



(deftest parse-protected-areas-test
  (is (= (parse-protected-areas "PA" test-lines1) {"area-51" "UFOs!\nAnd aliens with big eyes, by the way!\n"
                                                   "space" "Possible that aliens exist.\n"}))
  (is (= (parse-protected-areas "PA" test-lines2) {"area-51"
                                                   (str (join "\n" ["UFOs!"
                                                                    "And aliens with big eyes, by the way!"
                                                                    "// PA-END(space)?"
                                                                    "Still no UFOs!"
                                                                    "// PA-BEGIN(space)?"
                                                                    "Possible that aliens exist."])
                                                        "\n")})))

;(run-tests)
