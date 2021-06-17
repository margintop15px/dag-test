(ns dag-next.core
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [react :as react]
            [react-flow-renderer :default ReactFlow :refer [isNode]]
            [dagre :default dagre]
            [applied-science.js-interop :as jsi]))


(def graph
  (dagre/graphlib.Graph.))

(.setDefaultEdgeLabel graph (fn [] #js {}))

(def node-width 172)
(def node-height 36)

(defn is-node [el]
  (and (some? (el :id))
       (not (el :source))
       (not (el :target))))


(defn get-elements [elems direction]
  (.setGraph graph #js {:rankdir direction})

  (doseq [el elems]
    (if (is-node el)
      (.setNode graph (:id el) #js {:width node-width :height node-height})
      (.setEdge graph (el :source) (el :target))))

  (dagre/layout graph)

  (mapv
   (fn [el]
     (if (is-node el)
       (let [node-with-pos (.node graph (:id el))]
         (-> el
             (assoc :targetPosition "left")
             (assoc :sourcePosition "right")
             (assoc :position {:x (+ (- (jsi/get node-with-pos :x) (/ node-width 2))
                                     (/ (Math/random) 1000))
                               :y (- (jsi/get node-with-pos :y) (/ node-height 2))})))
       el))
   elems))

(for [n (range 10)]
  {:id       (str n),
   :data     {:label (str n " Node")},
   :position {:x 0, :y 0},})

(def initial-elems
  (concat
   (for [n (range 1000)]
     {:id       (str n),
      :data     {:label (str n " Node")},
      :position {:x 0, :y 0},})
   (for [n (range 500)
         :let [rn (rand-int 399)]]
     {:id     (str "e" n "-" rn),
      :source (str n)
      :target (str rn)
      :type   "smoothstep"})))


(def elems
  (get-elements initial-elems "LR"))


(defn app []
  (let [[elements, setElements] (react/useState (clj->js elems))]
    [:div {:style {:height 800 :border "1px solid black"}}
     [:> ReactFlow {:elements           elements
                    :connectionLineType "smoothstep"}]]))


(def development?
  ^boolean goog.DEBUG)


(defn mount []
  (rd/render [:f> app] js/app))


(defn on-update []
  (mount))


(defn ^:export main []
  (when development?
    (enable-console-print!))

  (mount))
