package rentserver.services;

import java.util.ArrayList;
import rentserver.controller.ClientState;

public class Commands {
  private final ClientState state;
  private final ArrayList<String> commandsForClient;
  
  public Commands(ClientState state) {
    this.commandsForClient = new ArrayList<>();
    this.state = state;
  }
  
  public ArrayList process(String command) {
    commandsForClient.clear();
    if (state.isCommandAvailable(command)) {
      execute(command);
    } else {
      commandsForClient.add("message Command not available or not a valid command.");
      commandsForClient.add("copy");
    }
    return commandsForClient;
  }
  
    private void execute(String command) {
    switch (command) {
      case "login":
        commandsForClient.add("message copy login");
        commandsForClient.add("copy");
        break;
      case "register":
        commandsForClient.add("message copy register");
        commandsForClient.add("copy");
        break;
      case "disconnect":
        commandsForClient.add("message Bye-bye");
        commandsForClient.add("disconnect");
      default:
    }
  }
}
