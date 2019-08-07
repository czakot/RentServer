package rentserver.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionsHandler extends Thread {
  private static final int SERVER_SOCKET_TIMEOUT = 5000; // millisecs; should be longer ~15000
  private static ClientConnectionsHandler instance = null;

  private final ServerSocket rentServerSocket;
  private volatile Boolean running = true;
  private final Map<Integer,ClientConnection> activeClientConnections = new ConcurrentHashMap<>();
  private int nextClientConnectionId = 0;

  private ClientConnectionsHandler(ServerSocket rentServerSocket) {
    this.rentServerSocket = rentServerSocket;
    try {
      rentServerSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT);
    } catch (SocketException ex) {
      Logger.getLogger(ClientConnectionsHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  static public ClientConnectionsHandler create(ServerSocket rentServerSocket) {
    if (instance == null) {
      instance = new ClientConnectionsHandler(rentServerSocket);
    }
    return instance;
  }
  
  @Override
  public void run() {
    while (running) {
      try {
        Socket clientSocket = rentServerSocket.accept();
        createClientConnection(clientSocket);
      } catch (SocketTimeoutException ex) {
          //Nothing to do, socket timeout only for cyclic check of running flag
      } catch (IOException ex) {
        Logger.getLogger(ClientConnectionsHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    closeActiveClientConnections();
    instance = null;
  }

  private void createClientConnection(Socket clientSocket) {
    ClientConnection clientConnection = new ClientConnection(clientSocket, nextClientConnectionId);
    synchronized(activeClientConnections) {
      activeClientConnections.put(nextClientConnectionId, clientConnection);
    }
    incrementNextClientConnectionId();
    clientConnection.start();
  }

  private void incrementNextClientConnectionId() {
    do {
      ++nextClientConnectionId;
      nextClientConnectionId %= Integer.MAX_VALUE;
    } while (activeClientConnections.get(nextClientConnectionId) != null);
  }

  private void closeActiveClientConnections() {
    Integer[] keys = getActiveClientConnections();
    for (Integer key : keys) {
      ClientConnection clientConnection = activeClientConnections.get(key);
      closeClientConnection(clientConnection);
    }
    while (!activeClientConnections.isEmpty()) {
    }
  }

  private Integer[] getActiveClientConnections() {
    synchronized(activeClientConnections) {
    return activeClientConnections.keySet().toArray(new Integer[0]);
    }
  }

  private void closeClientConnection(ClientConnection clientConnection) {
    if (clientConnection != null) {
      try {
        clientConnection.terminate();
      } catch (RuntimeException ex) {
        // Nothing to do. Already nonexistent thread's method was called.
      }
    }
  }

  public void terminate() {
    running = false;
  }
}
