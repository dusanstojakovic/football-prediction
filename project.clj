(defproject football-prediction "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [selmer "0.8.2"]
                 [ring-server "0.3.1"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [lib-noir "0.7.6"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler football-prediction.handler/app
         :init football-prediction.handler/init
         :destroy football-prediction.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.3.1"]]}})
