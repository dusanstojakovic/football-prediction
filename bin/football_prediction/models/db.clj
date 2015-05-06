(ns football-prediction.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))

(def db
  {:classname "org.sqlite.JDBC",
   :subprotocol "sqlite",
   :subname "db.fp"})

(defn create-table-users []
  (sql/with-connection 
    db
    (sql/create-table
      :users
      [:username "TEXT PRIMARY KEY"]
      [:password "TEXT"])))

(defn create-table-matches []
  (sql/with-connection 
    db
    (sql/create-table
      :matches
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:hometeam "TEXT"]
      [:awayteam "TEXT"]
      [:home "TEXT"]
      [:draw "TEXT"]
      [:away "TEXT"]
      [:username "TEXT"]
      [:status "TEXT"])))

(defn drop-table-users []
  (sql/with-connection
    db
    (sql/drop-table :users)))

(defn drop-table-matches []
  (sql/with-connection
    db
    (sql/drop-table :matches)))

(defn create-user [user]
  (sql/with-connection db
    (sql/insert-record :users user)))

(defn get-user [username]
  (sql/with-connection db
    (sql/with-query-results
      res ["select * from users where username = ?" username] (first res))))

(defn get-user-match [id]
  (sql/with-connection db
    (sql/with-query-results
      res ["select username from matches where id = ?" id] (first res))))

(defn remove-match [id]
  (sql/with-connection db
    (sql/update-values :matches ["id=?" id] {:status "deleted"})))

(defn get-matches [username]
  (sql/with-connection db
    (sql/with-query-results
      res ["select * from matches where status = 'active' and username = ?" username] (doall res))))

(defn add-match [match]
  (sql/with-connection db
    (sql/insert-record :matches match)))