package ui.client;

import chess.ChessGame;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import requests.LogoutRequest;
import results.ListResult;
import server.ServerFacade;
import ui.client.websocket.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostLoginClient {
    private final ServerFacade server;
    private String name = null;
    private final String serverUrl;
    private final client.websocket.NotificationHandler notificationHandler;
    private State state = State.SIGNEDIN;

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
//        if (params.length < 1) {
//            throw new exception.ResponseException(400, "Expected: <ID>");
//        }
//
//        int id = Integer.parseInt(params[0]);
//        var listResult = server.list();
//        List<GameData> games = new ArrayList<>(listResult.games());
//
//        if (id < 1 || id > games.size()) {
//            throw new exception.ResponseException(404, "Game index out of range");
//        }
//        System.out.println(games);
//        GameData game = games.get(id - 1);
//        int gameID = game.gameID();
//
//        var request = new JoinRequest(null, gameID);
//        server.join(request);

        BoardMaker.main(ChessGame.TeamColor.WHITE, new ChessGame());
        this.state = State.INGAME;
        return String.format("Joined game!");
    }

    public String play(String... params) throws exception.ResponseException {
        if (params.length < 1) {
            throw new exception.ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
        }

        int id = Integer.parseInt(params[0]);
        ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());

//        var request = new JoinRequest(color, id);
//        server.join(request);

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
            string.append(game.whiteUsername() != null ? game.whiteUsername() : "___");
            string.append(" vs ");
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
