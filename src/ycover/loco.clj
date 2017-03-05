(ns ycover.loco
  (:refer-clojure :exclude [cond])
  (:require [better-cond.core :refer [cond]]
            [clojure.set :refer [union intersection]]
            [ycover.core :refer :all]
            [loco.automata :as a]
            [loco.core :as loco]
            [loco.constraints :refer :all]))

(defn var-declarations [placements]
  (for [placement placements]
    ($in [:choose placement] 0 1)))

(defn build-constraints [placements]
  (for [[row col] (apply union placements)
        :let [cell [row col]]]
    ($= 1 (apply $+ (for [placement placements
                          :when (contains? placement cell)]
                      [:choose placement])))))

(def model-10 (concat (var-declarations all-possible-Y-placements-10) (build-constraints all-possible-Y-placements-10)))
(def model-15 (concat (var-declarations all-possible-Y-placements-15) (build-constraints all-possible-Y-placements-15)))

(defn solve-model [model]
  (into [] (comp (filter #(= 1 (val %)))
                 (map #(nth (key %) 1)))
        (loco/solution model)))

; (solve-model model-10)

;; Coloring problem

(defn one-away [[row col]]
  #{[(inc row) col] [(dec row) col] [row (inc col)] [row (dec col)]})

(defn touching? [placement1 placement2]
  (and (zero? (count (intersection placement1 placement2)))
       (pos? (count (intersection (apply union (map one-away placement1)) placement2)))))

(defn color-var-declarations [placements]
  (for [placement placements]
    ($in [:color placement] 0 2)))

(defn build-color-constraints [placements]
  (for [placement1 placements,
        placement2 placements
        :when (touching? placement1 placement2)]
    ($if ($= 2 ($+ [:choose placement1] [:choose placement2]))
         ($!= [:color placement1] [:color placement2]))))

(def color-model-10 (concat (var-declarations all-possible-Y-placements-10)
                            (build-constraints all-possible-Y-placements-10)
                            (color-var-declarations all-possible-Y-placements-10)
                            (build-color-constraints all-possible-Y-placements-10)))

;; Regex version

(defn n-placements
  [N M]
  (let [area (* N M)
        P (/ area (count Y))]
    (when-not (integer? P)
      (throw (IllegalArgumentException. "Area of grid not divisible by area of tile")))
    P))

(defn var-declarations
  [N M]
  (let [P (n-placements N M)]
    (for [i (range N)
          j (range M)]
      ($in [:placement-id i j] 0 (dec P)))))

(comment
  ;; For the following tile symmetry:
  A A A A
      A

  ;; and `tile-index=0, M=5, P=3`, the regex will be this:
  "@0000[^0][^0][^0][^0]0[^0][^0][^0]@|[0-2]*"

  ;; the regex alphabet will be:
  #{0 1 2 1000}

  ;; For the following grid `N=4, M=5` (with a sample placement for clarity)
  A A A A *
  * * A * *
  * * * * *
  * * * * *

  ;; The following sequence of vars will be compared with the regex:
  A A A A * 1000 * * A * * 1000 * * * * * 1000 * * * * * 1000
  ;; Each line has an extra `1000` sentinel appended on, to avoid
  ;; weird line-wrapping tile placements. So the regex needs to
  ;; account for one additional "column"
  )

(def eol-sentinel 1000)

(defn tile-automaton
  [tile placement-id N M P]
  (let [tile-N (inc (apply max (map first tile)))
        this-id (str "<" placement-id ">")
        not-this-id (str "[^" this-id "]")]
    (a/string->automaton
      (str
        not-this-id "*"
        (apply str (for [i (range tile-N)
                         j (range (inc M)) ; account for extra sentinel
                         ]
                     (if (contains? tile [i j])
                       this-id
                       not-this-id)))
        not-this-id "*"
        ;; `[^i]` or `@` implies an unbounded theoretical range of
        ;; terminals allowed, which will hang the automaton generation
        ;; which tries to enumerate all legal terminals for each state
        ;; transition. So we have to intersect the above range with
        ;; the following range:
        "&[<" eol-sentinel ">0-" "<" (dec P) ">" "]*"))))

(defn grid->regex-input
  [N M]
  (apply concat
         (interleave
           (for [i (range N)]
             (for [j (range M)]
               [:placement-id i j]))
           (repeat [eol-sentinel]))))

(defn placement-constraint
  [placement-id N M P]
  (let [regex-input (grid->regex-input N M)
        auto
        (a/minimize!
          (reduce a/union
                  (for [sym all-symmetries-of-Y]
                    (tile-automaton sym placement-id N M P))))]
    ($regular auto regex-input)))

(defn build-constraints
  [N M]
  (let [P (n-placements N M)]
    (for [placement-id (range P)]
      (placement-constraint placement-id N M P))))

(def model-10 (concat (var-declarations 10 10)
                      (build-constraints 10 10)))
(def model-15 (concat (var-declarations 15 15)
                      (build-constraints 15 15)))

(defn solve-model
  [model]
  (when-let [sol (loco/solution model)]
    (let [id->placement
          (apply merge-with into
                 (for [[[_ i j] placement-id] sol]
                   {placement-id #{[i j]}}))]
      (mapv id->placement (range (count id->placement))))))

#_ (time (print-solution (solve-model model-10)))
