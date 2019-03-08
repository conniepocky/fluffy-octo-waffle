(ns waffle-game.core.desktop-launcher
  (:require [waffle-game.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. waffle-game-game "waffle-game" 1600 750)
  (Keyboard/enableRepeatEvents true))
