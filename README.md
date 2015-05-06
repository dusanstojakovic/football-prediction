# football-prediction

## About

This project has the goal to give users simple predictions of outcomes in football games. 

It's using MySQL database that is hosted on private hosting, Enlive as scrape library and Selmer as templating library. It has been developed in Eclipse Luna IDE.

Algorithm used to calculate predictions is simple algorithm that uses past games from current season for both teams. Such algorithm is part of more complicated algorithm that is used on another site for football prediction, but for this project it's good enough. (http://www.forebet.com/en/strategies-for-predictions-making/85-host-guest-prediction.html)

MySQL database has two tables: 
1. users (username, password)
2. matches (id, hometeam, awayteam, home, draw, away, username, status)

Application has the following functionalities:

1. User register, login, logout.
2. Displaying predictions for fixtures that have been collected from www.scorespro.com (For demonstrating purposes it gives predictions for Spanish Primera Division).
3. Saving matches per user to database for later review.
4. Removing matches (updating flag to deleted) from database.

 

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

Sumbit 7.

Switched to embedded SQLite database. Handler.clj init function is configured to create new tables on each run and "destroy" function is configured to drop tables on the each server stop. This is done because this is only for testing purposes. If you want to keep database, you can easily delete these parts of "init" and "destroy" functions. Do not delete whole function, only these db parts.




Copyright © 2015 Dusan Stojakovic
