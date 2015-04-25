(ns football-prediction.routes.account
  (:require [compojure.core :refer :all]
            [football-prediction.routes.home :refer :all]
            [football-prediction.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]))

;;Render registration page
(defn register-page [& [username]]
  (layout/render "register.html" {:menu-active "register-menu"}))

;;Render login page
(defn login-page []
  (layout/render "login.html"))

;;Accept POST request from register page and register user
(defn handle-register [username password password1]
  (session/put! :user username)
  (resp/redirect "/"))

;;Accept POST request from register page and login user
(defn handle-login [username password]
  (session/put! :user username)
  (resp/redirect "/"))


(defroutes account-routes
  (GET "/register" []
       (register-page))
  (POST "/register" [username password password1]
       (handle-register username password password1))
  (GET "/login" []
       (login-page))
  (POST "/login" [username password]
       (handle-login username password)))
