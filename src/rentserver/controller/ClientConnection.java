package rentserver.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnection extends Thread {
  private static ClientConnectionsHandler clientConnectionsHandler;
  private static final String[] CLIENT_UI_TYPE = {"console"};
  private static final String[] SUPPORTED_LOCALS = {"en_US", "hu_HU"};
  private static final Logger logger = Logger.getLogger(ClientConnection.class.getName());
  
  private  Socket clientSocket;
  private  int id;
  private  Scanner sc;
  private  PrintWriter pw;
  private String clientUIType = null;
  private String clientLocals;
  private final LinkedList<String> availableCommands = new LinkedList<>();
  
  ClientConnection(Socket clientSocket, int id) {
    this.clientSocket = clientSocket;
    this.id = id;
    try {
      sc = new Scanner(clientSocket.getInputStream());
      pw = new PrintWriter(clientSocket.getOutputStream());
    } catch (IOException ex) {
      Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  @Override
  public void run() {
    if (recognizedClientUserInterface()) {
      clientLocals = getClientLocals();
      do {
        receiveCommandFromClient();
      } while (!clientSocket.isClosed());
    }
  }

  private boolean recognizedClientUserInterface() {
    String received = sc.nextLine();
    for (String uiType : CLIENT_UI_TYPE) {
      if (received.equals(uiType)) {
        clientUIType = received;
        return true;
      }
    }
    String msg = "Client''s (connection id: {0}) user interface not recognized. Received: \"{1}\".";
    logger.log(Level.INFO, msg, new Object[]{id, received});
    return false;
  }

  private String getClientLocals() {
    String received = sc.nextLine();
    for (String locals : SUPPORTED_LOCALS) {
      if (received.equals(locals)) {
        clientLocals = locals;
        return clientLocals;
      }
    }
    return SUPPORTED_LOCALS[0];
  }

  private void receiveCommandFromClient() {
    String command;
    command = sc.nextLine().trim().toLowerCase();
    executeCommand(command);
  }

  private void executeCommand(String command) {
    switch (command) {
      case "console":
        pw.println("no message");
        pw.println("login register logout");
        pw.flush();
        break;
      case "logout":
        pw.println("bye-bye");
        pw.flush();
      default:
        System.out.println("\"" + command + "\" not valid. No action.");
        pw.println("login register logout");
    }
  }

  public static void setClientConnectionsHandler(ClientConnectionsHandler clientConnectionsHandler) {
    ClientConnection.clientConnectionsHandler = clientConnectionsHandler;
  }
  
  public void terminate() {
    synchronized(pw) {
      pw.println("copy | terminate");
    }
    try {
      clientSocket.close();
    } catch (IOException ex) {
      Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
