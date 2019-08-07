package rentserver;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import rentserver.controller.ClientConnectionsHandler;

public class RentServer {
  private static final int PORT = 40000;
  private static ClientConnectionsHandler clientConnectionsHandler;
  private static final Logger logger = Logger.getLogger(RentServer.class.getName());
  private static ServerSocket rentServerSocket = null;
  
  public static void main(String[] args) throws IOException {
    
    if (rentServerNotYetRunning()) {
      StartRentServerComponents();
      WaitForLocalCommands();
    }
  }

  private static boolean rentServerNotYetRunning() throws IOException {
    try {
      rentServerSocket = new ServerSocket(PORT);
    } catch (BindException ex) {
      logger.info("Server already running.");
    }
    return rentServerSocket != null;
  }

  private static void StartRentServerComponents() {
    logger.info("Client Connections Handler is starting ...");
    clientConnectionsHandler = ClientConnectionsHandler.create(rentServerSocket);
    clientConnectionsHandler.start();
    logger.info("Client Connections Handler has started.");
  }

  private static void StopRentServerComponents() {
    try {
      logger.info("Stopping Client Connections Handler ...");
      clientConnectionsHandler.terminate();
      clientConnectionsHandler.join(3*ClientConnectionsHandler.getSERVER_SOCKET_TIMEOUT());
      logger.info("Client Connections Handler successfully stopped.");
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
