(ns ycover.core)

(def Y #{[0 0] [0 1] [0 2] [0 3] [1 2]})

(defn count-rows [coords]
  (inc (apply max (map first coords))))

(defn count-cols [coords]
  (inc (apply max (map second coords))))

(defn reflect-horiz [coords]
  (let [n-cols (count-cols coords)]
    (set (for [[row col] coords]
            [row (- n-cols col 1)]))))

(defn reflect-main-diag [coords]
  (set (for [[row col] coords] [col row])))

(def rotate-cw (comp set reflect-horiz reflect-main-diag))
(def rotate-180 (comp set rotate-cw rotate-cw))
(def rotate-ccw (comp set rotate-cw rotate-180))

(def reflect-vert (comp set reflect-horiz rotate-180))
(def reflect-other-diag (comp set reflect-horiz rotate-ccw))

(def rotations [identity rotate-cw rotate-180 rotate-ccw])
(def reflections [reflect-horiz reflect-vert reflect-main-diag reflect-other-diag])
(def all-symmetries (into rotations reflections))

(def all-symmetries-of-Y ((apply juxt all-symmetries) Y))

(defn all-possible-Y-placements [board-size]
  (for [coords all-symmetries-of-Y,
        :let [num-rows (count-rows coords)
              num-cols (count-cols coords)]
        row-offset (range (inc (- board-size num-rows))),
        col-offset (range (inc (- board-size num-cols)))]
    (set (for [[r c] coords]
           [(+ r row-offset) (+ c col-offset)]))))

(def all-possible-Y-placements-10 (all-possible-Y-placements 10))
(def all-possible-Y-placements-15 (all-possible-Y-placements 15))

