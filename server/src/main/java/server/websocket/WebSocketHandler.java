package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
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
import service.TakenException;
import service.UnauthorizedException;
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

            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                //case MAKE_MOVE ->
                //case LEAVE ->
                //case RESIGN ->
            }
        } catch (UnauthorizedException ex){
           // connections.broadcast("", new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage()));
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
            return data.username();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = getUsername(authToken);

        if (connections.isConnected(authToken, gameID, session)) {
            throw new IOException("Error: User already connected to this game.");
        }

        connections.add(authToken, gameID, session);

        GameData game;

        try {
            MySqlGameAccess gameAccess = new MySqlGameAccess();
            game = gameAccess.getGame(gameID);

            if (game == null) {
                var errorMessage = new ErrorMessage("Error: could not find game");
                System.out.println("Sending error message to authToken: " + authToken);
                connections.oneBroadcast(authToken, errorMessage);
                return;
            }

        } catch (DataAccessException | SQLException e) {
            var errorMessage = new ErrorMessage("Error: failed to get game data");
            connections.oneBroadcast(authToken, errorMessage);
            throw new IOException("Game not found");
        }

        ChessGame.TeamColor color = null;

        if (username == game.whiteUsername()) {
            color = ChessGame.TeamColor.WHITE;
        } else if (username == game.blackUsername()) {
            color = ChessGame.TeamColor.BLACK;
        }

        ChessGame chessGame = game.game();

        var message = String.format("%s joined the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(authToken, notification);
        var loadGameMessage = new LoadGameMessage(chessGame, color);
        connections.oneBroadcast(authToken, loadGameMessage);
    }
}
