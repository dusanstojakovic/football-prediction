(ns football-prediction.routes.predictions
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :refer [trim]]
            [compojure.core :refer :all]
            [football-prediction.views.layout :as layout]
            [noir.session :as session]))

;;Link to scrape /**current-url = current year statistics; fixtures-url = fixtures
(def ^:dynamic *current-url* "http://www.scorespro.com/soccer/spain/primera-division/2014-2015/standings/")
(def ^:dynamic *fixtures-url* "http://www.scorespro.com/soccer/spain/primera-division/2014-2015/fixtures/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

;;Storing whole site code for faster access
(def whole-site (fetch-url *current-url*))

;;Storing whole site code for faster access
(def fixtures-site (fetch-url *fixtures-url*))

;;Home teams statistics
(defn home-team []
  (map html/text (html/select whole-site 
                              #{[:#standings_2a :> :table :> :tbody :> :tr :> :td.team.st-br.uc]
                                [:#standings_2a :> :table :> :tbody :> :tr :> :td.mp.st-br]
                                [:#standings_2a :> :table :> :tbody :> :tr :> :td.winx.st-br]
                                [:#standings_2a :> :table :> :tbody :> :tr :> :td.draw.st-br]
                                [:#standings_2a :> :table :> :tbody :> :tr :> :td.lost.st-br]})))

;;Away teams statistics
(defn away-team []
  (map html/text (html/select whole-site 
                              #{[:#standings_3a :> :table :> :tbody :> :tr :> :td.team.st-br.uc]
                                [:#standings_3a :> :table :> :tbody :> :tr :> :td.mp.st-br]
                                [:#standings_3a :> :table :> :tbody :> :tr :> :td.winx.st-br]
                                [:#standings_3a :> :table :> :tbody :> :tr :> :td.draw.st-br]
                                [:#standings_3a :> :table :> :tbody :> :tr :> :td.lost.st-br]})))

;;Home teams mapping to better format for later use
(defn teams-map [stat]
  (into {}
        (map (fn [[t p w d l]] {(keyword (clojure.string/replace t " " "-")) {:played p :win w :draw d :lost l}})
               (partition 5 stat))))


(def home-set (teams-map (home-team)))
(def away-set (teams-map (away-team)))

;;Fixtures scrape
(defn fixtures []
  (map html/text (html/select fixtures-site 
                              #{[:#national :> :div.compgrp :> :table :> :tbody :> :tr :> :td.home.uc]
                                [:#national :> :div.compgrp :> :table :> :tbody :> :tr :> :td.away.uc]})))

;;Fixtures mapping
(defn fixtures-map [stat]
        (map (fn [[h a]] {:home (clojure.string/replace (trim h) " " "-"), :away (clojure.string/replace (trim a) " " "-")})
              (partition 2 stat)))

;;Assign fixtures to var
(def fix-set (fixtures-map (fixtures)))

;;Main algorithm for calculating odds
(defn search-points [h a]
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
  ([fix-set] 
    (let [home (keyword (second (first (first fix-set)))) 
          away (keyword (second (second (first fix-set))))]
      (if (not (empty? fix-set))
	        (prediction-res (rest fix-set) [(search-points home away)]))))
  ([fix-set res]
    (let [home (keyword (second (first (first fix-set)))) 
          away (keyword (second (second (first fix-set))))]
      (if (not (empty? fix-set))
        (prediction-res (rest fix-set) (conj res (search-points home away)))
        res)))) 

(defn prediction-page []
  (layout/render "predictions.html" 
                 {:predictions (prediction-res fix-set)}))

(defroutes prediction-routes
  (GET "/prediction" [] (prediction-page)))