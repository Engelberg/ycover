(ns ycover.loco
  (:refer-clojure :exclude [cond])
  (:require [better-cond.core :refer [cond]]
            [clojure.set :refer [union intersection]]
            [ycover.core :refer [all-possible-Y-placements all-possible-Y-placements-10 all-possible-Y-placements-15]]
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