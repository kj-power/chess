package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.Service;
import com.mysql.cj.callback.MysqlCallback;
import dataaccess.DataAccessException;
import dataaccess.MySqlGameAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.BadRequestException;
import service.GameService;
import service.TakenException;
import service.GameService;
import service.UnauthorizedException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import dataaccess.MySqlAuthAccess;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler() {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            UserGameCommand.CommandType type = UserGameCommand.CommandType.valueOf(json.get("commandType").getAsString());

            switch (type) {
                case LEAVE:
                    UserGameCommand leaveCommand = new Gson().fromJson(json, UserGameCommand.class);
                    leave(leaveCommand, session);
                    break;
                case MAKE_MOVE:
                    MakeMoveCommand moveCommand = new Gson().fromJson(json, MakeMoveCommand.class);
                    makeMove(moveCommand, session);
                    break;
                case CONNECT:
                    UserGameCommand connectCommand = new Gson().fromJson(json, UserGameCommand.class);
                    connect(connectCommand, session);
                    break;
                case RESIGN:
                    UserGameCommand resignCommand = new Gson().fromJson(json, UserGameCommand.class);
                    resign(resignCommand, session);
                    break;
            }
        } catch (UnauthorizedException ex){
           // connections.broadcast("", new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage()));
        } catch (ResponseException | SQLException | DataAccessException | InvalidMoveException e) {
            throw new RuntimeException(e);
        }

    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        error.printStackTrace(); // optional: log error
    }

    private String getUsername(String authToken) {
        try {
            MySqlAuthAccess authAccess = new MySqlAuthAccess();
            AuthData data = authAccess.getAuth(authToken);
            if (data == null) {
                System.out.println("YUHHHH");
                var errorMessage = new ErrorMessage("Error: bad auth token");
                connections.oneBroadcast(authToken, errorMessage);
                return null;
            }
            if (data.username() == null) {
                System.out.println("it's null!!!!");
                var errorMessage = new ErrorMessage("Error: bad auth token");
                connections.oneBroadcast(authToken, errorMessage);
                return null;
            }
            return data.username();
        } catch (DataAccessException | SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private GameData getGameData(int gameID, String authToken) throws IOException {
        GameData game;
        try {
            MySqlGameAccess gameAccess = new MySqlGameAccess();
            game = gameAccess.getGame(gameID);

            if (game == null) {
                var errorMessage = new ErrorMessage("Error: could not find game");
                connections.oneBroadcast(authToken, errorMessage);
            }

        } catch (DataAccessException | SQLException e) {
            var errorMessage = new ErrorMessage("Error: failed to get game data");
            connections.oneBroadcast(authToken, errorMessage);
            throw new IOException("Game not found");
        }
        return game;
    }

    private ChessGame.TeamColor getColor(GameData game, String username) {
        ChessGame.TeamColor color = null;
        if (Objects.equals(username, game.whiteUsername())) {
            color = ChessGame.TeamColor.WHITE;
        } else if (username.equals(game.blackUsername())) {
            color = ChessGame.TeamColor.BLACK;
        }
        return color;
    }

    private void leave(UserGameCommand command, Session session) throws IOException, SQLException, DataAccessException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        connections.add(authToken, gameID, session);
        String username = getUsername(authToken);

        if (username == null) {
            return;
        }

        GameData game = getGameData(gameID, authToken);
        if (game == null) {
            return;
        }

        ChessGame.TeamColor color = getColor(game, username);

        GameData newGame;
        if (color == ChessGame.TeamColor.WHITE) {
            newGame = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
        } else if (color == ChessGame.TeamColor.BLACK){
            newGame = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
        } else {
            newGame = game;
        }

        MySqlGameAccess gameAccess = new MySqlGameAccess();
        gameAccess.updateGame(newGame);

        NotificationMessage notification = new NotificationMessage(String.format("%s has left the game", username));
        connections.broadcast(gameID, authToken, notification);
        connections.remove(authToken);
    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        GameService gameService;
        try {
            MySqlAuthAccess authAccess = new MySqlAuthAccess();
            MySqlGameAccess gameAccess = new MySqlGameAccess();
            gameService = new GameService(gameAccess, authAccess);
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }

        GameData game = getGameData(gameID, authToken);
        if (game == null) {
            return;
        }

        ChessGame chessGame = game.game();

        String username = getUsername(authToken);
        if (username == null) {
            return;
        }

        ChessGame.TeamColor color = getColor(game, username);
        if (color == null) {
            var errorMessage = new ErrorMessage("Error: you are observing");
            connections.oneBroadcast(authToken, errorMessage);
            return;
        }
        String opUsername = color == ChessGame.TeamColor.WHITE ? game.blackUsername() : game.whiteUsername();

        if (game.game().getGameOver()) {
            var errorMessage = new ErrorMessage("Error: game is over");
            connections.oneBroadcast(authToken, errorMessage);
            return;
        }

        chessGame.setGameOver(true);

        gameService.updateGame(authToken, game);
        NotificationMessage notification = new NotificationMessage(String.format("%s has forfeited, %s wins!", username, opUsername));
        connections.broadcast(gameID, authToken, notification);
        connections.oneBroadcast(authToken, notification);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws ResponseException, IOException, DataAccessException, InvalidMoveException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        GameData game = getGameData(gameID, authToken);
        if (game == null) {
            return;
        }

        connections.add(authToken, gameID, session);

        ChessMove chessMove = command.getMove();
        String username = getUsername(authToken);

        if (username == null) {
            return;
        }

        ChessGame chessGame = game.game();
        ChessGame.TeamColor color = getColor(game, username);

        if (chessGame.getGameOver()) {
            var errorMessage = new ErrorMessage("Error: game is already over");
            connections.oneBroadcast(authToken, errorMessage);
            return;
        }

        if (chessMove == null) {
            var errorMessage = new ErrorMessage("Error: enter valid positions to move");
            connections.oneBroadcast(authToken, errorMessage);
            return;
        }

        try {
            if (chessGame.getTeamTurn() != color) {
                var errorMessage = new ErrorMessage("Error: it's not your turn");
                connections.oneBroadcast(authToken, errorMessage);
                return;
            }

            chessGame.makeMove(chessMove);
            Map<String, Connection> gameConnections = connections.getConnectionsForGame(gameID);

            for (Map.Entry<String, Connection> entry : gameConnections.entrySet()) {
                String token = entry.getKey();
                Connection conn = entry.getValue();

                String otherUsername = getUsername(token);
                ChessGame.TeamColor userColor = getColor(game, otherUsername);
                var loadGameMessage = new LoadGameMessage(chessGame, userColor);

                connections.oneBroadcast(token, loadGameMessage);
            }

            MySqlGameAccess gameAccess = new MySqlGameAccess();
            gameAccess.updateGame(game);
            ChessGame.TeamColor opColor = (color == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            String opUsername;
            if (opColor == ChessGame.TeamColor.WHITE) {
                opUsername = game.whiteUsername();
            } else {
                opUsername = game.blackUsername();
            }
            if (chessGame.isInCheckmate(opColor)) {
                var notificationMessage = new NotificationMessage(String.format("%s is in checkmate", opUsername));
                connections.broadcast(gameID, "", notificationMessage);
                chessGame.setGameOver(true);
            } else if (chessGame.isInCheck(opColor)) {
                var notificationMessage = new NotificationMessage(String.format("%s is in check", opUsername));
                connections.broadcast(gameID, "", notificationMessage);
            }

            if (chessGame.isInStalemate(opColor)) {
                var notificationMessage = new NotificationMessage(String.format("%s is in stalemate", opColor));
                connections.broadcast(gameID, "", notificationMessage);
                chessGame.setGameOver(true);
            }

            if (chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE) {
                chessGame.setTeamTurn(ChessGame.TeamColor.BLACK);
            } else {
                chessGame.setTeamTurn(ChessGame.TeamColor.WHITE);
            }

            try {
                String endCords;
                int col = chessMove.getEndPosition().getColumn();
                int row = chessMove.getEndPosition().getRow();
                endCords = convertColBack(col);
                endCords = endCords + String.valueOf(row);
                var message = String.format("%s moved to %s", username, endCords);
                var notification = new NotificationMessage(message);

                connections.broadcast(gameID, authToken, notification);

            } catch (Exception ex) {
                throw new ResponseException(500, ex.getMessage());
            }

        } catch (InvalidMoveException | SQLException e) {
            var errorMessage = new ErrorMessage("Error: invalid move - " + e.getMessage());
            connections.oneBroadcast(authToken, errorMessage);
        }
    }

    private String convertColBack(int colIndex) {
        System.out.println(colIndex);
        if (colIndex < 0 || colIndex > 8) {
            throw new IllegalArgumentException("Error: column index must be between 0 and 7");
        }
        char colLetter = (char) ('a' + colIndex - 1);
        return String.valueOf(colLetter);
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        if (connections.isConnected(authToken, gameID, session)) {
            throw new IOException("Error: User already connected to this game.");
        }

        connections.add(authToken, gameID, session);

        String username = getUsername(authToken);
        if (username == null) {
            return;
        }

        GameData game = getGameData(gameID, authToken);
        if (game == null) {
            return;
        }

        ChessGame.TeamColor color = getColor(game, username);

        ChessGame chessGame = game.game();

        var message = String.format("%s joined the game as %s", username, color);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, authToken, notification);
        var loadGameMessage = new LoadGameMessage(chessGame, color);
        connections.oneBroadcast(authToken, loadGameMessage);
    }
}
