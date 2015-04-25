(ns football-prediction.routes.home
  (:require [compojure.core :refer :all]
            [football-prediction.views.layout :as layout]
            [noir.session :as session]))

(defn home []
  (layout/render "home.html" 
                 {:message "Hello Dusan"}))

(defroutes home-routes
  (GET "/" [] (home)))