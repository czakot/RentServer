package rentserver.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;

public class ClientState {
  private String clientUIType = null;
  private String clientLocals;
  private Locale clientLocale;
  private String roletype;
  private String user;

  private final LinkedList<String> availableCommands = new LinkedList<>();
  
  public ClientState() {
    roletype = "anonymus";
    user = null;
    String[] commandsForAnonymus = {"login", "register", "disconnect"};
    availableCommands.addAll(Arrays.asList(commandsForAnonymus));
  }
  
  public boolean isCommandAvailable(String command) {
    return availableCommands.contains(command);
  }
  
  public String getClientUIType() {
    return clientUIType;
  }

  public void setClientUIType(String clientUIType) {
    this.clientUIType = clientUIType;
  }

  public String getClientLocals() {
    return clientLocals;
  }

  public void setClientLocals(String clientLocals) {
    this.clientLocals = clientLocals;
    String[] cls = clientLocals.split("_");
    clientLocale = new Locale(cls[0], cls[1]);
  }

  public Locale getClientLocale() {
    return clientLocale;
  }

  public void setClientLocale(Locale clientLocale) {
    this.clientLocale = clientLocale;
    clientLocals = clientLocale.toString();
  }

  public String getRoletype() {
    return roletype;
  }

  public void setRoletype(String roletype) {
    this.roletype = roletype;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }
}
