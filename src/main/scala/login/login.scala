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
    def login(): (Int, Int) = {
        var exit = false;
        var loginOption = 0;
        var validLogin = false;
        var inside = false;
        var isAdmin = false;
        var loginInfo = new Array[String](2); // Structure having username & pass
        var con: java.sql.Connection = null;

        var currentID = -1;

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
            var sql = "load local data inpath '" + filepath + "' into table " + tableName;
            System.out.println("Running: " + sql);
            stmt.execute(sql);

            sql = "select * from " + tableName;
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
                                    currentID = res.getInt(1);
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
                                currentID = lastId + 1;
                                isAdmin = false;
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
            return (currentID, 2); // Admin User
        }
        else if(exit)
            return (currentID, 1); // Regular User
        else
            return (currentID, 0); // Invalid User
    }
    
    def updateLogin(currentID: Int): Unit = {
        var option = 0;
        var validOption = false;
        var con: java.sql.Connection = null;
        val tableName = "Users";

        var newUsername = "";
        var newPass = "";

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
                    if(option != 2) { // Option 2 not allowed
                        var validUsername = true;
                        do {
                            validUsername = true; // Reset
                            println("\nWhat username would you like to have: ")
                            newUsername = scala.io.StdIn.readLine();
                            var sql = "select * from " + tableName;
                            var res = stmt.executeQuery(sql);
                            while(res.next()) { // Checks if the username is unique
                                if(newUsername == res.getString(2))
                                    validUsername = false;
                            }
                            if(!validUsername)
                                println("Username has been taken. Choose another one")
                        } while(!validUsername)
                        validOption = true;
                    }
                    if(option== 2) {
                        var sql = "select * from " + tableName;
                        var res = stmt.executeQuery(sql);
                            while(res.next()) { // Grab old password using same username
                                if(currentID == res.getInt(1))
                                    newUsername = res.getString(2);
                            }
                    }
                    if(option != 1) { // Option 1 not allowed
                        println("\nWhat password would you like to have: ");
                        newPass = scala.io.StdIn.readLine();
                    }
                    else if (option == 1) { // In case of option 4
                        var sql = "select * from " + tableName;
                        var res = stmt.executeQuery(sql);
                            while(res.next()) { // Grab old password using user's ID
                                if(currentID == res.getInt(1))
                                    newPass = res.getString(3);
                            }
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
                        var out = currentID.toString() + "," + newUsername + "," + newPass + ",false\n";

                        val user_file = new File("/tmp/users.csv");
                        val user_source = Source.fromFile(user_file);
                        val oldFile = user_source.mkString.split("\n");
                        var old_writer = new FileWriter(user_file, false);

                        var lineSplit = "";
                        old_writer.close();
                        old_writer = new FileWriter(user_file, true);
                        try {
                            for (i <- 0 until oldFile.length) {
                                lineSplit = oldFile(i).split(",").head;
                                var newL = oldFile(i) + "\n";
                                if(lineSplit.toInt != currentID) // If given incorrect username
                                    old_writer.write(newL);
                                else    
                                    old_writer.write(out);
                            }
                        }
                        catch {
                            case ex : Throwable => ex.printStackTrace();
                        }
                        finally { // Close everything
                            user_source.close();
                            old_writer.close();
                        }

                        // Reload the table
                        println("\nUpdating...")
                        val tableName = "Users";
                        stmt.execute("drop table IF EXISTS " + tableName);
                        stmt.execute("create table " + tableName + " (key int, username string, password string, admin boolean) row format delimited  fields terminated by ','");
                        val filepath = "/tmp/users.csv";
                        var sql = "load data local inpath '" + filepath + "' into table " + tableName;
                        stmt.execute(sql);
                        println("Done. Exiting...\n")
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
            finally {
                try { // Closing client
                    if (con != null)
                        con.close();
                } catch {
                    case ex : Throwable =>  {
                        ex.printStackTrace();
                        throw new Exception(s"${ex.getMessage}")
                    }
                }
            }
        } while(!validOption)
    }

    def userInfo(currentID: Int): Unit = { // Prints user's info
        var con: java.sql.Connection = null;
        val tableName = "Users";

        var driverName = "org.apache.hive.jdbc.HiveDriver"
        val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
        try {
            Class.forName(driverName);
            con = DriverManager.getConnection(conStr, "", "");
            val stmt = con.createStatement();
            println(currentID)
            var sql = "select * from " + tableName;
            var res = stmt.executeQuery(sql);
            while(res.next()) {
                if(res.getInt(1) == currentID) {
                    println("User's Info")
                    println("User ID: " + res.getString(1));
                    println("Username: " + res.getString(2));
                    println("Password: " + res.getString(3));
                    println("Admin: : " + res.getString(4));
                    println;
                }
            }


        }
        catch {
            case ex: Throwable => ex.printStackTrace();
        }
        finally {
            try { // Closing client
                    if (con != null)
                        con.close();
                } catch {
                    case ex : Throwable =>  {
                        ex.printStackTrace();
                        throw new Exception(s"${ex.getMessage}")
                    }
                }
        }
    }

}