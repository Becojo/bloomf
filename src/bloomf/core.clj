(ns bloomf.core
  (:refer-clojure :exclude [contains?]))

(defn make [size hashers]
  "Creates a Bloom filter of the given size using the given hash functions"
  { :hashers hashers
    :size    size
    :value   0 })

(defn add
  "Adds x to the Bloom filter"
  ([{:keys [value size hashers] :as bloom} x]
   (assoc bloom :value
     (reduce bit-set value (map #(mod (% x) size) hashers))))

  ([bloom x & xs] (reduce add (add bloom x) xs)))

(defn contains?
  "Returns false if n is not in the Bloom filter"
  [{:keys [value size hashers]} x]
   (empty? (drop-while #(bit-test value (mod (% x) size)) hashers)))

(defn intersection
  "Returns a Bloom filter that is the intersection of its inputs"
  ([f1 f2]
   (assoc f1 :size  (min (:size f1) (:size f2))
             :value (bit-and (:value f1) (:value f2))))
  ([f1 f2 & fs]
   (reduce intersection (intersection f1 f2) fs)))

(defn union
  "Returns a Bloom filter that is the union of the inputs"
  ([f1 f2]
   (assoc f1 :size (max (:size f1) (:size f2))
             :value (bit-or (:value f1) (:value f2))))
  ([f1 f2 & fs]
   (reduce union (union f1 f2) fs)))

(defn shrink
  "Split the Bloom filter in half"
  [{:keys [value size] :as bloom}]
   (let [half-size (quot size 2)]
     (assoc bloom
       :size half-size
       :value (bit-or
                (bit-shift-right value half-size)
                (bit-and value (dec (bit-shift-left 1 (inc half-size))))))))
