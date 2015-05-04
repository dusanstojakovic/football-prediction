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