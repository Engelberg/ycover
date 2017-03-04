(ns ycover.backtrack
  (:refer-clojure :exclude [cond])
  (:require [better-cond.core :refer [cond]]
            [clojure.set :refer [intersection union]]
            [ycover.core :refer [all-possible-Y-placements]]))

(defn group-by-cell [placements]
  (into {}
    (for [[row col] (apply union placements)
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

(defn Y-groups [board-size] (group-by-cell (all-possible-Y-placements board-size)))
(def Y-groups-10 (Y-groups 10))
(def Y-groups-15 (Y-groups 15))

; (time (first (all-solutions Y-groups-10))) 38ms
; (time (count (all-solutions Y-groups-10))) 14s