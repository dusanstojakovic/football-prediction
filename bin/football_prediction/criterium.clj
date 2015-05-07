(ns football-prediction.criterium
  (:require [football-prediction.routes.predictions :refer :all]
            [criterium.core :refer [with-progress-reporting quick-bench]]))

;;Predefined lazy-seq to avoid too many requests to web site from which is scrape done.
(def ht-map-data (take 100 ["Valencia" "18" "15" "2" "1" "Real Madrid" "17" "15" "1" "1" "FC Barcelona" "17" "15" "0" "2" "Atletico Madrid" "18" "14" "3" "1" "Sevilla" "18" "12" "5" "1" "Real Sociedad" "18" "9" "5" "4" "Villarreal" "17" "10" "1" "6" "Malaga CF" "18" "8" "6" "4" "Real CD Espanyol" "18" "8" "6" "4" "Athletic Bilbao" "17" "7" "5" "5" "Celta Vigo" "17" "7" "4" "6" "Rayo Vallecano" "17" "7" "2" "8" "Getafe" "18" "6" "4" "8" "Levante" "17" "6" "4" "7" "Elche" "18" "6" "3" "9" "Granada CF" "17" "3" "9" "5" "Dep. La Coruna" "18" "4" "6" "8" "Almeria" "17" "3" "7" "7" "Eibar" "17" "4" "3" "10" "Cordoba" "18" "1" "6" "11"]))
(def at-map-data (take 100 ["FC Barcelona" "18" "13" "3" "2" "Real Madrid" "18" "13" "0" "5" "Atletico Madrid" "17" "9" "4" "4" "Sevilla" "17" "9" "1" "7" "Valencia" "17" "6" "7" "4" "Villarreal" "18" "4" "11" "3" "Athletic Bilbao" "18" "6" "4" "8" "Celta Vigo" "18" "5" "6" "7" "Rayo Vallecano" "18" "6" "2" "10" "Elche" "17" "5" "4" "8" "Malaga CF" "17" "5" "2" "10" "Real CD Espanyol" "17" "4" "4" "9" "Eibar" "18" "4" "4" "10" "Almeria" "18" "5" "1" "12" "Getafe" "17" "4" "2" "11" "Levante" "18" "3" "4" "11" "Dep. La Coruna" "17" "2" "6" "9" "Real Sociedad" "17" "1" "8" "8" "Cordoba" "17" "2" "5" "10" "Granada CF" "18" "2" "4" "12"]))
(def fx-map-data (take 20 [" Eibar" "Real CD Espanyol  " " Granada CF" "Cordoba  " " FC Barcelona" "Real Sociedad  " " Real Madrid" "Valencia  " " Athletic Bilbao" "Dep. La Coruna  " " Levante" "Atletico Madrid  " " Villarreal" "Elche  " " Almeria" "Malaga CF  " " Celta Vigo" "Sevilla  " " Rayo Vallecano" "Getafe  "]))

;;Predefined mapped home 
(def home-set (teams-map ht-map-data))
(def away-set (teams-map at-map-data))


;;Returning link for get parameter
;;Execution time mean : 4.033506 ns
;;Execution time std-deviation : 0.122866 ns
(with-progress-reporting(quick-bench (get-fix-link "spa")))

;;Mapping to correct format predefined lazy sequence of home teams statistics
;;Execution time mean : 76.280114 µs
;;Execution time std-deviation : 1.736468 µs
(with-progress-reporting(quick-bench (teams-map ht-map-data)))

;;Mapping to correct format predefined lazy sequence of away teams statistics
;;Execution time mean : 76.853077 µs
;;Execution time std-deviation : 847.683300 ns
(with-progress-reporting(quick-bench (teams-map at-map-data)))

;;Mapping to correct format predefined lazy sequence of fixtures
;;Execution time mean : 21.757234 ns
;;Execution time std-deviation : 0.544358 ns
(with-progress-reporting(quick-bench (fixtures-map fx-map-data)))

;;Calculating predictions for one match with predefined home/away teams and sets.
;;Execution time mean : 12.277712 µs
;;Execution time std-deviation : 342.471607 ns
(with-progress-reporting(quick-bench (search-points :REAL-MADRID :VALENCIA home-set away-set)))

;;Final prediction result returning using predefined lazy-seq (it's full algorithm execution time)
;;Execution time mean : 346.320613 µs
;;Execution time std-deviation : 6.655723 µs
(with-progress-reporting (quick-bench (prediction-res (fixtures-map fx-map-data) (teams-map ht-map-data) (teams-map at-map-data))))

;;WARNING
;;DON'T RUN IT TOO MANY TIMES, BECAUSE IT SENDS A LOT OF REQUESTS TO WEBSITE SERVER while trying to collect data for calculating execution time mean.
;;Final predictions result WITH website scrape 
;;Execution time mean : 4.059491 sec
;;Execution time std-deviation : 342.613215 ms
(with-progress-reporting 
  (quick-bench 
    (prediction-res (fixtures-map (fixtures (get-fix-link "spa"))) (teams-map (home-team (get-stat-link "spa"))) (teams-map (away-team (get-stat-link "spa"))))))