package login

object menu {
  def menuOptions: Unit = {
    println("Welcome!")
    println("To change username/password (1)");
    println("To print out a list of certain criteria (2)")
    println("To search specific criteria via ID (3)")
    println("For future events for a team (4)")
    println("For analysis (5)")
    println("To print user's info (6)")
    println("To exit (10)")
  }
}
