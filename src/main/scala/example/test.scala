package example

import scala.io.Source;
import java.io.File;
import java.io._;
import scala.io.StdIn.readLine;
import java.io.IOException
import scala.util.Try

object test{
    println("testing...");
    val user_file = new File("users.csv");
    val user_source = Source.fromFile(user_file);

    val check = user_source.mkString.split("\r");
    var old_writer = new FileWriter(user_file, false);
    old_writer.close();
    old_writer = new FileWriter(user_file, true);

    var count = 0;
    
    for(line <- check) {
        count = count + 1;
        var out = count.toString + line;
        old_writer.write(out);
    }
    
  
    user_source.close();
    old_writer.close();
}
