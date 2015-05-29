(ns football-prediction.routes.accuracy
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]
            [compojure.core :refer :all]
            [football-prediction.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]))

(defn get-res-link [league]
 (cond
   (= league "spa") "http://www.betexplorer.com/soccer/spain/primera-division/results/"
   (= league "eng") "http://www.betexplorer.com/soccer/england/premier-league/results/"
   (= league "fra") "http://www.betexplorer.com/soccer/france/ligue-1/results/"
   (= league "ger") "http://www.betexplorer.com/soccer/germany/bundesliga/results/"
   :else "http://www.betexplorer.com/soccer/italy/serie-a/results/")) ;;default italian

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn get-results [res-url]
  (map html/text (html/select (fetch-url res-url) 
                              #{[:#leagueresults_tbody :> :tr :> :td.first-cell.tl :> :a]
                                [:#leagueresults_tbody :> :tr :> :td.result :> :a]})))

(defn calculate [h-team a-team h-score a-score complete-set]
    (if (< h-score a-score)
      (if (empty? complete-set)
        {a-team {:hwon 0 :hdraw 0 :hlost 0 :awon 1 :adraw 0 :alost 0}, h-team {:hwon 0 :hdraw 0 :hlost 1 :awon 0 :adraw 0 :alost 0}}
        (if (contains? complete-set a-team)
          (let [first-set (update-in complete-set [a-team :awon] inc)]
            (if (contains? first-set h-team)
              (update-in first-set [h-team :hlost] inc)
              (conj first-set {h-team {:hwon 0 :hdraw 0 :hlost 1 :awon 0 :adraw 0 :alost 0}})))
          (let [first-set (conj complete-set {a-team {:hwon 0 :hdraw 0 :hlost 0 :awon 1 :adraw 0 :alost 0}})]
            (if (contains? first-set h-team)
              (update-in first-set [h-team :hlost] inc)
              (conj first-set {h-team {:hwon 0 :hdraw 0 :hlost 1 :awon 0 :adraw 0 :alost 0}})))))
      (if (> h-score a-score)
        (if (empty? complete-set)
          {a-team {:hwon 0 :hdraw 0 :hlost 0 :awon 0 :adraw 0 :alost 1}, h-team {:hwon 1 :hdraw 0 :hlost 0 :awon 0 :adraw 0 :alost 0}}
          (if (contains? complete-set a-team)
            (let [first-set (update-in complete-set [a-team :alost] inc)]
              (if (contains? first-set h-team)
                (update-in first-set [h-team :hwon] inc)
                (conj first-set {h-team {:hwon 1 :hdraw 0 :hlost 0 :awon 0 :adraw 0 :alost 0}})))
            (let [first-set (conj complete-set {a-team {:hwon 0 :hdraw 0 :hlost 0 :awon 0 :adraw 0 :alost 1}})]
              (if (contains? first-set h-team)
                (update-in first-set [h-team :hwon] inc)
                (conj first-set {h-team {:hwon 1 :hdraw 0 :hlost 0 :awon 0 :adraw 0 :alost 0}})))))
        (if (empty? complete-set)
          {a-team {:hwon 0 :hdraw 0 :hlost 0 :awon 0 :adraw 1 :alost 0}, h-team {:hwon 0 :hdraw 1 :hlost 0 :awon 0 :adraw 0 :alost 0}}
          (if (contains? complete-set a-team)
            (let [first-set (update-in complete-set [a-team :adraw] inc)]
              (if (contains? first-set h-team)
                (update-in first-set [h-team :hdraw] inc)
                (conj first-set {h-team {:hwon 0 :hdraw 1 :hlost 0 :awon 0 :adraw 0 :alost 0}})))
            (let [first-set (conj complete-set {a-team {:hwon 0 :hdraw 0 :hlost 0 :awon 0 :adraw 1 :alost 0}})]
              (if (contains? first-set h-team)
                (update-in first-set [h-team :hdraw] inc)
                (conj first-set {h-team {:hwon 0 :hdraw 1 :hlost 0 :awon 0 :adraw 0 :alost 0}}))))))))

(defn check-result [h-score a-score]
  (if (< h-score a-score)
    (str "2 - " h-score ":" a-score)
    (if (> h-score a-score)
      (str "1 - " h-score ":" a-score)
      (str "X - " h-score ":" a-score))))

(defn predict [hwin alose hdraw adraw hlose awin total]
  (let [sh (* (float (/ (+ hwin alose) total)) 100)
        sd (* (float (/ (+ hdraw adraw) total)) 100)
        sa (* (float (/ (+ hlose awin) total)) 100)]
    (if (and (>= sh sd) (>= sh sa))
      (str "1 - " (format "%.2f" sh)) 
      (if (and (>= sd sh) (>= sd sa))
        (str "X - " (format "%.2f" sd))
        (str "2 - " (format "%.2f" sa))))))

(defn check-if-good [result prediction]
  (if (= (subs result 0 1) (subs prediction 0 1))
    "yes"
    "no"))

(defn compare-prediction [h-team a-team h-score a-score res] 
  (let [hwin (:hwon (h-team res))
        alose (:alost (a-team res))
        hdraw (:hdraw (h-team res))
        adraw (:adraw (a-team res))
        hlose (:hlost (h-team res))
        awin (:awon (a-team res))
        total (+ hwin alose hdraw adraw hlose awin)]    
    (let [result (check-result h-score a-score)
          prediction (predict hwin alose hdraw adraw hlose awin total)
          good-bad (check-if-good result prediction)]    
      {:teamh (clojure.string/replace (name h-team) "-" " ") :teama (clojure.string/replace (name a-team) "-" " ") :result result :prediction prediction :good good-bad})))

(defn check-played [res]
  (let [team (second (first res))
        total (+ (:hlost team) (:adraw team) (:hwon team) (:alost team) (:awon team) (:hdraw team))]
    (if (> total 10)
      true
      false)))

(defn accuracy-res 
  ([result-set] 
    (let [h-team (keyword (str/upper-case (str/replace (first (str/split (first (last result-set)) #" - ")) " " "-")))
          a-team (keyword (str/upper-case (str/replace (second (str/split (first (last result-set)) #" - ")) " " "-")))
          h-score (int (read-string (first (str/split (second (last result-set)) #":"))))
          a-score (int (read-string (second (str/split (second (last result-set)) #":"))))]
      (if (not (empty? result-set))
	        (accuracy-res (drop-last result-set) (calculate h-team a-team h-score a-score {}) []))))
  ([result-set res fin-set]
      (if (not (empty? result-set))
        (if (check-played res)
          (let [h-team (keyword (str/upper-case (str/replace (first (str/split (first (last result-set)) #" - ")) " " "-")))
                a-team (keyword (str/upper-case (str/replace (second (str/split (first (last result-set)) #" - ")) " " "-")))
                h-score (int (read-string (first (str/split (second (last result-set)) #":"))))
                a-score (int (read-string (second (str/split (second (last result-set)) #":"))))
                view-set (conj fin-set (compare-prediction h-team a-team h-score a-score res))]
            (if (not (empty? result-set))
              (accuracy-res (drop-last result-set) (calculate h-team a-team h-score a-score res) view-set)
              (println "Kraj")))
          (let [h-team (keyword (str/upper-case (str/replace (first (str/split (first (last result-set)) #" - ")) " " "-")))
                a-team (keyword (str/upper-case (str/replace (second (str/split (first (last result-set)) #" - ")) " " "-")))
                h-score (int (read-string (first (str/split (second (last result-set)) #":"))))
                a-score (int (read-string (second (str/split (second (last result-set)) #":"))))]
            (if (not (empty? result-set))
              (accuracy-res (drop-last result-set) (calculate h-team a-team h-score a-score res) [])
              (println "Kraj"))))
        fin-set)))

(defn accuracy-page [league]
  (if (session/get :user)
    (let [result-set (partition 2 (get-results (get-res-link league)))]
	    (layout/render "accuracy.html" 
	                   {:results (accuracy-res result-set)}))
	    (resp/redirect "/login")))

(defroutes accuracy-routes
  (GET "/accuracy/:league" [league] (accuracy-page league)))