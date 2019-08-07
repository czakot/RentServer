package rentserver.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import rentserver.services.Commands;

public class ClientConnection extends Thread {
  private static final String WELCOME = "Welcome at Rent Server V0.1";
  private static final String[] CLIENT_UI_TYPES = {"console"};
  private static final Logger logger = Logger.getLogger(ClientConnection.class.getName());
  
  private  Socket clientSocket;
  private  int id;
  private  final Scanner sc;
  private  final PrintWriter pw;
  private ClientState clientState = new ClientState();
  private Commands commands = new Commands(clientState);
  
  ClientConnection(Socket clientSocket, int id) {
    this.clientSocket = clientSocket;
    this.id = id;
    
    Scanner sct = null;
    PrintWriter pwt = null;
    try {
      sct = new Scanner(clientSocket.getInputStream());
      pwt = new PrintWriter(clientSocket.getOutputStream());
    } catch (IOException ex) {
      Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
    sc = sct;
    pw = pwt;
  }
  
  @Override
  public void run() {
    welcomeClient();
    if (recognizedClientUserInterface()) {
      getClientLocals();
      do {
        handleCommandFromClient();
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
        clientState.setClientUIType(received);
        return true;
      }
    }
    String msg = "Client''s (connection id: {0}) user interface not recognized. Received: \"{1}\".";
    logger.log(Level.INFO, msg, new Object[]{id, received});
    return false;
  }

  private void  getClientLocals() {
    sendToClient("question_locals");
    clientState.setClientLocals(sc.nextLine());
    sendToClient("copy");
  }

  private void handleCommandFromClient() {
    String commandFromClient;
    ArrayList<String> replies;
    if (sc.hasNextLine()) {
      commandFromClient = sc.nextLine().trim().toLowerCase();
      replies = commands.process(commandFromClient);
      for (String reply : replies) {
        sendToClient(reply);
        if (reply.equals("disconnect")) {
          try {
            clientSocket.close();
          } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

  public void terminate() {
    sendToClient("terminate");
    try {
      clientSocket.close();
    } catch (IOException ex) {
      Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public void sendToClient(String command) {
    synchronized(pw) {
      pw.println(command);
      pw.flush();
    }
  }
}
