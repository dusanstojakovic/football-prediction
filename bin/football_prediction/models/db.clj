(ns football-prediction.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db
  {:subprotocol "mysql"
   :subname "//195.154.187.199:3306/pcelarst_clojure"
   :user "pcelarst_dusan"
   :password "clojure123!"})

(defn create-user [user]
  (sql/with-connection db
    (sql/insert-record :users user)))

(defn get-user [username]
  (sql/with-connection db
    (sql/with-query-results
      res ["select * from users where username = ?" username] (first res))))