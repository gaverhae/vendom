(ns t.core
  (:require [clojure.core.async :as async]
            [clojure.core.match :refer [match]]
            [compojure.core :as cj]
            [compojure.route :as cj.route]
            [hiccup2.core :as h]
            [org.httpkit.server :as server]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.middleware.defaults :as rd]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]])
  (:gen-class))

(defn html-frame
  [& content]
  (str
    (h/html
      {:mode :html}
      (h/raw "<!DOCTYPE html>\n")
      [:html
       [:head
        [:meta {:charset "utf-8"}]
        [:meta {:name "viewport", :content "width=device-width, initial-scale=1"}]
        [:link {:href "css/style.css", :rel "stylesheet", :type "text/css"}]
        [:title "ChangeMe"]]
       [:body.w-svw.h-svh.flex.justify-center.items-center.font-sans.bg-amber-50
        [:div {:id "sente-csrf-token",
               :data-csrf-token *anti-forgery-token*}]
        content]])))

(defn app
  [sente-get sente-post]
  (cj/routes
    (cj/GET "/sente" _ sente-get)
    (cj/POST "/sente" _ sente-post)
    (cj/GET "/" _ (fn [_]
                    {:status 200
                     :headers {"Content-Type" "text/html"}
                     :body (html-frame
                             [:div {:id "app"}]
                             [:script {:src "cljs-out/dev-main.js", :type "text/javascript"}])}))
    (cj.route/not-found {:status 404
                         :headers {"Content-Type" "text/html"}
                         :body (html-frame
                                 [:h1 "Not found"])})))

(defn now
  []
  (let [now (str (java.time.Instant/now))]
    (str (subs now 0 19) "Z")))

(defn log
  [conn uid event ts]
  (let [e {:ts ts, :uid uid, :event event}]
    (swap! conn update ::log (fnil conj []) e)))

(defn increment-counter!
  [conn]
  (swap! conn update :counter (fnil inc 0)))

(defn handle-event!
  [conn {:keys [uid event send-fn connected-uids]}]
  (let [ts (now)
        connected? (:any @connected-uids)
        tell-user send-fn
        broadcast (fn [users msg]
                    (doseq [uid users
                            :when (connected? uid)]
                      (tell-user uid msg)))]
    (log conn uid event ts)
    (match event
      [:chsk/uidport-open _] :ignore
      [:chsk/uidport-close _] :ignore
      [:chsk/ws-ping] :ignore
      [:chsk/ws-pong] :ignore
      [:button/click] (let [new-db (increment-counter! conn)]
                          (broadcast connected? [:counter/value (:counter new-db)]))
      [:button/get] (tell-user uid [:counter/value (:counter @conn 0)])
      _ (prn [:server/unhandled event]))))

(defn event-loop
  [conn ch]
  (async/thread
    (loop []
      (let [msg (async/<!! ch)]
        (when (not= :stop msg)
          (try (handle-event! conn msg)
            (catch Throwable t (prn [:error t])))
          (recur))))))

(defn start-sente
  []
  (let [s (sente/make-channel-socket-server!
            (get-sch-adapter)
            {:user-id-fn (comp :user-id :session)})]
    [(:ajax-post-fn s)
     (:ajax-get-or-ws-handshake-fn s)
     (:ch-recv s)]))

(defonce database
  (atom {}))

(defn -main
  [& args]
  (let [version (or (System/getenv "VERSION") "local")
        [sente-post sente-get sente-chan] (start-sente)
        loop-chan (event-loop database sente-chan)
        stop-server (server/run-server
                      (-> (app sente-get sente-post)
                          (rd/wrap-defaults rd/site-defaults))
                      {:port (parse-long (or (System/getenv "PORT") "8000"))})]
    (fn []
      (async/put! sente-chan :stop)
      (stop-server)
      (async/<!! loop-chan))))
