(ns ^:figwheel-hooks t.core
  (:require [cljs.core.async :as async]
            [cljs.core.match :refer-macros [match]]
            [goog.events]
            [reagent.core :as r]
            [reagent.dom.client :as rdc]
            [taoensso.sente :as sente]))

(defonce event-queue
  (async/chan))

(defn emit
  [event]
  (fn [js-event]
    (.preventDefault js-event)
    (async/put! event-queue event)))

(defn ui-root
  [state]
  [:div
   [:div "Clicked: " (:counter state "not received yet")]
   [:input {:type "submit"
            :on-click (emit [:button/clicked])
            :value "click me"}]
   [:pre state]])

(defn init-state
  []
  {})

(defn update-state
  [state event tell-server]
  (match event
    [:chsk/state _] state
    [:chsk/handshake _] state
    [:chsk/ws-ping] state
    [:browser/refresh] state

    [:button/clicked] (do (tell-server [:button/click])
                          state)
    [:counter/value n] (assoc state :counter n)

    _ (do (prn [:client/unhandled event])
          state)))

(defn start-event-loop
  [react-root]
  (let [csrf (.. js/document
                 (getElementById "sente-csrf-token")
                 (getAttribute "data-csrf-token"))
        {:keys [ch-recv send-fn]} (sente/make-channel-socket-client!
                                    "/sente" csrf {:type :auto})]
    (async/go
      (loop [old-state (init-state)]
        (let [event (async/alt!
                      ch-recv ([msg] (:event msg))
                      event-queue ([event] event))
              new-state (update-state old-state event send-fn)]
          (rdc/render react-root [ui-root new-state])
          (recur new-state))))))

(defn ^:after-load reload
  []
  (async/put! event-queue [:browser/refresh]))

(goog.events/listen
  js/window
  "load"
  (fn [_]
    (start-event-loop (rdc/create-root (js/document.getElementById "app")))))
