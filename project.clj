(defproject dag-next "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [thheller/shadow-cljs "2.12.5"]
                 [applied-science/js-interop "0.2.7"]
                 [reagent "1.0.0"]
                 [re-frame "1.2.0"]]

  :plugins [[lein-shadow "0.4.0"]]

  :source-paths ["src"]

  :npm-deps [[shadow-cljs "2.12.5"]
             [dagre "0.8.5"]
             [react "17.0.2"]
             [react-dom "17.0.2"]
             [react-flow-renderer "9.6.1"]]

  :shadow-cljs {:nrepl  {:port 3333}
                :builds {:app {:target     :browser
                               :output-dir "public/js"
                               :asset-path "/js"
                               :modules    {:main {:entries [dag-next.core]}}
                               :devtools   {:after-load dag-next.core/on-update
                                            :http-root  "public"
                                            :http-port  3001}}}}

  :repl-options {:init-ns dag-next.core}

  :aliases {"run:frontend" ["shadow" "watch" "app"]})
