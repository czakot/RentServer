package rentserver.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnection extends Thread {
  private static ClientConnectionsHandler clientConnectionsHandler;
  private static final String WELCOME = "Welcome at Rent Server V0.1";
  private static final String[] CLIENT_UI_TYPES = {"console"};
  private static final String[] SUPPORTED_LOCALS = {"en_US", "hu_HU"};
  private static final Logger logger = Logger.getLogger(ClientConnection.class.getName());
  
  private  Socket clientSocket;
  private  int id;
  private  Scanner sc;
  private  PrintWriter pw;
  private ClientStatus clientStatus = new ClientStatus();
  
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
    // set initial client properties
    // start state machine
    // get command sequence
    // execute command sequence
    // update state machine
    // wait user input or goto row3: get comm...
    // update state machine
    // goto row3: get comm...
    welcomeClient();

    if (recognizedClientUserInterface()) {
      /*clientLocals = */getClientLocals();
      do {
        receiveCommandFromClient();
      } while (!clientSocket.isClosed());
    }
  }

  private void welcomeClient() {
    sendToClient("message " + WELCOME);
  }

  private boolean recognizedClientUserInterface() {
    sendToClient("question_user_interface_type");
    String received = sc.nextLine();
    for (String uiType : CLIENT_UI_TYPES) {
      if (received.equals(uiType)) {
        clientStatus.setClientUIType(received);
        return true;
      }
    }
    String msg = "Client''s (connection id: {0}) user interface not recognized. Received: \"{1}\".";
    logger.log(Level.INFO, msg, new Object[]{id, received});
    return false;
  }

  private void /*String*/ getClientLocals() {
    sendToClient("question_locals");
    clientStatus.setClientLocals(sc.nextLine());
/*
    String received = sc.nextLine();
    for (String locals : SUPPORTED_LOCALS) {
      if (received.equals(locals)) {
        clientLocals = locals;
        return clientLocals;
      }
    }
    return SUPPORTED_LOCALS[0];
*/
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
    sendToClient("terminate");
    try {
      clientSocket.close();
    } catch (IOException ex) {
      Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private void sendToClient(String msg) {
    synchronized(pw) {
      pw.println(msg);
      pw.flush();
    }
  }
}
