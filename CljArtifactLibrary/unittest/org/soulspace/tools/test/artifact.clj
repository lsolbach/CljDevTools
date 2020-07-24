;;
;;   Copyright (c) Ludger Solbach. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file license.txt at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.
;;

(ns org.soulspace.tools.test.artifact
  (:use [clojure.test]
        [org.soulspace.tools.artifact]))

(comment
 (deftest match-identifier-true
   (is (true? (identifier-match? nil "module")))
   (is (true? (identifier-match? "" "module")))
   (is (true? (identifier-match? "module" "module"))))

 (deftest match-identifier-false
   (is (false? (identifier-match? "module1" "module"))))

 (deftest match-type-true
   (is (true? (type-match? nil "jar")))
   (is (true? (type-match? "" "jar")))
   (is (true? (type-match? "jar" "jar"))))

 (deftest match-type-false
   (is (false? (type-match? "zip" "jar")))))


; TODO enhance tests for artifact matching
(deftest match-artifact-true
  (is (true? (matches-artifact? (new-artifact-pattern ["" "" ""]) (new-artifact ["org.soulspace" "module" "1.0.0"]))))
  (is (true? (matches-artifact? (new-artifact-pattern ["org.soulspace" "" ""]) (new-artifact ["org.soulspace" "module" "1.0.0"]))))
  (is (true? (matches-artifact? (new-artifact-pattern ["org.soulspace" "module" ""]) (new-artifact ["org.soulspace" "module" "1.0.0"]))))
  (is (true? (matches-artifact? (new-artifact-pattern ["org.soulspace" "module" "1.0.0"]) (new-artifact ["org.soulspace" "module" "1.0.0"]))))
  (is (true? (matches-artifact? (new-artifact-pattern ["org.soulspace" "module" "1.0.0"]) (new-artifact ["org.soulspace" "module" "1.0.0" "module" "jar"])))))


(deftest match-artifact-false
  (is (false? (matches-artifact? (new-artifact-pattern ["org.apache" "" ""]) (new-artifact ["org.soulspace" "module" "1.0.0"]))))
  (is (false? (matches-artifact? (new-artifact-pattern ["" "module1" ""]) (new-artifact ["org.soulspace" "module" "1.0.0"]))))
  (is (false? (matches-artifact? (new-artifact-pattern ["" "" "1.1.0"]) (new-artifact ["org.soulspace" "module" "1.0.0"]))))
  (is (false? (matches-artifact? (new-artifact-pattern ["org.apache" "module" "1.0.0"]) (new-artifact ["org.soulspace" "module" "1.0.0"]))))
  (is (false? (matches-artifact? (new-artifact-pattern ["org.soulspace" "module1" "1.0.0"]) (new-artifact ["org.soulspace" "module" "1.0.0"]))))
  (is (false? (matches-artifact? (new-artifact-pattern ["org.soulspace" "module" "1.1.0"]) (new-artifact ["org.soulspace" "module" "1.0.0"])))))


;(run-tests)
