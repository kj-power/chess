package ui.client;

import requests.LoginRequest;
import requests.RegisterRequest;
import server.ServerFacade;
import ui.client.websocket.NotificationHandler;
import ui.client.websocket.State;
import ui.client.websocket.WebSocketFacade;

import java.util.Arrays;

public class PreLoginClient {
    private String username = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;

    public PreLoginClient(ServerFacade server, String serverUrl, NotificationHandler notificationHandler) {
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
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (exception.ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit
                - help
                """;
    }

    public String login(String... params) throws exception.ResponseException {
        if (params.length < 2) {
            throw new exception.ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
        }

        var request = new LoginRequest(params[0], params[1]);
        var result = server.login(request);

        if (result != null && result.authToken() != null) {
            this.state = State.SIGNEDIN;
            this.username = params[0];
            server.setAuthToken(result.authToken());
            return String.format("Logged in as %s.", this.username);
        } else {
            throw new exception.ResponseException(401, "Login failed");
        }
    }

    public String register(String... params) throws exception.ResponseException {
        if (params.length < 3) {
            throw new exception.ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
        }

        var request = new RegisterRequest(params[0], params[1], params[2]);
        var result = server.register(request);

        if (result != null && result.authToken() != null) {
            this.state = State.SIGNEDIN;
            this.username = params[0];
            server.setAuthToken(result.authToken());
            return String.format("Registered as %s.", this.username);
        } else {
            throw new exception.ResponseException(401, "Registration failed");
        }
    }

}
