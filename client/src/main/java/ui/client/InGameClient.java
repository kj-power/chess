package ui.client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
    private ChessGame chessGame;
    private ChessGame.TeamColor color;
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

    public void updateGame(ChessGame updatedGame, ChessGame.TeamColor updatedColor) {
        this.chessGame = updatedGame;
        this.color = updatedColor;
    }

    public String eval(String input) throws ResponseException {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (exception.ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String highlight(String... params) throws ResponseException {
        int col;
        int row;

        if (params.length < 2) {
            throw new exception.ResponseException(400, "Expected: <PIECE COL> <PIECE ROW>");
        }

        try {
            col = convertCol(params[0]) + 1;
            row = Integer.parseInt(params[1]);
        }
        catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: rows must be numbers");
        }

        ChessPosition pos = new ChessPosition(row, col);
        BoardMaker.main(this.color, this.chessGame, pos);
        return String.format("Possible moves highlighted");
    }

    public String move(String... params) throws ResponseException {
        int startCol;
        int endCol;
        int startRow;
        int endRow;

        if (params.length < 4) {
            throw new exception.ResponseException(400, "Expected: <INITIAL COL> <INITIAL ROW> <FINAL COL> <FINAL ROW>");
        }

        try {
            startCol = convertCol(params[0]) + 1;
            startRow = Integer.parseInt(params[1]);
            endCol = convertCol(params[2]) + 1;
            endRow = Integer.parseInt(params[3]);
        }
        catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: rows must be numbers");
        }
        catch (IllegalArgumentException e) {
            throw new ResponseException(500, e.getMessage());
        }

        ChessPosition startPos = new ChessPosition(startRow, startCol);
        ChessPosition endPos = new ChessPosition(endRow, endCol);
        ChessMove move = new ChessMove(startPos, endPos, null);

        var moves = this.chessGame.validMoves(startPos);
        if (moves == null) {
            throw new ResponseException(500, "Error: move is not valid");
        }
        if (!moves.contains(move)) {
            throw new ResponseException(500, "Error: move is not valid");
        }

        ChessMove chessMove = new ChessMove(startPos, endPos, null);

        ws = new WebSocketFacade(serverUrl, notificationHandler);
        ws.move(server.getAuthToken(), this.gameID, chessMove);

        return String.format("Making move from %s%s to %s%s", params[0], params[1], params[2], params[3]);
    }

    private int convertCol(String col) {
        if (col == null || col.length() != 1) {
            throw new IllegalArgumentException("Error: column must be a single letter a-h");
        }
        char letter = Character.toLowerCase(col.charAt(0));
        if (letter < 'a' || letter > 'h') {
            throw new IllegalArgumentException("Error: column must be between a and h");
        }
        return letter - 'a';
    }

    public String resign() {
        String token = server.getAuthToken();
        System.out.println("Are you sure you want to forfeit? (y/n)");
        String[] confirmation = getUserInput();
        if (confirmation.length == 1 && confirmation[0].equals("y")) {
            try {
                ws = new WebSocketFacade(serverUrl, notificationHandler);
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
        System.out.printf("\n >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    public String leave() throws ResponseException {
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        ws.leave(server.getAuthToken(), this.gameID, this.color);
        return String.format("Leaving game");
    }

    public String redraw() {
        BoardMaker.main(color, chessGame, null);
        return String.format("Current board");
    }



    public String help() {
        return """
                - redraw - to redraw the board
                - leave - to leave the game
                - move <INITIAL ROW> <INITIAL COLUMN> <FINAL ROW> <FINAL COLUMN> - to move a piece
                - resign - to forfeit the game
                - highlight - <PIECE COLUMN> <PIECE ROW> - to highlight all legal moves for a piece
                - help
                """;
    }
}
