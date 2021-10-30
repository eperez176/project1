import login.Login;

import scala.io.Source;
import java.io.File;
import java.io._;
import org.mongodb.scala._;
import scala.io.StdIn.readLine;

object Main extends App {
  var userType = 0;
  var exit = false;

  while ( 1 == 1 ) {

    userType = Login.login();

    do {
      if(userType == 1 || userType == 2) {
        println("What would you like to do?");
        println("To change username/password (1)");
        var something = scala.io.StdIn.readLine();
        
      }
      else {
        println("FAIL")
      }
    } while(!exit)
  }
}

      /* 
      val apiKey = 40130162;
      val url = "https://www.thesportsdb.com/api/v1/json/40130162/lookupeventstats.php?id=1032723";
      val result = scala.io.Source.fromURL(url).getLines.toList.map(x => Document(x));
      val out = result(0).get("eventstats").get.asArray().getValues().get(0).asDocument.get("strEvent").asString.getValue;

      println(out); */