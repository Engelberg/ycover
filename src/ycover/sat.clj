(ns ycover.sat
    (:refer-clojure :exclude [cond])
    (:require [better-cond.core :refer [cond]]
              [clojure.set :refer [union]]
              [ycover.core :refer [all-possible-Y-placements all-possible-Y-placements-10 all-possible-Y-placements-15]]
              [rolling-stones.core :as sat :refer [! NOT AND OR XOR IFF IMP NOR NAND at-least at-most exactly]]
              [ycover.viz :as viz]))

(defn build-constraints [placements]
  (for [[row col] (apply union placements)
        :let [cell [row col]]]
    (exactly 1 (for [placement placements
                     :when (contains? placement cell)]
                 placement))))

(def Y-constraints-10 (build-constraints all-possible-Y-placements-10))
(def Y-constraints-15 (build-constraints all-possible-Y-placements-15))

;(time (sat/true-symbolic-variables (sat/solve-symbolic-formula Y-constraints-10)))
;(time (count (sat/solutions-symbolic-formula Y-constraints-10)))

; Exploring double cells

(defn build-constraints-double-cell [placements double-cells]
  (for [[row col] (apply union placements)
        :let [cell [row col]]]
    (if (contains? double-cells cell)
      (exactly 2 (for [placement placements
                       :when (contains? placement cell)]
                   placement))
      (exactly 1 (for [placement placements
                       :when (contains? placement cell)]
                   placement)))))

;(sat/true-symbolic-variables (sat/solve-symbolic-formula (build-constraints-double-cell (all-possible-Y-placements 14) #{[0 0] [3 3] [0 3] [3 0]})))

;; Exploring symmetries

(defn rotate-piece [placement board-size]
  (set (for [[row col] placement]
         [(- board-size row 1) (- board-size col 1)])))

(defn reflect-piece [placement board-size]
  (set (for [[row col] placement]
         [row (- board-size col 1)])))

(defn enforce-symmetries [placements board-size]
  (for [placement placements]
    (IFF placement (rotate-piece placement board-size))))

(def symmetric-Y-constraints-10 (concat Y-constraints-10 (enforce-symmetries all-possible-Y-placements-10 10)))
(def symmetric-Y-constraints-15 (concat Y-constraints-15 (enforce-symmetries all-possible-Y-placements-15 15)))
;(time (sat/true-symbolic-variables (sat/solve-symbolic-formula symmetric-Y-constraints-10)))
;(time (count (sat/solutions-symbolic-formula symmetric-Y-constraints-10)))

(defn enforce-ten-symmetries [placements board-size]
  [(exactly 10 (for [placement placements]
                 (AND placement (reflect-piece placement board-size))))])
(def ten-symmetric-Y-constraints-10 (concat Y-constraints-10 (enforce-ten-symmetries all-possible-Y-placements-10 10)))
(def ten-symmetric-Y-constraints-15 (concat Y-constraints-15 (enforce-ten-symmetries all-possible-Y-placements-15 15)))


;; Examples for slides
;(rolling-stones.core/solve-symbolic-formula
;  (AND (OR :p :q :r :s)
;     (OR (NOT :p) :r (NOT :s))
;     (OR (NOT :q) (NOT :s))
;     (OR :q (NOT :r) :s)))
;
;(rolling-stones.core/solutions-symbolic-formula
;  (AND (OR :p :q :r :s)
;     (OR (NOT :p) :r (NOT :s))
;     (OR (NOT :q) (NOT :s))
;     (OR :q (NOT :r) :s)))
;
;(defn Exactly [n vars]
;  (let [diff (- (count vars) n)]
;    (concat
;      (for [var-comb (c/combinations vars (inc n))]
;        (with-meta (vec (for [var var-comb] (! var))) {:clause :exactly}))
;      (for [var-comb (c/combinations vars (inc diff))]
;        (with-meta (vec (for [var var-comb] var)) {:clause :exactly})))))
