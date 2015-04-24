(ns football-prediction.views.layout
  (:require [hiccup.page :refer [html5 include-css]]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to football-prediction"]
     (include-css "/css/screen.css")]
    [:body body]))
