(ns ycover.backtrack
  (:refer-clojure :exclude [cond])
  (:require [better-cond.core :refer [cond]]
            [clojure.set :refer [intersection union]]
            [ycover.core :refer [all-possible-Y-placements print-solution]]))

(defn group-by-cell [placements]
  (into {}
    (for [[row col] (apply union placements)
          :let [cell [row col]]]
      [cell (set (for [placement placements
                       :when (contains? placement cell)]
                   placement))])))

(defn remove-placement-from-groups
  [placement groups]
  (as-> groups groups
    (reduce
      (fn [groups invalid-placement]
        (reduce
          (fn [groups related-cell]
            (update groups related-cell disj invalid-placement))
          groups
          invalid-placement))
      groups
      (for [cell placement
            invalid-placement (groups cell)]
        invalid-placement))
    (reduce dissoc groups placement)))

(defn all-solutions [groups]
  (cond
    (empty? groups) [[]]
    :let [[cell smallest-group] (apply min-key #(count (val %)) groups)]
    (zero? (count smallest-group)) []
    :else (for [placement smallest-group,
                solution (all-solutions (remove-placement-from-groups placement groups))]
            (cons placement solution))))

(defn Y-groups [board-size] (group-by-cell (all-possible-Y-placements board-size)))
(def Y-groups-10 (Y-groups 10))
(def Y-groups-15 (Y-groups 15))

; (time (first (all-solutions Y-groups-10))) 15ms
; (time (count (all-solutions Y-groups-10))) 3.2s
