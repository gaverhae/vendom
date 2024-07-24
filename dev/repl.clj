(ns repl
  (:require [clojure.tools.namespace.dir :as dir]
            [clojure.tools.namespace.reload :as reload]
            [clojure.tools.namespace.track :as track]
            [figwheel.main.api :as fig]
            [nextjournal.beholder :as beholder]
            [t.core :as t]))

(defonce state (atom {:tracker (track/tracker)}))

(defn refresh-code
  [old-tracker]
  (let [new-tracker (-> (dir/scan old-tracker "src")
                        (assoc ::track/unload [])
                        reload/track-reload)]
    (if-let [e (::reload/error new-tracker)]
      (prn [:refresh/error (-> e Throwable->map :cause)])
      (prn [:refresh/ok]))
    new-tracker))

(defn go
  []
  (swap! state
         (fn [{:keys [figwheel server watcher]}]
           (when server (server))
           (when watcher (beholder/stop watcher))
           (when figwheel (fig/stop-all))
           {:server (t/-main)
            :figwheel (do (fig/start {:mode :serve} "dev")
                          true)
            :watcher (beholder/watch (fn [_] (swap! state update :tracker refresh-code))
                                     "src")})))
