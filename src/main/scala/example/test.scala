package example

import scala.io.Source;
import java.io.File;
import java.io._;
import scala.io.StdIn.readLine;
import java.io.IOException
import scala.util.Try

import scala.collection.mutable.Stack

object test extends App {
    println("testing...");
    val apiKey = 40130162;
    val url = "https://www.thesportsdb.com/api/v1/json/40130162/lookupeventstats.php?id=1032723";
    val result = scala.io.Source.fromURL(url).mkString;
    
    var N = 0;

    // var theStack = Stack[Char]();
    for(i <- 0 until result.length) {
        if(result(i) == '{')
            N = N + 1;
    }

    println(N)
    println(result)

    var A = new Array[String](N);
    for(i <- 0 until A.length)
        A(i) = "";
    var count = -1;

    for(i <- 0 until result.length) {
        if(result(i) == '{')
            count = count + 1;
        else if( (result(i) != '{' || result(i) != '}') && count > 0   )
            A(count) = A(count) + result(i).toString
        else if (result(i) == '}')
            count = count - 1;
        
        //println("Count: " + count.toString)
    } 
    val dQ = '"';
    var count2 = 0;
    var B = new Array[String](50);
    var unitedS = "";

    for(line <- A) {
        unitedS = unitedS + line + "/";
    }
    println(unitedS)
    B = unitedS.replace(dQ.toString, "").replace("}", "").split("/");
    println
    B.foreach(println)

        
}
