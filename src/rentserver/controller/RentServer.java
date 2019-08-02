package rentserver.controller;

import java.util.Scanner;

public class RentServer {

  public static void main(String[] args) {
    Scanner console = new Scanner(System.in);
    String command;
    do {
      command = console.nextLine();
    } while (!command.trim().equalsIgnoreCase("stop"));
  }
}
