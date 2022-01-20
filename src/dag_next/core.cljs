(ns dag-next.core
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [react :as react]
            [react-flow-renderer :default ReactFlow :refer [Controls Background removeElements addEdge]]
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


(defn calc-elements [elems direction]
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
             (assoc :position {:x (+ (- (jsi/get node-with-pos :x) (/ node-width 2))
                                     (/ (Math/random) 1000))
                               :y (- (jsi/get node-with-pos :y) (/ node-height 2))})))
       el))
   elems))


(def initial-elems
  (calc-elements
   [{:id "1", :type "input", :data {:label "Welcome to React Flow!"}}
    {:id "2", :data {:label "This is a default node"}}
    {:id "3", :data {:label "This one has a custom style"}, :style {:background "#D6D5E6", :color "#333", :border "1px solid #222138", :width 180}}
    {:id "4", :data {:label "Another default node"}}
    {:id "5", :data {:label "Node id: 5"}}
    {:id "6", :type "output", :data {:label "An output node"}}
    {:id "7", :type "output", :data {:label "Another output node"}}

    {:id "e1-2", :source "1", :target "2", :label "this is an edge label"}
    {:id "e1-3", :source "1", :target "3"}
    {:id "e3-4", :source "3", :target "4", :animated true, :label "animated edge"}
    {:id "e4-5", :source "4", :target "5", :arrowHeadType "arrowclosed", :label "edge with arrow head"}
    {:id "e5-6", :source "5", :target "6", :type "smoothstep", :label "smooth step edge"}
    {:id "e5-7", :source "5", :target "7", :type "step", :style {:stroke "#f6ab6c"}, :label "a step edge", :animated true, :labelStyle {:fill "#f6ab6c", :fontWeight 700}}]
   "TB"))


(defn on-load [reactFlowInstance]
  (jsi/call reactFlowInstance :fitView))


(defn app []
  (let [[elements, set-elements] (react/useState (clj->js initial-elems))
        on-elements-remove (fn [elementsToRemove]
                             (set-elements #(removeElements elementsToRemove %)))
        on-connect         (fn [params]
                             (set-elements #(addEdge params %)))]
    [:div {:style {:height 800 :border "1px solid black"}}
     [:> ReactFlow
      {:elements         elements
       :onElementsRemove on-elements-remove
       :onConnect        on-connect
       :onLoad           on-load
       :snapToGrid       true
       :snapGrid         [15, 15]}

      [:> Controls]
      [:> Background {:color "#aaa" :gap 16}]]]))






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
