(ns football-prediction.handler
  (:require [compojure.route :as route]
            [compojure.core :refer [defroutes]]
            [noir.util.middleware :as noir-middleware]
            [football-prediction.routes.home :refer [home-routes]]
            [football-prediction.routes.account :refer [account-routes]]
            [football-prediction.routes.predictions :refer [prediction-routes]]
            [football-prediction.routes.matches :refer [matches-routes]]
            [football-prediction.routes.accuracy :refer [accuracy-routes]]
            [football-prediction.models.db :as db]))

(defn init []
  (println "football-prediction is starting")
  (db/create-table-users) 
  (db/create-table-matches))

(defn destroy []
  (println "football-prediction is shutting down")
  (if (.exists (java.io.File. "./db.fp"))
    (do (db/drop-table-users) (db/drop-table-matches))))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app 
  (noir-middleware/app-handler 
    [home-routes
     account-routes
     prediction-routes
     matches-routes
     accuracy-routes
     app-routes]))
