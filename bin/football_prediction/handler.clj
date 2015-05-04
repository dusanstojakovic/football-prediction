(ns football-prediction.handler
  (:require [compojure.route :as route]
            [compojure.core :refer [defroutes]]
            [noir.util.middleware :as noir-middleware]
            [football-prediction.routes.home :refer [home-routes]]
            [football-prediction.routes.account :refer [account-routes]]
            [football-prediction.routes.predictions :refer [prediction-routes]]))

(defn init []
  (println "football-prediction is starting"))

(defn destroy []
  (println "football-prediction is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app 
  (noir-middleware/app-handler 
    [home-routes
     account-routes
     prediction-routes
     app-routes]))
