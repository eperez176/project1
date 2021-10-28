package  example;

object Solution extends App {
    //INPUT [uncomment & modify if required]
    var sampleInput = scala.io.StdIn.readLine();
    var result = -404;
    //write your Logic here:
    var temp = "";
    var newA = sampleInput.split(" "); // Make an array of substrings
    var newB = new Array[Int](newA.length);
    for(i <- 0 to newA.length-1) { // Transform the array of substring to
        newB(i) = newA(i).toInt; // An array of integers
    }
    for(i <- 0 to newB.length-2) { // Check each array element to find inconsistent order
        if(newB(i)+1 != newB(i+1))
            result = newB(i)+1
    }


    //OUTPUT [uncomment & modify if required]
    System.out.println(result);
    
}