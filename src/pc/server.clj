(ns pc.server
  (:require [compojure.core :refer (defroutes GET ANY)]
            [compojure.handler :refer (site)]
            [compojure.route]
            [pc.less :as less]
            [pc.views.content :as content]
            [pc.stefon]
            [stefon.core :as stefon]
            [org.httpkit.server :as httpkit]))

(defroutes routes
  (compojure.route/resources "/" {:root "public"
                                  :mime-types {:svg "image/svg"}})
  (GET "/" [] (content/app))
  (ANY "*" [] {:status 404 :body nil}))

(defn port []
  (if (System/getenv "HTTP_PORT")
    (Integer/parseInt (System/getenv "HTTP_PORT"))
    8080))

(defn start []
  (def server (httpkit/run-server (stefon/asset-pipeline (site #'routes) pc.stefon/stefon-options) {:port (port)})))

(defn stop []
  (server))

(defn restart []
  (stop)
  (start))

(defn init []
  (start))
