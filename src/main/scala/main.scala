import login.Login;
import login.menu;

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
        
        menu.menuOptions;
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