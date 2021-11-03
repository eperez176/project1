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
    val url = "https://www.thesportsdb.com/api/v1/json/1/lookupcontracts.php?id=34147178";
    val result = scala.io.Source.fromURL(url).mkString;
    
    var N = 0;

    // var theStack = Stack[Char]();
    for(i <- 0 until result.length) {
        if(result(i) == '{')
            N = N + 1;
    }

    var A = new Array[String](N);
    for(i <- 0 until A.length)
        A(i) = "";
    var count = -1;

    println("The raw input: \n" + result);

    for(i <- 0 until result.length) {
        if(result(i) == '{')
            count = count + 1;
        else if( (result(i) != '{' || result(i) != '}') && count > 0   )
            A(count) = A(count) + result(i).toString
        else if (result(i) == '}')
            count = count - 1;
    } 
    val dQ = '"';
    var B = new Array[String](A.length);
    var unitedS = "";
    var endOF = 0;
    var backL = '/';
    var fowardL = (92).toChar

    println("\nA Size: " + A.size.toString + "\n")
    for(line <- A) {
        endOF = endOF+1;
        if(!line.isEmpty() && A.size > 2 && endOF != A.size) {
            unitedS = unitedS + line + "#";
        }
        else if(!line.isEmpty())
            unitedS = unitedS + line;
    }   
    println("\nThe Semi Parsed Input: \n" + unitedS);

    if(A.size > 2)
        B = unitedS.replace(dQ.toString, "").replace("}", "").replace("]", "").replace("https:" + fowardL + backL + fowardL + backL, "").replace(fowardL.toString, "").replace(", ", ". ").split("#");
        //println(unitedS.replace(dQ.toString, "").replace("}", "").replace("]", ""))
    else
        B(1) = unitedS.replace(dQ.toString, "").replace("}", "").replace("]", "").replace("https:" + fowardL + backL + fowardL + backL, "").replace(fowardL.toString, "").replace(", ", ". ");

    println("\nSecond Parsed Data: ")
    B.foreach(println)
    println
    var C = Array.ofDim[String](B.length, B(1).split(",").length)
    if(A.size > 2) {
        for(i <- 0 until B.length) { // Array having of record into several fields
            C(i) = B(i).split(",")
        }
    }
    else {
        C(1) = B(1).split(",")
    }
    var D = Array.ofDim[String](2, C(1).length)
    var out = "";
    var out2 = "";

    println("\nPARSED DATA:")

    //println("\n" + unitedS.replace("https:" + fowardL + backL + fowardL + backL, ""))
    if(A.size > 2) {
        for(j <- 0 until C.length) {
            if(C(j).length != 1) { // The empty data strings removed
                for(i <- 0 until C(j).length) {
                    if(C(j)(i).count(c => c == ':') > 1) {
                        C(j)(i) = C(j)(i).replace(":", "#");
                        C(j)(i) = C(j)(i).replaceFirst("#", ":");
                    }
                        
                    D(0)(i) = C(j)(i).split(":").head;
                    try {
                        D(1)(i) = (C(j)(i).split(":"))(1)
                    }
                    catch {
                        case _: Throwable => D(1)(i) = "null"
                    }
                }
                for(j <- 0 until D(1).length)
                    D(1)(j) = D(1)(j).replace("#",":")
                
                out = D(1).reduceLeft(_ + ","+ _);
                out2 = D(0).reduceLeft(_ + "," + _);
                println(out2)
                println(out)
                println
            }
        }
    }
    else {
        for(i <- 0 until C(1).length) {
            D(0)(i) = C(1)(i).split(":").head;
            if(C(1)(i).count(c => c == ':') > 1) {
                C(1)(i) = C(1)(i).replace(":", "#");
                C(1)(i) = C(1)(i).replaceFirst("#", ":");
            }
            try {
                D(1)(i) = (C(1)(i).split(":"))(1)
            }
            catch {
                case _: Throwable => D(1)(i) = "null"
            }
        }
        for(j <- 0 until D(1).length)
            D(1)(j) = D(1)(j).replace("#",":")
        out = D(1).reduceLeft(_ + ","+ _);
        out2 = D(0).reduceLeft(_ + "," + _);
        println(out2)
        println(out)
        println
    }
}
