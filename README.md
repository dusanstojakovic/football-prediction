# football-prediction

## About

This project has the goal to give users simple predictions of outcomes in football games. 

It's using SQLite databaseg, Enlive as scrape library and Selmer as templating library. It has been developed in Eclipse Luna IDE.

Algorithm used to calculate predictions is simple algorithm that uses past games from current season for both teams. Such algorithm is part of more complicated algorithm that is used on another site for football prediction, but for this project it's good enough. (http://www.forebet.com/en/strategies-for-predictions-making/85-host-guest-prediction.html)

SQLite database has two tables: 
1. users (username, password)
2. matches (id, hometeam, awayteam, home, draw, away, username, status)

Application has the following functionalities:

1. User register, login, logout.
2. Displaying predictions for fixtures that have been collected from www.scorespro.com
3. Saving matches per user to database for later review.
4. Removing matches (updating flag to deleted) from database.
5. Displaying prediction for five major leagues (England, Spain, Germany, France, Italy)
6. Criterium tests


## Criterium execution time tests

Here are some results of criterium library tests. I have included Execution time mean and std-deviation results on my machine.
Testing machine specification:
	Intel Core i5-2450M @2.50GHz
	8 GB RAM memory
	64-bit Operating System, x64-based processor
	Windows 8.1

;;Returning link for get parameter
;;Execution time mean : 4.033506 ns
;;Execution time std-deviation : 0.122866 ns

(with-progress-reporting(quick-bench (get-fix-link "spa")))

;;Mapping to correct format predefined lazy sequence of home teams statistics
;;Execution time mean : 76.280114 탎
;;Execution time std-deviation : 1.736468 탎

(with-progress-reporting(quick-bench (teams-map ht-map-data)))

;;Mapping to correct format predefined lazy sequence of away teams statistics
;;Execution time mean : 76.853077 탎
;;Execution time std-deviation : 847.683300 ns

(with-progress-reporting(quick-bench (teams-map at-map-data)))

;;Mapping to correct format predefined lazy sequence of fixtures
;;Execution time mean : 21.757234 ns
;;Execution time std-deviation : 0.544358 ns

(with-progress-reporting(quick-bench (fixtures-map fx-map-data)))

;;Calculating predictions for one match with predefined home/away teams and sets.
;;Execution time mean : 12.277712 탎
;;Execution time std-deviation : 342.471607 ns

(with-progress-reporting(quick-bench (search-points :REAL-MADRID :VALENCIA home-set away-set)))

;;Final prediction result returning using predefined lazy-seq (it's full algorithm execution time)
;;Execution time mean : 346.320613 탎
;;Execution time std-deviation : 6.655723 탎

(with-progress-reporting (quick-bench (prediction-res (fixtures-map fx-map-data) (teams-map ht-map-data) (teams-map at-map-data))))

 

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server
    
## Process

Submit 1.

Setting up base for project.

Submit 2.

Added: Selmer templating library, connection to database, login page, register page, base for
templates

Submit 3. 

Added: register and login, database functions, error validation, different menu for guests and users.

Submit 4. 

Added: Enlive for web scrape. Scrape statistics and fixtures from site. Apply algorithm to the data and calculate odds for matches outcome. 

Submit 5.

Added: Saving matches to database per user. Removing matches from visual presentation to user. Security checks for unauthorized access for guests. Security checks for unauthorized access to database records from other users.

Submit 6.

Added: Support for multiple leagues (top five: England, Spain, Germany, France, Italy). Switching leagues depends on the GET parameter.

Submit 7.

Switched to embedded SQLite database. Handler.clj init function is configured to create new tables on each run and "destroy" function is configured to drop tables on the each server stop. This is done because this is only for testing purposes. If you want to keep database, you can easily delete these parts of "init" and "destroy" functions. Do not delete whole function, only these db parts.

Submit 8.

Added: Criterium tests.

## Submit 9.

Added: Complete monitoring of algorithm accuracy on previous matches. Accuracy is calculated after 10 rounds, when there is enough data to work with. When calculating, only statistic of previously played matches before current match is being used. Algorithm for calculating is creating an virtual scores table using only results from matches before current match and that scores table is used for prediction. 

Example: 

After 12 rounds Almeria and Rayo Vallecano have the following results:
- Almeria (home-won: 0, home-draw: 3, home-lost: 3, away-won: 2, away-draw: 1,away-lost: 3)
- Rayo Vallecano (home-won: 2, home-draw: 1, home-lost: 3, away-won: 2, away-draw: 1,away-lost: 3)

Prediction algorithm says that Rayo Vallecano has 41.67% for away-win.
Real result was away-win (0:1) for Rayo Vallecano.

Algorithm used for this calculation can be used for testing some other prediction algorithms and their accuracy, with some adaptations for other algorithm.

## Submit 10.

Added: Following of overall good and bad accuracy for leagues.


Copyright 짤 2015 Dusan Stojakovic
