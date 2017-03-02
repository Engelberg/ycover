(ns ycover.backtrack
  (:refer-clojure :exclude [cond])
  (:require [better-cond.core :refer [cond]]
            [clojure.set :refer [intersection]]
            [ycover.core :refer [board-size all-possible-Y-placements]]))

(defn group-by-cell [placements]
  (into {}
    (for [row (range board-size), col (range board-size)
          :let [cell [row col]]]
      [cell (for [placement placements
                  :when (contains? placement cell)]
              placement)])))

(defn remove-placement-from-groups [placement groups]
  (into {}
    (for [[cell group] groups
          :when (not (contains? placement cell))]
      [cell (for [p group
                  :when (zero? (count (intersection placement p)))]
              p)])))

(defn all-solutions [groups]
  (cond
    (empty? groups) [[]]
    :let [[cell smallest-group] (apply min-key #(count (val %)) groups)]
    (zero? (count smallest-group)) []
    :else (for [placement smallest-group,
                solution (all-solutions (remove-placement-from-groups placement groups))]
            (cons placement solution))))

(def Y-groups (group-by-cell all-possible-Y-placements))

; (time (first (all-solutions Y-groups))) 38ms
; (time (count (all-solutions Y-groups))) 14s