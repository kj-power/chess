package ui.client;

import server.ServerFacade;
import ui.client.websocket.State;

import java.util.Arrays;

public class PostLoginClient {
    private String username = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final client.websocket.NotificationHandler notificationHandler;
    private State state = State.SIGNEDOUT;

    public PreLoginClient(String serverUrl, client.websocket.NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public PostLoginClient(ServerFacade server, String serverUrl, client.websocket.NotificationHandler notificationHandler) {
        this.server = server;
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "quit" -> "quit";
                case "logout" -> logout(params);
                case "create game" -> create(params);
                case "list games" -> list(params);
                case "play game" -> play(params);
                case "observe game" -> observe(params);
                default -> help();
            };
        } catch (exception.ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - create a game <NAME>
                - list games
                - join game <ID> [WHITE|BLACK]
                - observe game <ID>
                - logout
                - quit
                - help
                """;
    }




}
