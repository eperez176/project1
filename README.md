# Scala - Hive: Real time news analyzer
 ## Project Description 
A Scala console application that is retrieving data using Hive or MapReduce. Build a real-time news analyzer. This application should allow users to view the trending topics (e.g. all trending topics for news related to "politics", "tv shows", "movies", "video games", or "sports" [choose one topic for the project] ). - ALL user interaction must come purely from the console application. - Hive/MapReduce must: - Scrap data from datasets from an API. - Console application must: - query data to answer at least 6 analysis questions. - have a login system for all users with passwords. - 2 types of users: BASIC and ADMIN. - Users should also be able to update username and password. Technologies: - Hadoop MapReduce. - YARN. - HDFS. - Scala 2.13. - Hive. - Git + GitHub.
## Techologies Used
- Hive
- Scala
- Hadoop
- HDFS
## Features
- Login system with unique username
- Division of admin and basic privileges
- Cleaning of input (since the API is open, crowd-sourced database)
- Retrives historical data from past sport events
- Analysis of popular sports (soccer, basketball, football)
## Getting Started
The program needs to run in a Hadoop environment with Hive installed as well as Apache Spark.
Movement of users.csv into directory of "/tmp".
To clone the repo:
git clone https://github.com/eperez176/project1.git
## Usage
The use command line to navigate through the options.

The login menu with some incorrect attempts:
![login](https://github.com/eperez176/project1/blob/main/images/userInterface.PNG)

The main function of the program:
![main](https://github.com/eperez176/project1/blob/main/images/main_function.PNG)

Sample Analysis:
![sample](https://github.com/eperez176/project1/blob/main/images/sample_analysis.PNG)

