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
    def login(): Unit = {
        var exit = false;
        var loginOption = 0;
        var validLogin = false;
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
            System.out.println(res);
            /*
            while (res.next()) {
                System.out.println(
                String.valueOf(res.getInt(1)) + "\t" + res.getString(2)
                );
            }*/

            do {
                try {
                    println("Login (1), Sign Up (2)");
                    loginOption = scala.io.StdIn.readInt();
                    if(loginOption == 1 || loginOption == 2) {
                        println("You pick a correct a option!");
                        exit = true;
                    }
                    else {
                        println("You did not pick a valid option");
                    }
                }
                catch {
                    case _ : Throwable => println("Invalid Input! Try again")
                }

                if(loginOption == 1) { // Login
                    try {
                        var inside = false;

                        println("Enter Username:");
                        loginInfo(0) = scala.io.StdIn.readLine();
                        println("Enter Paswword:");
                        loginInfo(1) = scala.io.StdIn.readLine();
                        println("Verifying...");
                        while(res.next()) {
                            if(loginInfo(0) == res.getString(2)) {
                                if(loginInfo(1) == res.getString(3))
                                    inside = true;
                            }
                        }
                        if(inside)
                            println("Hello" + loginInfo(0));
                    }
                    catch {
                        case _: Throwable => println("Invalid");
                    }
                }
                else if(loginOption == 2) {
                    var validEntry = false; 
                    do {
                        try {
                            println("Enter Username:")
                            loginInfo(0) = scala.io.StdIn.readLine();
                            println("Enter Password:")
                            loginInfo(1) = scala.io.StdIn.readLine();
                            validEntry = true;

                        }
                        catch {
                            case _: Throwable => println("Invalid");
                        }


                    } while (!validEntry)
                }
            } while (!exit)
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
    }
    

}