package login
import parser._

import scala.io.Source;
import java.io.File;
import java.io._;
import scala.io.StdIn.readLine;
import java.io.IOException
import scala.util.Try

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

object menu {
  def menuOptions(admin: Int): Int = {
    println("\nWelcome!\n")
    println("To change username/password (1)");
    println("To print out a list of certain criteria (2)")
    println("To search stats for an event (3)")
    println("To print out future events for a team (4)")
    println("To do analysis (5)")
    println("To print user's info (6)")
    if(admin == 2) {
        println("Admin: Delete Users (7)")
        println("Admin: List of Users Info (8)")
    }
    println("To log out (9)")
    println("To exit (10)\n")

    var validEntry = false;
    var input = 10;
    do { // Make sure a valid input is added without causing errors
        try {
            input = scala.io.StdIn.readInt();
            if((input > 0 && input <= 10) && (input != 7 && input != 8)) // Option 7 & 8 are admin only
                validEntry = true;
            else if( (input == 7 || input == 8) && admin == 2) // Only admins are allowed to delete users or print all user info
                validEntry = true;
            else
                println("Invalid Option. Try again.\n")
        }
        catch {
            case _: Throwable => println("Invalid Entry. Try again\n")
        }
    }   while(!validEntry)

    return input;
  }

  def printEvents: Unit = {
        try {
            println("Printing Future Events (1) or Past Events (2)")
            var opt = scala.io.StdIn.readInt();
            if(opt == 1) {
                println("Enter the League ID: ")
                var id = scala.io.StdIn.readLine();
                var input = "https://www.thesportsdb.com/api/v1/json/40130162/eventsnextleague.php?id=" + id;
                var output = Parser.csvParser(input).split("\n");
                for(line <- output) {
                    var parsed = line.split(",");
                    println(parsed(5))
                }
            }
            else if (opt == 2) {
                println("Enter the League ID: ")
                var id = scala.io.StdIn.readLine();
                var input = "https://www.thesportsdb.com/api/v1/json/40130162/eventspastleague.php?id=" + id;
                var output = Parser.csvParser(input).split("\n");
                for(line <- output) {
                    var parsed = line.split(",");
                    println(parsed(0) + " " + parsed(5) + " Home Score: " + parsed(13) + " Away Score: " + parsed(15));
                }
            }
        }
        catch {
            case _: Throwable => println("Invalid. Returning back to the Main Menu...")
        }
  }

  def analysis: Unit = {
    println("What analysis should be done:")
    println("Average Score of a Team (1)")
    println("Average Score of against a Team (2)")
    println("Average Contract Length of a Player in a Team (3)")
    println("Highest Payed Player in a Team (4) ")
    println("Average Age of a Player in a Team (5)")
    println("Average Wage of a Player in a Team (6)")

    try {
        var input = "";
        var output = "";
        var tableName = ""
        var filepath = "";
        var sql = "";
        var driverName = "org.apache.hive.jdbc.HiveDriver"
        val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
        Class.forName(driverName);
        var con: java.sql.Connection = null;
        con = DriverManager.getConnection(conStr, "", "");
        val stmt = con.createStatement();

        var opt = scala.io.StdIn.readInt();
        if(opt> 0 && opt < 7) {
            if(opt == 1) {
                println("Enter Team ID: ")
                var id = scala.io.StdIn.readLine();
                input = "https://www.thesportsdb.com/api/v1/json/40130162/eventslast.php?id=" + id;
                output = Parser.csvParser(input);
                var tmp = 0;
                var result = output.split("\n")
                for(line <- result) {
                    var S = line.split(",");
                    tmp = tmp + S(13).toInt
                }
                println("Average for the team: " + (tmp/5).toString);
            }
            else if(opt == 2) {
                println("Enter Team ID: ")
                var id = scala.io.StdIn.readLine();
                input = "https://www.thesportsdb.com/api/v1/json/40130162/eventslast.php?id=" + id;
                output = Parser.csvParser(input);
                var tmp = 0;
                var result = output.split("\n")
                for(line <- result) {
                    var S = line.split(",");
                    tmp = tmp + S(15).toInt
                }
                println("Average for score against the team: " + (tmp/5).toString);
            }
            else if(opt == 3) { // To do later
                println("Enter Team ID: ")
                var id = scala.io.StdIn.readLine();
                input = "https://www.thesportsdb.com/api/v1/json/40130162/lookup_all_players.php?id=" + id;
                output = Parser.csvParser(input);
                var tmp = 0;
                var num = 0;
                var count = 0;
                var result = output.split("\n")
                for(line <- result) {
                    var S = line.split(",");
                    var c = S(15)(0);
                    if(S(15) == null)
                        num = 0;
                    else if(S(15) == "null") {
                        num = 0;
                    }
                    else if(c > 48 && c < 56){
                        count = count + 1;
                        var s_tmp = S(15)
                        num = (s_tmp(0).toString + s_tmp(1).toString + s_tmp(2).toString + s_tmp(3).toString).toInt;
                        num = 2021-num;
                    }
                    tmp = tmp + num
                }
                println("Average Known Contract Length of The Team: " + (tmp/count).toString);
            }
            else if(opt == 4) {
                println("Enter Team ID: ")
                var id = scala.io.StdIn.readLine();
                input = "https://www.thesportsdb.com/api/v1/json/40130162/lookup_all_players.php?id=" + id;
                output = Parser.csvParser(input);
                var tmp = '0';
                var num = 0.0;
                var max = -1.0;
                var result = output.split("\n")
                var count = 0;
                var index = -1;
                var name = "";
                var ss = ""
                for(line <- result) {
                    var S = line.split(",");
                    var c = (S(16))(0)
                    if(S(16) == null)
                        num = 0;
                    else if(c.toInt < 56 && c.toInt > 47) {
                        count = count + 1;
                        var s_tmp = S(16);
                        if(s_tmp(0).toInt > 56)
                            tmp = 0
                        else
                            tmp = s_tmp(0)
                        ss = tmp.toString + s_tmp(1).toString + s_tmp(2).toString
                        num = ss.toFloat
                        if(num > max) {
                            max = num;
                            name = S(8)
                        }

                    }
                    else
                        num = 0;
                }
                println("Highest Payed Player in The Team: " + name + " with wage of " + (max).toString);   
            }
            else if(opt == 5) {
                println("Enter Team ID: ")
                var id = scala.io.StdIn.readLine();
                input = "https://www.thesportsdb.com/api/v1/json/40130162/lookup_all_players.php?id=" + id;
                output = Parser.csvParser(input);
                var tmp = 0.0;
                var num = 0.0;
                var max = -1;
                var result = output.split("\n")
                var count = 0;
                for(line <- result) {
                    var S = line.split(",");
                    if(S(13) == null)
                        num = 0;
                    else {
                        count = count + 1;
                        var s_tmp = S(13)
                        num = (s_tmp(0).toString + s_tmp(1).toString + s_tmp(2).toString + s_tmp(3).toString).toInt;
                        num = 2021-num;
                    }
                    tmp = tmp + num
                }
                println("Average Age Of Players in The Team: " + (tmp/count).toString); 
            }
            else if(opt == 6) {
                println("Enter Team ID: ")
                var id = scala.io.StdIn.readLine();
                input = "https://www.thesportsdb.com/api/v1/json/40130162/lookup_all_players.php?id=" + id;
                output = Parser.csvParser(input);
                var tmp = 0.0;
                var num = 0.0;
                var max = -1;
                var result = output.split("\n")
                var count = 0;
                var ss = "";
                var tmpi = '0';
                for(line <- result) {
                    var S = line.split(",");
                    var c = (S(16))(0)
                    if(S(16) == null)
                        num = 0;
                    else if(c.toInt < 56 && c.toInt > 47) {
                        count = count + 1;
                        var s_tmp = S(16);
                        if(s_tmp(0).toInt > 56)
                            tmpi = 0
                        else
                            tmpi = s_tmp(0)
                        ss = tmpi.toString + s_tmp(1).toString + s_tmp(2).toString
                        num = ss.toFloat

                    }
                    tmp = tmp + num
                }
                println("Average Known Contract Income in The Team: " + (tmp/count).toString);   
            }
            
        }

    }
    catch {
        case _: Throwable => println("Invalid. Going back to the Main menu...\n")
    }
  }

  def search: Unit = {
    var driverName = "org.apache.hive.jdbc.HiveDriver"
    val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
    Class.forName(driverName);
    var con: java.sql.Connection = null;

    try {

        con = DriverManager.getConnection(conStr, "", "");
        val stmt = con.createStatement();
        var input = "";
        var output = "";
        var tableName = ""
        var filepath = "";
        var sql = "";
        println("Enter the Event ID: ");
        var id = scala.io.StdIn.readLine;
        tableName = "EventStats"
        stmt.execute("drop table IF EXISTS " + tableName);
        stmt.execute("create table " + tableName + " (key int, idEvent int, idApiFootball int, strEvent string, strStat string, intHome int, intAway int) row format delimited  fields terminated by ','");
        filepath = "/tmp/event.csv";
        val user_file = new File(filepath);
        val user_writer = new FileWriter(user_file);

        input = "https://www.thesportsdb.com/api/v1/json/40130162/lookupeventstats.php?id=" + id;
        output = Parser.csvParser(input);

        user_writer.write(output)
        var SA = output.split("\n");
        var first = SA(0).split(",")
        println(first(3))
        for(line <- SA) {
            var parsedLine = line.split(",");
            println(parsedLine(4) + " Home: " + parsedLine(5) + " Away: " + parsedLine(6))
        }
        user_writer.close();
        con.close()
    }
    catch {
        case ex: Throwable => ex.printStackTrace();
    }
  }

  def printList : Unit = {
        var driverName = "org.apache.hive.jdbc.HiveDriver"
        val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
        Class.forName(driverName);
        var con: java.sql.Connection = null;
        con = DriverManager.getConnection(conStr, "", "");
        val stmt = con.createStatement();

        println("What list would you like to print? All Leagues (1), All Countries (2), All Leagues" +
            " in a country (3), All Teams in a League (4), All Players in a Team (5)");
        var options = -1;
        try {
            var input = "";
            var output = "";
            var tableName = ""
            var filepath = "";
            var sql = "";
            options = scala.io.StdIn.readInt();
            if(options > 0 && options < 6) {
                // Find out how to create a table using a string (might need to hard code it)
                // Using Hive to get the answers afterwards (using res)
                if(options == 2)
                { // Get all the information from the API
                    filepath = "/tmp/countries.csv";
                    input = "https://www.thesportsdb.com/api/v1/json/1/all_countries.php";
                    output = Parser.csvParser(input);
                    println(output)
                }
                else if (options == 1) {
                    tableName = "Leagues"
                    stmt.execute("drop table IF EXISTS " + tableName);
                    stmt.execute("create table " + tableName + " (key int, strLeague string, strSport string, strLeagueAlternate string) row format delimited  fields terminated by ','");
                    filepath = "/tmp/leagues.csv";
                    val user_file = new File(filepath);
                    val user_writer = new FileWriter(user_file);

                    input = "https://www.thesportsdb.com/api/v1/json/1/all_leagues.php"
                    output = Parser.csvParser(input);

                    user_writer.write(output)
                    sql = "load data local inpath '" + filepath + "' into table " + tableName;
                    stmt.execute(sql);

                    sql = "select * from " + tableName;
                    var res = stmt.executeQuery(sql)
                    while(res.next()) {
                        println(res.getString(1) + " " + res.getString(2) + " (" + res.getString(3) +")")
                    }
                    user_writer.close();
                    con.close()

                }
                else if (options == 3) {
                    // Add more navigation for the country
                    println("Country: ")
                    var country = scala.io.StdIn.readLine();
                    input = "https://www.thesportsdb.com/api/v1/json/40130162/search_all_leagues.php?c=" + country;
                    tableName = "LeaguesCountry"
                    stmt.execute("drop table IF EXISTS " + tableName);
                    stmt.execute("create table " + tableName + " (key int, idSoccerXmL int, idAPIfootball int, strSport string, strLeague string, strLeagueAlternate string, strDivision string, idCup int, strCurrentSeason string, intFormedYear int, dateFirstEvent string, strGender string, strCountry string, strWebsite string, strFacebook string, strTwitter string, strYoutube string, strRss string, strDescribtionEN string, strDescriptionDE string, strDescriptionFR string, strDescriptionIL string, strTvrights string, strFanart1 string, strFanart2 string, strFanart3 string, strFanart4 string, strBanner string, strBadge string, strLogo string, strPoster string, strTropy string, strNaming string, strComplete string, strLocked string) row format delimited  fields terminated by ','");
                    filepath = "/tmp/lc.csv";
                    val user_file = new File(filepath);
                    val user_writer = new FileWriter(user_file);

                    output = Parser.csvParser(input);

                    user_writer.write(output)
                    sql = "load data local inpath '" + filepath + "' into table " + tableName;
                    stmt.execute(sql);

                    sql = "select * from " + tableName;
                    var res = stmt.executeQuery(sql)
                    while(res.next()) {
                        println(res.getString(1) + " " + res.getString(5) + " ("+ res.getString(4)+")")
                    }
                    user_writer.close();
                    con.close()
                }
                else if (options == 4) {
                    // Menu
                    println("Enter League Name: ")
                    var leagueName = scala.io.StdIn.readLine()
                    var LN = leagueName.replace(" ", "%20");
                    input = "https://www.thesportsdb.com/api/v1/json/1/search_all_teams.php?l=" + LN;
                    tableName = "AllTeams"
                    stmt.execute("drop table IF EXISTS " + tableName);
                    stmt.execute("create table " + tableName + " (key int, idSoccerXmL int, idAPIfootball int, intLoved int, strTeam string, strTeamShort string, strAlternate string, intFormedYear int, strSport string, strLeague string, idLeague int, strLeague2 string, idLeague2 int, strLeague3 string, idLeague3 int, strLeague4 string, idLeague4 int, strLeague5 string, strLeague5 string, idLeague6 int, strLeague6 string, strLeague7 string, idLeague7 int, strDivision string, strManger string, strStadium string, strKeywords string, strRSS string, strStadiumThumb string, strStadiumDescription string, strStadiumLocation string, intStadiumCapcity int, strWebsite string, strTwitter string, strInstagram string, strDescriptionEn string, strDescriptionDE, strDescriptionFR string, strDescriptionCN, strDescriptionIT, strDescriptionJP string, strDescriptionRU string strDescriptionES string, strDescriptionPT string, strDescriptionSE, strDescriptionHU string, strDescriptionNO string, strDescriptionIL string, strDescriptionPL string, strGender string, strCountry string, strTeamBadge string, strTeamJersey string, strTeamLogo string, strTeamFanart1 string,strTeamFanart2 string,strTeamFanart3 string,strTeamFanart4 string, strTeamBanner string, strYoutube string, strLocked string) row format delimited  fields terminated by ','");
                    filepath = "/tmp/at.csv";
                    val user_file = new File(filepath);
                    val user_writer = new FileWriter(user_file);

                    output = Parser.csvParser(input);

                    user_writer.write(output)
                    sql = "load data local inpath '" + filepath + "' into table " + tableName;
                    stmt.execute(sql);

                    sql = "select * from " + tableName;
                    var res = stmt.executeQuery(sql)
                    while(res.next()) {
                        println(res.getString(1) + " " + res.getString(5))
                    }
                    user_writer.close();
                    con.close()
                    output = Parser.csvParser(input)
                }
                else if (options == 5) {
                    println("Team ID: ")
                    var id = scala.io.StdIn.readLine();
                    input = "https://www.thesportsdb.com/api/v1/json/40130162/lookup_all_players.php?id=" + id;
                    tableName = "AllPlayers"
                    stmt.execute("drop table IF EXISTS " + tableName);
                    stmt.execute("create table " + tableName + " (key int, idTeam int, idTeam2 int, idTeamNational int, idSoccerXML int, idAPIfootball int, idPlayerManager int, strNationaliy string, strPlayer string, strTeam string, strTeam2 string, strSport string,SoccerXMLTeamID int, dateBorn string, strNumber string, dateSigned string, strTwitter string, strInstagram string, strYoutube string, strHeight string, strWeight string, intLoved int, strThumb string, strCutout string, strRender string, strBaner string, strFanart1 string, strFanart2 string, strFanart3 string, strFanart4 string, strCreativeCommons string, strLocked string) row format delimited  fields terminated by ','");
                    filepath = "/tmp/ap.csv";
                    val user_file = new File(filepath);
                    val user_writer = new FileWriter(user_file);

                    output = Parser.csvParser(input);

                    user_writer.write(output)
                    sql = "load data local inpath '" + filepath + "' into table " + tableName;
                    stmt.execute(sql);

                    sql = "select * from " + tableName;
                    var res = stmt.executeQuery(sql)
                    while(res.next()) {
                        println(res.getString(1) + " " + res.getString(9) + " " + res.getString(10) + " Number: " + res.getString(15));
                    }
                    user_writer.close();
                    con.close()
                    output = Parser.csvParser(input)
                }

            }
            else
                println("Invalid Entry. Going back to the Main Menu...\n")
        }
        catch {
            case ex: Throwable => ex.printStackTrace();
        }
    
  }
}
