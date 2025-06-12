package ui.client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import requests.CreateRequest;
import requests.LogoutRequest;
import server.ServerFacade;
import ui.client.websocket.NotificationHandler;
import ui.client.websocket.State;
import ui.client.websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Scanner;

public class InGameClient {
    private final ServerFacade server;
    private String name = null;
    private final String serverUrl;
    private State state = State.SIGNEDIN;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private final ChessGame chessGame;
    private final ChessGame.TeamColor color;
    private final GameData gameData;
    private final int gameID;

    public InGameClient(ServerFacade server, String serverUrl, NotificationHandler notificationHandler, GameData gameData, ChessGame.TeamColor color) {
        this.server = server;
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        this.color = color;
        this.gameData = gameData;
        this.chessGame = gameData.game();
        this.gameID = gameData.gameID();
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "help" -> help();
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "move" -> move();
            case "resign" -> resign();
            case "highlight" -> highlight();
            default -> help();
        };
    }

    public String highlight() {
        return String.format("Possible moves highlighted");
    }

    public String move() {
        return String.format("Move made");
    }

    public String resign() {
        String token = server.getAuthToken();
        System.out.println("Are you sure you want to forfeit? (y/n)");
        String[] confirmation = getUserInput();
        if (confirmation.length == 1 && confirmation[0].equals("y")) {
            try {
                ws.resign(token, this.gameID);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
            return String.format("Resigned");
        }
        else {
            return String.format("Resignation canceled");
        }
    }

    private String[] getUserInput() {
        System.out.printf("\n[%s] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    public String leave() {
        return String.format("Left game");
    }

    public String redraw() {
        BoardMaker.main(color, chessGame);
        return String.format("Board drawn");
    }



    public String help() {
        return """
                - redraw - to redraw the board
                - leave - to leave the game
                - move <INITIAL ROW> <INITIAL COLUMN> <FINAL ROW> <FINAL COLUMN> - to move a piece
                - resign - to forfeit the game
                - highlight - <PIECE ROW> <PIECE COLUMN> - to highlight all legal moves for a piece
                - help
                """;
    }
}
