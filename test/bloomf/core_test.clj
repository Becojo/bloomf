(ns bloomf.core-test
  (:require [clojure.test :refer :all]
            [bloomf.core :as bf]))
;; helper

(defn make-bloom-add [& elements]
  (apply bf/add (bf/make 20 [hash]) elements))

;; tests

(deftest contains
  (testing "empty"
    (let [filter (bf/make 10 [hash])]
      (doseq [n (take 10 (repeatedly #(rand-int 250)))]
        (is (not (bf/contains? filter n)))))))

(deftest adding-elements
  (testing "adding single elements"
    (let [filter (-> (bf/make 10 [hash])
                     (bf/add 0)
                     (bf/add 1)
                     (bf/add "abc")
                     (bf/add "ghi"))]

      (are [n] (bf/contains? filter n) 0 1 "abc" "ghi")))

  (testing "adding multiple elements"
    (let [filter (make-bloom-add 0 1 "abc" "ghi")]
      (are [n] (bf/contains? filter n) 0 1 "abc" "ghi"))))

(deftest unions
  (testing "single union"
    (let [b1 (make-bloom-add "abc" "def")
          b2 (make-bloom-add "ghi" "jkl")
          b1b2 (bf/union b1 b2)]

      (are [n] (bf/contains? b1b2 n) "abc" "def" "ghi" "jkl")))

  (testing "multiple unions"
    (let [filters  [(make-bloom-add "012" "345")
                    (make-bloom-add "012" "678")
                    (make-bloom-add "678" "91011")]
          union    (apply bf/union filters)]

      (are [n] (bf/contains? union n) "012" "345" "678" "91011"))))

(deftest intersections
  (let [b1 (make-bloom-add "abc" "def" "ghi" "123" "jkl")
        b2 (make-bloom-add "123" "456" "abc" "789")
        b1b2 (bf/intersection b1 b2)]

    (are [n] (bf/contains? b1b2 n) "123" "abc")
    (are [n] (not (bf/contains? b1b2 n)) "def" "ghi" "jkl" "456" "789")))


(deftest shrink
  (let [filter (make-bloom-add "abc" "def" "ghi")
        shrinked (bf/shrink filter)]
        
    (is (= (:size shrinked) (int (/ (:size filter) 2))))
    (are [n] (bf/contains? shrinked n) "abc" "def" "ghi")))
