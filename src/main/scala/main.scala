import scala.io.Source;
import java.io.File;
import java.io._;
import org.mongodb.scala._;
import scala.io.StdIn.readLine;

object Main extends App {
  val user_file = new File("output.json");
  val user_source = Source.fromFile(user_file);
  val user_writer = new FileWriter(user_file, true);

  println("Welcome!");
    println("What are we doing today?")
    println("1, 2, 3, 4, 5,");
    println("For a list of countries: 1")
    println("For a list of league in a country: 2")

    val apiKey = 40130162;  

  val url = "https://www.thesportsdb.com/api/v1/json/40130162/lookupeventstats.php?id=1032723";
  val result = scala.io.Source.fromURL(url).getLines.toList.map(x => Document(x));
  val out = result(0).get("eventstats").get.asArray().getValues().get(0).asDocument.get("strEvent").asString.getValue;

  println(out);
  //user_writer.write(result);

  user_writer.close();
  user_source.close();
}