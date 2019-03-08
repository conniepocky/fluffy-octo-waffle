(ns waffle-game.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.math :refer :all]))
            

(def s 0)

(def speed 100)

(declare waffle-game-game main-screen)

(defn- get-direction []
  (cond
    (key-pressed? :a) :left
    (key-pressed? :d) :right))

(defn- update-player-position [{:keys [player?] :as entity}]
  (if player?
     (let [direction (get-direction) 
           new-x (case direction
                  :right (+ (:x entity) speed)
                  :left (- (:x entity) speed))]
          (when (not= (:direction entity) direction)
            (texture! entity :flip true false))
          (assoc entity :x new-x :direction direction))
     entity))

(defn- update-hit-box [{:keys [player? waffle?] :as entity}]
  (if (or player? waffle?)
    (assoc entity :hit-box (rectangle (:x entity) (:y entity) (:width entity) (:height entity)))
    entity))

(defn- remove-touched-waffles [entities]

  (if-let [waffles (filter #(contains? % :waffle?) entities)]
    (let [player (some #(when (:player? %) %) entities)
          touched-waffles (filter #(rectangle! (:hit-box player) :overlaps (:hit-box % )) waffles)]
      (remove (set touched-waffles) entities))
    (entities))

  (def e [(some #(when (:player? %) %) entities) (filter #(contains? % :waffle?) entities)])

  (if (filter #(rectangle! (:hit-box (first e)) :overlaps (:hit-box % )) (last e))
    (def s (inc s))
    (println s)))
    
(defn- move-player [entity]
  (->> entity
    (map (fn [entity]
           (->> entity
                (update-player-position)
                (update-hit-box))))
    (remove-touched-waffles)))

(defn- spawn-waffle []
  (let [x (+ 50 (rand-int 1400))
        y (+ 50 (rand-int 30))]
    (assoc (texture "waffle.png") :x x, :y y, :width 150, :height 100, :waffle? true)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (add-timer! screen :spawn-waffle 1 2)
    (let [background (texture "hogwarts.jpg")
          player (assoc (texture "cat.png") :x 50, :y 50, :width 400, :height 350 :direction :right :player? true)
          score (label (str s) (color :white))]
      [background player score]))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

 :on-key-down
 (fn [screen entities]
     (cond
       (key-pressed? :r) (app! :post-runnable #(set-screen! waffle-game-game main-screen))
       (get-direction) (move-player entities)
       :else entities))

 :on-timer
 (fn [screen entities]
   (case (:id screen)
     :spawn-waffle (conj entities (spawn-waffle)))))


(defgame waffle-game-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(-> main-screen :entities deref)
