(ns football-prediction.routes.account
  (:require [compojure.core :refer :all]
            [football-prediction.routes.home :refer :all]
            [football-prediction.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [football-prediction.models.db :as db]))

;;Error validation
(defn valid? [username password password1]
  (vali/rule (vali/has-value? username)
             [:username "Username is reqired"])
  (vali/rule (vali/min-length? password 6)
             [:password "Password must be at least 6 character"])
  (vali/rule (= password password1)
             [:password "Passwords do not match"])
  (not (vali/errors? :username :password)))

;;Checking for sql exception
(defn format-error [username ex]
  (cond
    (and (instance? java.sql.SQLException ex)
         (= 1062 (.getErrorCode ex))) ;;1062 mySql error code for duplicate key
    (str "The user with name " username " already exists!")
  :else
  "An error has occured while processing the request"))

;;Render registration page
(defn register-page [& [username]]
  (layout/render "register.html" 
                 {:username username
                  :username-error (first (vali/get-errors :username))
                  :password-error (first (vali/get-errors :password))}))

;;Accept POST request from register page and register user
(defn register-user [username password password1]  
  (if (valid? username password password1)
    (try
      (db/create-user {:username username :password (crypt/encrypt password)})
      (session/put! :user username)
      (resp/redirect "/")
      (catch Exception ex
        (vali/rule false [:username (format-error username ex)])
        (register-page)))
    (register-page username)))

;;Render login page
(defn login-page []
  (if-not (session/get :user)
    (layout/render "login.html"
                   {:login-err (first (vali/get-errors :login-err))})
    (resp/redirect "/")))

;;Accept POST request from login page and login user
(defn login-user [username password]
  (let [user (db/get-user username)]
    (if (and user (crypt/compare password (:password user))) ;;Encript and compare passwords
      (do (session/put! :user username) (resp/redirect "/"))
      (do (vali/rule false [:login-err "Wrong username or password"]) (login-page)))))

;;Logout user
(defn logout-user []
  (session/clear!)
  (resp/redirect "/"))

(defroutes account-routes
  (GET "/register" []
       (register-page))
  (POST "/register" [username password password1]
       (register-user username password password1))
  (GET "/login" []
       (login-page))
  (POST "/login" [username password]
       (login-user username password))
  (GET "/logout" []
       (logout-user)))
