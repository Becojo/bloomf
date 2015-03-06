(ns bloomf.core-test
  (:use [clojure.test.check.clojure-test :only [defspec]])
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.set :as set]
            [bloomf.core :as bf]))

(def choose-filter-size (gen/choose 5 500))

(defspec empty-stays-empty
  (prop/for-all [xs (gen/vector gen/int) n choose-filter-size]
    (let [f (bf/make n [hash])]
      (every? #(not (bf/contains? f %)) xs))))


(defspec add-contains
  (prop/for-all [xs (gen/vector gen/int) n choose-filter-size]
    (let [f (reduce bf/add (bf/make n [hash]) xs)]
      (every? #(bf/contains? f %) xs))))


(defspec union
  (prop/for-all [xs (gen/vector gen/int)
                 ys (gen/vector gen/int)
                 n  choose-filter-size]

    (let [fx (reduce bf/add (bf/make n [hash]) xs)
          fy (reduce bf/add (bf/make n [hash]) ys)
          fxy (bf/union fx fy)]

      (every? #(bf/contains? fxy %) (concat xs ys)))))


(defspec intersection
  (prop/for-all [xs (gen/vector gen/int)
                 ys (gen/vector gen/int)
                 n choose-filter-size]

    (let [intersection (set/intersection (set xs) (set ys))
          fx (reduce bf/add (bf/make n [hash]) xs)
          fy (reduce bf/add (bf/make n [hash]) xs)
          fxy (bf/intersection fx fy)]

      (every? #(bf/contains? fxy %) intersection))))


(defspec shrink
  (prop/for-all [xs (gen/vector gen/int)
                 n (gen/such-that even? (gen/choose 10 40))]
    (let [f (bf/shrink (reduce bf/add (bf/make n [hash]) xs))]
      (every? #(bf/contains? f %) xs))))
