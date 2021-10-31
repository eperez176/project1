package  login;

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

object Login {
    def login(): Int = {
        var exit = false;
        var loginOption = 0;
        var validLogin = false;
        var inside = false;
        var isAdmin = false;
        var loginInfo = new Array[String](2); // Structure having username & pass
        var con: java.sql.Connection = null;

        try {
            var driverName = "org.apache.hive.jdbc.HiveDriver"
            val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
            Class.forName(driverName);
            con = DriverManager.getConnection(conStr, "", "");
            val stmt = con.createStatement();
            stmt.executeQuery("Show databases");
            System.out.println("show database successfully");

            // Re-Load the db from users.csv
            val tableName = "Users";
            println(s"Dropping table $tableName..")
            stmt.execute("drop table IF EXISTS " + tableName);
            println(s"Creating table $tableName..")
            stmt.execute("create table " + tableName + " (key int, username string, password string, admin boolean) row format delimited  fields terminated by ','");

            val filepath = "/tmp/users.csv";
            var sql = "load data local inpath '" + filepath + "' into table " + tableName;
            System.out.println("Running: " + sql);
            stmt.execute(sql);

            sql = "select * from " + tableName;
            System.out.println("Running: " + sql);
            var res = stmt.executeQuery(sql);

            do {
                do {
                    exit = false; // Reset loop
                    try {
                        println("Login (1), Sign Up (2)");
                        loginOption = scala.io.StdIn.readInt();
                        if(loginOption == 1 || loginOption == 2) { // Valid options picked
                            exit = true;
                        }
                        else {
                            println("You did not pick a valid option\n");
                        }
                    }
                    catch {
                        case _ : Throwable => println("Invalid Input! Try again\n")
                    }
                } while (!exit)

                if(loginOption == 1) { // Login
                    inside = false; // Reset for future loops
                    try {
                        // To get back to the top of db
                        sql = "select * from " + tableName;
                        res = stmt.executeQuery(sql);

                        println("Enter Username:");
                        loginInfo(0) = scala.io.StdIn.readLine();
                        println("Enter Password:");
                        loginInfo(1) = scala.io.StdIn.readLine();
                        println("Verifying...");
                        while(res.next()) { // Check if the user exists in the db
                            if(loginInfo(0) == res.getString(2)) {
                                if(loginInfo(1) == res.getString(3)) {
                                    inside = true;
                                    if(res.getBoolean(4)) // Check for the user is admin
                                        isAdmin = true;
                                }
                            }
                        }
                        if(inside)
                            println("\nHello " + loginInfo(0));
                        else
                            println("Username and password combination does not exit. Try again\n")
                    }
                    catch {
                        case _: Throwable => println("Invalid\n");
                    }
                }
                else if(loginOption == 2) {
                    var validEntry = true;
                    var lastId = 0;
                    var newUser = " ";

                    val user_file = new File("/tmp/users.csv");
                    val user_source = Source.fromFile(user_file);
                    val user_writer = new FileWriter(user_file, true);

                    do {
                        try {

                            // To get back to the top of db
                            sql = "select * from " + tableName;
                            res = stmt.executeQuery(sql);

                            validEntry = true; // Look for cases it's not true

                            println("Enter Username:")
                            loginInfo(0) = scala.io.StdIn.readLine();
                            while(res.next()) {
                                if(loginInfo(0) == res.getString(2))
                                    validEntry = false;
                                lastId = res.getInt(1); // Saves the last id of the file for adding a new one
                            }

                            if(validEntry) { // If the username is unique, proceed
                                println("Enter Password:")
                                loginInfo(1) = scala.io.StdIn.readLine();
                                newUser = (lastId+1).toString + "," + loginInfo(0) + "," + loginInfo(1) + "," + "false\n"; // String to append
                                println(newUser);
                                user_writer.write(newUser);
                                stmt.execute("drop table IF EXISTS " + tableName);
                                stmt.execute("create table " + tableName + " (key int, username string, password string, admin boolean) row format delimited  fields terminated by ','");
                                sql = "load data local inpath '" + filepath + "' into table " + tableName;
                                stmt.execute(sql);
                                println("Database updated");
                                inside = true;
                            }
                            else {
                                println("Username has been taken. Try again!\n")
                            }

                        }
                        catch {
                            case _: Throwable => println("Invalid\n");
                        }
                        finally { // Closing files
                            user_source.close();
                            user_writer.close();
                        }


                    } while (!validEntry)
                }
            } while (!inside)
        }
        catch {
            case ex: Throwable => {
                ex.printStackTrace();
                throw new Exception(s"${ex.getMessage}")
            }
        }
        finally {
            try {
                if (con != null)
                    con.close();
            }
            catch {
                case ex : Throwable => {
                    ex.printStackTrace();
                    throw new Exception(s"${ex.getMessage}")
                }
            }
        }

        if(isAdmin) {
            return 2; // Admin User
        }
        else if(exit)
            return 1; // Regular User
        else
            return 0; // Invalid User
    }
    
    def updateLogin(): Unit = {
        var option = 0;
        var validOption = false;
        var con: java.sql.Connection = null;
        val tableName = "Users";

        var newUsername = "";
        var newPass = "";
        var currentID = 0;

        var driverName = "org.apache.hive.jdbc.HiveDriver"
        val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
        val filepath = "/tmp/users.csv";

        println("What would you like to change? Username (1), password  (2), both (3), exit (4)");
        do {
            try {
                Class.forName(driverName);
                con = DriverManager.getConnection(conStr, "", "");
                val stmt = con.createStatement();

                option = scala.io.StdIn.readInt();
                if(option > 0 && option < 4) { // Valid options
                    println("Please enter your username:");
                    var oldUsername = scala.io.StdIn.readLine();
                    if(option != 2) { // Option 2 not allowed
                        var validUsername = true;
                        
                        do {
                            println("What username would you like to have: ")
                            newUsername = scala.io.StdIn.readLine();
                            var sql = "select * from " + tableName;
                            var res = stmt.executeQuery(sql);
                            while(res.next()) {
                                if(newUsername == res.getString(1))
                                    validUsername = false;
                            }
                            if(!validUsername)
                                println("Username has been taken. Choose another one")
                        } while(!validUsername)
                        validOption = true;
                    }
                    if(option != 1) { // Option 1 not allowed
                        println("What password would you like to have: ");
                        newPass = scala.io.StdIn.readLine();
                    }

                    println("Are you sure? (Y) or (N)");
                    var check = false;
                    var checkOp = '0';
                    try { // Invalid entries are assumed to be no.
                        checkOp = scala.io.StdIn.readChar();
                    }
                    catch {
                        case _: Throwable => println("Invalid entry. Assuming no.")
                    }
                    if(checkOp == 'Y') {
                        var sql = "select * from " + tableName;
                        var res = stmt.executeQuery(sql);
                        
                        while(res.next()) {
                            if(res.getString(2) == oldUsername)
                                currentID = res.getInt(1);
                        }
                        var out = currentID.toString() + "," + newUsername + "," + newPass + ",false\n";

                        
                        val user_file = new File("/tmp/users.csv");
                        val user_source = Source.fromFile(user_file);
                        val user_writer = new FileWriter(user_file, true);

                    }
                }
                else if(option == 4) { // Nothing changes, exit
                    println("Exiting...")
                    validOption = true;
                }
                else
                    println("Invalid option. Try again. Enter 4 to exit.\n")

            }
            catch {
                case _ : Throwable => println("Invalid Entry. Try again\n");
            }

        } while(!validOption)
    }

}