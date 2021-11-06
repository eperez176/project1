package login

object menu {
  def menuOptions(admin: Int): Int = {
    println("Welcome!")
    println("To change username/password (1)");
    println("To print out a list of certain criteria (2)")
    println("To search specific criteria via ID (3)")
    println("For future events for a team (4)")
    println("For analysis (5)")
    println("To print user's info (6)")
    if(admin == 2) {
        println("Admin: Delete Users (7)")
        println("List of Users Info (8)" +
          "")
    }
    println("To log out (9)")
    println("To exit (10)")

    var validEntry = false;
    var input = 10;
    do { // Make sure a valid input is added without causing errors
        try {
            input = scala.io.StdIn.readInt();
            if((input > 0 && input <= 10) && input != 7)
                validEntry = true;
            else if( (input == 7 || input == 8) && admin == 2) // Only admins are allowed to delete users or print all user info
                validEntry = true;
            else
                println("Invalid Option. Try again.\n")
        }
        catch {
            case _: Throwable => println("Invalid Entry. Try again\n")
        }
    }   while(!validEntry)

    return input;
  }
}
