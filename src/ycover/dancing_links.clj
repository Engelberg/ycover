(ns ycover.dancing-links
  (:require [ycover.core :refer [all-possible-Y-placements-10 all-possible-Y-placements-15]]
            [tarantella.core :refer [dancing-links]]))

(def Y-matrix-10 (zipmap all-possible-Y-placements-10 all-possible-Y-placements-10))
(def Y-matrix-15 (zipmap all-possible-Y-placements-15 all-possible-Y-placements-15))

;(time (dancing-links Y-matrix-10 :limit 1))
;(time (count (dancing-links Y-matrix-10)))

;(dancing-links
;  [[0 0 1 0 1 1 0]
;   [1 0 0 1 0 0 1]
;   [0 1 1 0 0 1 0]
;   [1 0 0 1 0 0 0]
;   [0 1 0 0 0 0 1]
;   [0 0 0 1 1 0 1]])
