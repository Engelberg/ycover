(ns ycover.viz
  (:refer-clojure :exclude [cond])
  (:require [better-cond.core :refer [cond defnc]])
  (:use [analemma svg charts xml]
        [tikkba swing dom transcoder])
  (:import (javax.swing JFrame SwingUtilities)))

(defn rand-color []
  (rgb (rand-int 256) (rand-int 256) (rand-int 256)))

(defn rand-colors []
  (repeatedly rand-color))

(def colors
  ["rgb(131,129,62)"
   "rgb(75,29,44)"
   "rgb(208,191,31)"
   "rgb(254,14,29)"
   "rgb(198,243,137)"
   "rgb(250,28,245)"
   "rgb(148,181,135)"
   "rgb(171,83,249)"
   "rgb(148,190,231)"
   "rgb(82,55,177)"
   "rgb(186,175,14)"
   "rgb(106,39,169)"
   "rgb(243,60,247)"
   "rgb(220,212,66)"
   "rgb(1,153,49)"
   "rgb(148,242,221)"
   "rgb(227,4,182)"
   "rgb(98,172,211)"
   "rgb(69,98,254)"
   "rgb(249,154,170)"
   "rgb(201,116,96)"
   "rgb(90,157,92)"
   "rgb(138,196,95)"
   "rgb(64,101,199)"
   "rgb(219,201,211)"
   "rgb(1,17,115)"
   "rgb(92,78,149)"
   "rgb(155,114,11)"
   "rgb(156,39,194)"
   "rgb(177,79,113)"
   "rgb(197,144,195)"
   "rgb(74,180,129)"
   "rgb(162,70,6)"
   "rgb(174,195,201)"
   "rgb(26,190,71)"
   "rgb(211,34,246)"
   "rgb(129,164,165)"
   "rgb(56,245,38)"
   "rgb(205,250,198)"
   "rgb(17,131,218)"
   "rgb(176,164,235)"
   "rgb(166,68,251)"
   "rgb(12,55,66)"
   "rgb(228,161,236)"
   "rgb(79,101,226)"])

(def colors (rand-colors))


(declare analemma-data)

(defnc ycover-svg
  "Creates a SVG representation of Y cover"
  [placements gridsize]
  :let [size (/ 600 gridsize)]
  (svg
    (apply group
           (mapcat (fn [placement color]
                     (for [[row col] placement]
                       (rect (* col size) (* row size) size size
                             :stroke "gray"
                             :fill color)))
                   placements colors))))

(defnc ycover-colors-svg
  "Creates a SVG representation of Y cover with colors"
  [color-map gridsize]
  :let [size (/ 600 gridsize)]
  (svg
    (apply group
           (mapcat (fn [[placement color]]
                     (for [[row col] placement]
                       (rect (* col size) (* row size) size size
                             :stroke "gray"
                             :fill color)))
                   color-map))))

(defn create-frame
  [canvas]
  (let [frame (JFrame.)]
    (.add (.getContentPane frame) canvas)
    (.setSize frame 620 640)
    ;(.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (SwingUtilities/invokeAndWait
      (fn [] (.setVisible frame true)))))

(defn display [svg]
  ;; Converts the SVG representation to a XML Document
  ;; and displays the SVG in a JFrame
  (let [doc (svg-doc svg)
        canvas (jsvgcanvas)]
    (set-document canvas doc)
    (create-frame canvas)))

(defn save [svg name]
  ;; using PNG transcoder
  (to-png (svg-doc svg) name {:width 600 :height 600}))
