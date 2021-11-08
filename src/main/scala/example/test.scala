package example
import parser._
import scala.io.Source;
import java.io.File;
import java.io._;
import scala.io.StdIn.readLine;
import java.io.IOException
import scala.util.Try

object test{
        println("Retrieving information from the API...");
        var input = "";
        var output = "";
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
