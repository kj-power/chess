package ui.client;

import chess.ChessGame;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import results.ListResult;
import server.ServerFacade;
import ui.client.websocket.State;

import java.util.Arrays;

public class PostLoginClient {
    private String name = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final client.websocket.NotificationHandler notificationHandler;
    private State state = State.SIGNEDIN;

    public PostLoginClient(String serverUrl, client.websocket.NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
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
                case "logout" -> logout();
                case "create game" -> create(params);
                case "list games" -> list();
                case "play game" -> play(params);
                case "observe game" -> observe(params);
                default -> help();
            };
        } catch (exception.ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String observe(String... params) throws exception.ResponseException {
        if (params.length < 2) {
            throw new exception.ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
        }

        ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(params[0]);
        int id = Integer.parseInt(params[1]);
        if (color == null) {
            System.out.println("Invalid game ID provided");
            return null;
        }

        var request = new JoinRequest(color, id);
        var result = server.join(request);
        if (result == null) {
            System.out.println("Error joining game");
            return null;
        }
        this.state = State.INGAME;
        return String.format("Joined game!");
    }

    public String play(String... params) throws exception.ResponseException {
        if (params.length < 1) {
            throw new exception.ResponseException(400, "Expected: <ID>");
        }

        int id = Integer.parseInt(params[0]);

        var request = new JoinRequest(null, id);
        var result = server.join(request);
        if (result == null) {
            System.out.println("Error joining game");
            return null;
        }
        this.state = State.INGAME;
        return String.format("Joined game!");
    }

    public String list() throws exception.ResponseException {
        ListResult result = server.list();
        StringBuilder string = null;
        int numGame = 0;

        for (GameData game : result.games()) {
            string.append(numGame + 1);
            string.append(": ");
            string.append(game.gameName());
            string.append("--");
            if (game.whiteUsername() != null) {
                string.append(game.whiteUsername());
            } else {
                string.append("___");
            }
            string.append(" vs. ");
            if (game.blackUsername() != null) {
                string.append(game.blackUsername());
            } else {
                string.append("____");
            }
        }
        this.state = State.INGAME;
        return string.toString();
    }

    public String create(String... params) throws exception.ResponseException {
        if (params.length < 1) {
            throw new exception.ResponseException(400, "Expected: <NAME>");
        }

        var request = new CreateRequest(params[0]);
        var result = server.create(request);

        if (result != null) {
            this.state = State.INGAME;
            this.name = params[0];
            return String.format("Created game %s.", this.name);
        } else {
            throw new exception.ResponseException(401, "Create game failed");
        }
    }

    public String logout(String... params) throws exception.ResponseException {
        if (server.getAuthToken() == null) {
            return "You must be logged in to perform this action.";
        }
        server.logout();
        this.state = State.SIGNEDOUT;
        server.setAuthToken(null);
        return String.format("Logged out");
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
