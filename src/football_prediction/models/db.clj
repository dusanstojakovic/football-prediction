(ns football-prediction.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db
  {:subprotocol "mysql"
   :subname "//195.154.187.199:3306/pcelarst_clojure"
   :user "pcelarst_dusan"
   :password "clojure123!"})

