package rentserver.controller;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RentServer {
  private static final int PORT = 40000;
  private static ClientConnectionsHandler clientConnectionsHandler;
  private static final Logger logger = Logger.getLogger(RentServer.class.getName());
  private static ServerSocket rentServerSocket = null;
  
  public static void main(String[] args) throws IOException {
    
    if (rentServerNotYetRunning(rentServerSocket)) {
      StartRentServerComponents(rentServerSocket);
      WaitForLocalCommands();
    }
  }

  private static boolean rentServerNotYetRunning(ServerSocket rentServerSocket) throws IOException {
    try {
      rentServerSocket = new ServerSocket(PORT);
    } catch (BindException ex) {
      logger.info("Server already running, exiting ...");
    }
    return rentServerSocket != null;
  }

  private static void StartRentServerComponents(ServerSocket rentServerSocket) {
    logger.info("Client Connection Handler is starting ...");
    //TODO hand over logger as well?
    clientConnectionsHandler = ClientConnectionsHandler.create(rentServerSocket);
    clientConnectionsHandler.start();
    logger.info("Client Connection Handler has started.");
  }

  private static void StopRentServerComponents() {
    try {
      logger.info("Stopping Client Connection Handler ...");
      clientConnectionsHandler.terminate();
      clientConnectionsHandler.join();
      logger.info("Client Connection Handler successfully stopped.");
    } catch (InterruptedException ex) {
      logger.log(Level.SEVERE, null, ex);
    }
  }

  private static void WaitForLocalCommands() {
    String command;
    Scanner console = new Scanner(System.in);
    do {
      System.out.print("RentServer> ");
      command = console.nextLine().trim().toLowerCase();
      executeCommand(command);
    } while (!command.equals("stop"));
  }

  private static void executeCommand(String command) {
    switch (command) {
      case "stop":
        StopRentServerComponents();
        break;
      default:
        System.out.println("\"" + command + "\" not valid. No action.");
    }
  }
}
