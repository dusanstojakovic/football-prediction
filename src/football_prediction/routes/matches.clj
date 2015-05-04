(ns football-prediction.routes.matches 
  (:require [compojure.core :refer :all]
            [football-prediction.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [football-prediction.models.db :as db]))

(defn matches []
  (if (session/get :user)
    (let [username (session/get :user)]
      (layout/render "matches.html" 
                     {:matches (db/get-matches username)}))
    (resp/redirect "/login")))

(defn save-match [teamh teama home draw away]
  (if (session/get :user)
    (try
	      (db/add-match {:hometeam teamh :awayteam teama :home home :draw draw :away away :username (session/get :user) :status "active"})
	      (resp/redirect "/matches")
	      (catch Exception ex
	        (resp/redirect "/")))
   (resp/redirect "/login")))

(defn remove-match [id]
  (if (session/get :user)
	  (if (= (session/get :user) (:username (db/get-user-match id)))
	    (try
		      (db/remove-match id)
		      (resp/redirect "/matches")
		      (catch Exception ex
		        (resp/redirect "/")))
	   (resp/redirect "/login"))
  (resp/redirect "/login")))

(defroutes matches-routes
  (GET "/matches" [] (matches))
  (GET "/save-match/:teamh/:teama/:home/:draw/:away" [teamh teama home draw away] 
       (save-match teamh teama home draw away))
  (GET "/remove-match/:id" [id] 
       (remove-match id)))