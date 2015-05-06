(ns football-prediction.routes.predictions
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :refer [trim upper-case]]
            [compojure.core :refer :all]
            [football-prediction.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]))

(defn get-fix-link [league]
 (cond
   (= league "spa") "http://www.scorespro.com/soccer/spain/primera-division/2014-2015/fixtures/"
   (= league "eng") "http://www.scorespro.com/soccer/england/premier-league/2014-2015/fixtures/"
   (= league "fra") "http://www.scorespro.com/soccer/france/ligue-1/2014-2015/fixtures/"
   (= league "ger") "http://www.scorespro.com/soccer/germany/bundesliga/2014-2015/fixtures/"
   :else "http://www.scorespro.com/soccer/italy/serie-a/2014-2015/fixtures/")) ;;default italian

(defn get-stat-link [league]
 (cond
   (= league "spa") "http://www.scorespro.com/soccer/spain/primera-division/2014-2015/standings/"
   (= league "eng") "http://www.scorespro.com/soccer/england/premier-league/2014-2015/standings/"
   (= league "fra") "http://www.scorespro.com/soccer/france/ligue-1/2014-2015/standings/"
   (= league "ger") "http://www.scorespro.com/soccer/germany/bundesliga/2014-2015/standings/"
   :else "http://www.scorespro.com/soccer/italy/serie-a/2014-2015/standings/")) ;;default italian

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

;;Home teams statistics
(defn home-team [stat-url]
  (map html/text (html/select (fetch-url stat-url) 
                              #{[:#standings_2a :> :table :> :tbody :> :tr :> :td.team.st-br.uc]
                                [:#standings_2a :> :table :> :tbody :> :tr :> :td.mp.st-br]
                                [:#standings_2a :> :table :> :tbody :> :tr :> :td.winx.st-br]
                                [:#standings_2a :> :table :> :tbody :> :tr :> :td.draw.st-br]
                                [:#standings_2a :> :table :> :tbody :> :tr :> :td.lost.st-br]})))

;;Away teams statistics
(defn away-team [stat-url]
  (map html/text (html/select (fetch-url stat-url) 
                              #{[:#standings_3a :> :table :> :tbody :> :tr :> :td.team.st-br.uc]
                                [:#standings_3a :> :table :> :tbody :> :tr :> :td.mp.st-br]
                                [:#standings_3a :> :table :> :tbody :> :tr :> :td.winx.st-br]
                                [:#standings_3a :> :table :> :tbody :> :tr :> :td.draw.st-br]
                                [:#standings_3a :> :table :> :tbody :> :tr :> :td.lost.st-br]})))

;;Home teams mapping to better format for later use
(defn teams-map [stat]
  (into {}
        (map (fn [[t p w d l]] {(keyword (upper-case (clojure.string/replace t " " "-"))) {:played p :win w :draw d :lost l}})
               (partition 5 stat))))

;;Fixtures scrape
(defn fixtures [fix-url]
  (map html/text (html/select (fetch-url fix-url) 
                              #{[:#national :> :div.compgrp :> :table :> :tbody :> :tr :> :td.home.uc]
                                [:#national :> :div.compgrp :> :table :> :tbody :> :tr :> :td.away.uc]})))

;;Fixtures mapping
(defn fixtures-map [stat]
        (map (fn [[h a]] {:home (upper-case (clojure.string/replace (trim h) " " "-")), :away (upper-case (clojure.string/replace (trim a) " " "-"))})
              (partition 2 stat)))

;;Main algorithm for calculating odds
(defn search-points [h a home-set away-set]
  (let [hwin (read-string (:win (h home-set)))
        alose (read-string (:lost (a away-set)))
        hdraw (read-string (:draw (h home-set)))
        adraw (read-string (:draw (a away-set)))
        hlose (read-string (:lost (h home-set)))
        awin (read-string (:win (a away-set)))
        total (+ (read-string (:played (h home-set))) (read-string (:played (a away-set))))]
    
    (let [sh (format "%.2f" (* (float (/ (+ hwin alose) total)) 100))
          sd (format "%.2f" (* (float (/ (+ hdraw adraw) total)) 100))
          sa (format "%.2f" (* (float (/ (+ hlose awin) total)) 100))]
      {:teamh (clojure.string/replace (name h) "-" " ") :teama (clojure.string/replace (name a) "-" " ") :home sh :draw sd :away sa })))

;;Looping through fixtures set and creating new set of calculated results
(defn prediction-res 
  ([fix-set home-set away-set] 
    (let [home (keyword (second (first (first fix-set)))) 
          away (keyword (second (second (first fix-set))))]
      (if (not (empty? fix-set))
	        (prediction-res (rest fix-set) home-set away-set [(search-points home away home-set away-set)]))))
  ([fix-set home-set away-set res]
    (let [home (keyword (second (first (first fix-set)))) 
          away (keyword (second (second (first fix-set))))]
      (if (not (empty? fix-set))
        (prediction-res (rest fix-set) home-set away-set (conj res (search-points home away home-set away-set)))
        res))))

(defn prediction-page [league]
  (if (session/get :user)
    (let [home-set (teams-map (home-team (get-stat-link league)))
          away-set (teams-map (away-team (get-stat-link league)))
          fix-set (fixtures-map (fixtures (get-fix-link league)))]
	    (layout/render "predictions.html" 
	                   {:predictions (prediction-res fix-set home-set away-set)}))
	    (resp/redirect "/login")))

(defroutes prediction-routes
  (GET "/prediction/:league" [league] (prediction-page league)))