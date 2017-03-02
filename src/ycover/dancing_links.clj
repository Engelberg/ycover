(ns ycover.dancing-links
  (:require [tarantella.core :refer [dancing-links]]))

(def Y-matrix (zipmap all-possible-Y-placements all-possible-Y-placements))

;(time (dancing-links Y-matrix :limit 1))
;(time (count (dancing-links Y-matrix)))

