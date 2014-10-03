(ns om-canvas.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(enable-console-print!)

(defn init-state [n]
  (set (take n (repeatedly (fn [] [(rand-int 400) (rand-int 400)])))))

(def app-state (atom {:state (init-state 5000)}))


(defn neighbors [[x y]]
  (for [dx [-1 0 1] dy (if (zero? dx) [-1 1] [-1 0 1])]
    [(+ dx x) (+ dy y)]))

(def neighbors-memo (memoize neighbors))

(defn step [cells]
  (set (for [[loc n] (frequencies (mapcat neighbors-memo cells))
             :when (or (= n 3) (and (= n 2) (cells loc)))]
         loc)))


(defn render-cells [context world]
  (set! (.-fillStyle context) "#FFFFFF")
  (.fillRect context 0 0 800 800)
  (set! (.-fillStyle context) "#000000")
  (doseq [[x y] world]
    (.fillRect context (* x 2) (* y 2) 2 2)))

(defn update [owner dom-node-ref app-state]
  (let [canvas (om/get-node owner dom-node-ref)
        context (.getContext canvas "2d")
        world app-state]
    (render-cells context world)))

(om/root
  (fn [app owner]
    (reify 
      om/IInitState
      (init-state [_]
        {:state (init-state 10000)})
      om/IWillMount
      (will-mount [_]
        (let [benchmark-time (async/timeout 60000)]
          (m/go-loop [t (async/timeout 10)
                      game-count 0]
                     (let [[v ch] (async/alts! [t benchmark-time])]
                       (if (= ch benchmark-time)
                         (do (println "States generated:" game-count)
                             (println "States per second:" (/ game-count 60.0)))
                         (do (om/update-state! owner [:state] step)
                             (recur (async/timeout 10) (inc game-count))))))))
      om/IDidMount
      (did-mount [_]
        (update owner "main-canvas-ref" (om/get-state owner [:state])))
      om/IDidUpdate
      (did-update [_ _ _]
        (update owner "main-canvas-ref" (om/get-state owner [:state])))
      om/IRender
      (render [_]
        (dom/canvas #js {:id "main-canvas"
                         :height 800
                         :width 800
                         :ref "main-canvas-ref"}))))
  app-state
  {:target (. js/document (getElementById "app"))})
