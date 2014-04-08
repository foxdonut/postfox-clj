(ns postfox.core-test
  (:require
    [clojure.test :refer :all]
    [postfox.core :refer :all]))

(deftest arg-count-test
  (testing "arg-count"
    (is (= 2 (arg-count +)))
    (is (= 1 (arg-count str)))
    (is (= 0 (arg-count (fn [] {}))))
    (is (= 3 (arg-count reduce)))))

; regular function definition
(defn avg1 [x y]
  (/ (+ x y) 2))

; postfox function definitions
(def avg2 (postfunc + 2 /))
(def avg3 (postfunc avg2 avg2))

(deftest postfox-test
  (testing "postfox"
    (testing "function length"
      (is (= 14 (postfox 5 1 2 + 4 * + 3 -))))

    (testing "quotations and function arity"
      (is (= 15 (postfox (qtn +) 0 [1 2 3 4 5] reduce))))

    (testing "partial function application in arity"
      (is (= [10 20 30 40 50] (postfox (qtn 10 *) [1 2 3 4 5] (arity 2 map)))))

    (testing "multiple partial function application in quotation"
      (is (= [16 20 24 28 32] (postfox (qtn 3 + 4 *) [1 2 3 4 5] (arity 2 map)))))

    (testing "postfox function definition"
      (is (= 42 (postfox 80 4 avg1)))
      (is (= 42 (postfox 80 4 avg2)))
      (is (= 27 (postfox 2 80 4 avg2 + 10 avg2)))
      (is (= 40 (postfox 10 60 80 avg3))))

    (testing "utility functions"
      (is (= 42 (postfox 84 2 /)))
      (is (= 5 (postfox "hello" count)))
      (is (= true (postfox 8 (+ 4 4) =)))
      (is (= [2 4 42] (postfox 2 [4 42] cons))))))

