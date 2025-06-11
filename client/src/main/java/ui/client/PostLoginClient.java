package ui.client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import requests.LogoutRequest;
import results.ListResult;
import server.ServerFacade;
import ui.client.websocket.NotificationHandler;
import ui.client.websocket.State;
import ui.client.websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostLoginClient {
    private final ServerFacade server;
    private String name = null;
    private final String serverUrl;
    private State state = State.SIGNEDIN;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;

    public PostLoginClient(ServerFacade server, String serverUrl, NotificationHandler notificationHandler) {
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
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "play" -> play(params);
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (exception.ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String observe(String... params) throws exception.ResponseException {
        int id = 0;
        try {
            id = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            throw new ResponseException(400, "must be a number");
        }
        var result = server.list();
        List<GameData> games = new ArrayList<>(result.games());
        if (id <= games.size()) {
            BoardMaker.main(ChessGame.TeamColor.WHITE, new ChessGame());
        }
        else {
            throw new ResponseException(400, "Not in list");
        }
        this.state = State.INGAME;
        return String.format("Joined game!");
    }

    public String play(String... params) throws exception.ResponseException {
        String token = server.getAuthToken();

        if (params.length < 1) {
            throw new exception.ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
        }

        int id = Integer.parseInt(params[0]);
        try {
            id = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new exception.ResponseException(400, "Game ID must be a number.");
        }
        ChessGame.TeamColor color = null;
        try {
             color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new exception.ResponseException(400, "Invalid color. Expected WHITE or BLACK.");
        }
        var result = server.list();
        List<GameData> games = new ArrayList<>(result.games());

        if (id < 1 || id > games.size()) {
            throw new exception.ResponseException(404, "Game index out of range");
        }

        GameData game = games.get(id - 1);
        int gameID = game.gameID();

        JoinRequest req = new JoinRequest(color, gameID);
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        ws.connect(token, gameID, color);
        boolean success = server.join(req);
        if (!success) {
            System.out.println("Join failed. Please check the game ID and color, or try again.");
        }

        this.state = State.INGAME;
        BoardMaker.main(color, new ChessGame());
        return String.format("Joined game!");
    }

    public String list() throws exception.ResponseException {
        ListResult result = server.list();
        StringBuilder string = new StringBuilder();
        int numGame = 1;

        for (GameData game : result.games()) {
            string.append(numGame++).append(": ");
            string.append(game.gameName()).append(" -- ");
            string.append("WHITE: ");
            string.append(game.whiteUsername() != null ? game.whiteUsername() : "___");
            string.append(" vs ");
            string.append("BLACK: ");
            string.append(game.blackUsername() != null ? game.blackUsername() : "___");
            string.append("\n");
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

    public String logout() throws exception.ResponseException {
        String token = server.getAuthToken();
        if (token == null) {
            return "You must be logged in to perform this action.";
        }
        LogoutRequest req = new LogoutRequest(token);
        server.logout();
        this.state = State.SIGNEDOUT;
        server.setAuthToken(null);
        return String.format("Logged out");
    }

    public String help() {
        return """
                - create <NAME>
                - list
                - play <ID> [WHITE|BLACK]
                - observe <ID>
                - logout
                - quit
                - help
                """;
    }


}
