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
   "// PA-END(space)?"
   ])

(def test-lines2
  ["No UFOs anywhere."
   "// PA-BEGIN(area-51)"
   "UFOs!"
   "And aliens with big eyes, by the way!"
   "// PA-END(space)?"
   "Still no UFOs!"
   "// PA-BEGIN(space)?"
   "Possible that aliens exist."
   "// PA-END(area-51)"
   ])


(deftest parse-protected-areas-test
  (is (= (parse-protected-areas "PA" test-lines1) {"area-51" "UFOs!\nAnd aliens with big eyes, by the way!\n"
                                                  "space" "Possible that aliens exist.\n"}))
  (is (= (parse-protected-areas "PA" test-lines2) {"area-51"
                                                   (str (join "\n" ["UFOs!"
                                                               "And aliens with big eyes, by the way!"
                                                               "// PA-END(space)?"
                                                               "Still no UFOs!"
                                                               "// PA-BEGIN(space)?"
                                                               "Possible that aliens exist."
                                                               ]) "\n")})))

;(run-tests)