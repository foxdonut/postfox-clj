(ns postfox.core-test
  (:require
    [clojure.test :refer :all]
    [postfox.core :refer :all]))

(deftest arity-test
  (testing "arity"
    (is (= 2 (arity +)))
    (is (= 1 (arity str)))
    (is (= 0 (arity (fn [] {}))))
    (is (= 3 (arity reduce)))))

(deftest postfox-test
  (testing "postfox"
    (testing "function length"
      (is (= 14 (postfox 5 1 2 + 4 * + 3 -))))
    (testing "quotations and function arity"
      (is (= 15 (postfox [1 2 3 4 5] (qtn +) 0 reduce))))
    (testing "partial function application in arity"
      (is (= [10 20 30 40 50] (postfox [1 2 3 4 5] (qtn 10 *) map))))
    (testing "multiple partial function application in quotation"
      (is (= [16 20 24 28 32] (postfox [1 2 3 4 5] (qtn 3 + 4 *) map))))
    (testing "utility functions"
      (is (= 42 (postfox 84 2 /)))
      (is (= 5 (postfox "hello" count)))
      (is (= true (postfox 8 (+ 4 4) =)))
      (is (= [2 4 42] 2 [4 42] cons)))))

