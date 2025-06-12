package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
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
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler() {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthToken());

            MakeMoveCommand moveCommand = null;

            if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
            }

            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                case MAKE_MOVE -> makeMove(moveCommand, session);
                //case LEAVE ->
                case RESIGN -> resign(command, session);
            }
        } catch (UnauthorizedException ex){
           // connections.broadcast("", new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage()));
        } catch (ResponseException e) {
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
                var errorMessage = new ErrorMessage("Error: bad auth token");
                connections.oneBroadcast(authToken, errorMessage);
                return null;
            }
            if (data.username() == null) {
                var errorMessage = new ErrorMessage("Error: bad auth token");
                connections.oneBroadcast(authToken, errorMessage);
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
        if (username == game.whiteUsername()) {
            color = ChessGame.TeamColor.WHITE;
        } else if (username == game.blackUsername()) {
            color = ChessGame.TeamColor.BLACK;
        }
        return color;
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
        connections.broadcast(authToken, notification);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws ResponseException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        ChessMove chessMove = command.getMove();
        String username = getUsername(authToken);

        try {
            var message = String.format("%s moves to %s", username, chessMove.getEndPosition());
            var notification = new NotificationMessage(message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
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

        var message = String.format("%s joined the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(authToken, notification);
        var loadGameMessage = new LoadGameMessage(chessGame, color);
        connections.oneBroadcast(authToken, loadGameMessage);
    }
}
