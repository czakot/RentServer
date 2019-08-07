package rentserver.controller;

import java.util.LinkedList;
import java.util.Locale;

public class ClientStatus {
  private String clientUIType = null;
  private String clientLocals;
  private Locale clientLocale;
  private String user="anonymus";
  private final LinkedList<String> availableCommands = new LinkedList<>();
  

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

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }
}
