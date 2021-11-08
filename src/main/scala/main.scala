import login.Login;
import login.menu;
import parser.Parser;

import scala.io.Source;
import java.io.File;
import java.io._;
import org.mongodb.scala._;
import scala.io.StdIn.readLine;

object Main extends App {
  var exit = false;
  var exitMain = false;
  var options = 0;

  while (!exitMain) {
    exit = false;
    var userInfo = Login.login(); // Contains user's ID and admin status

    do {
      if(userInfo._2 == 1 || userInfo._2 == 2) {
        
        options = menu.menuOptions(userInfo._2); // Checks for admin
        if(options == 1)
          Login.updateLogin(userInfo._1);
        else if (options == 2) {
          menu.printList
        }
        else if(options == 3)
          menu.search
        else if(options == 4)
          menu.printEvents
        else if(options == 5)
          menu.analysis
        else if(options == 9) {
          exit = true;
        }
        else if(options == 10){
          exit = true;
          exitMain = true;
        }
        else if(options == 6)
          Login.userInfo(userInfo._1);
        else if(options == 7)
          Login.adminDelete
        else if(options == 8) // Print of user's ID and username
          Login.adminUserList;
        
      }
      else {
        println("FAIL")
      }
    } while(!exit)
    println("Good bye!\n")
  }
}