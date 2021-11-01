import login.Login;

import scala.io.Source;
import java.io.File;
import java.io._;
import org.mongodb.scala._;
import scala.io.StdIn.readLine;

object Main extends App {
  var exit = false;
  var exitMain = false;

  while (!exitMain) {

    var userInfo = Login.login(); // Contains user's ID and admin status

    do {
      if(userInfo._2 == 1 || userInfo._2 == 2) {
        println("To change username/password (1)");
        println("To print user's info (6)")
        println("To exit (10)")
        var options = scala.io.StdIn.readInt();
        if(options == 1)
          Login.updateLogin(userInfo._1);
        else if(options == 10){
          exit = true;
          exitMain = true;
        }
        else if(options == 6)
          Login.userInfo(userInfo._1);
        
      }
      else {
        println("FAIL")
      }
    } while(!exit)
    println("Good bye!\n")
  }
}

      /* 
      val apiKey = 40130162;
      val url = "https://www.thesportsdb.com/api/v1/json/40130162/lookupeventstats.php?id=1032723";
      val result = scala.io.Source.fromURL(url).getLines.toList.map(x => Document(x));
      val out = result(0).get("eventstats").get.asArray().getValues().get(0).asDocument.get("strEvent").asString.getValue;

      println(out); */