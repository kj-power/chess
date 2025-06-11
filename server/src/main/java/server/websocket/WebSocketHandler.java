package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.protobuf.Service;
import dataaccess.DataAccessException;
import dataaccess.MySqlGameAccess;
import exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.BadRequestException;
import service.TakenException;
import service.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import dataaccess.MySqlAuthAccess;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {
    private static MySqlAuthAccess staticAuthAccess;
    private static MySqlGameAccess staticGameAccess;

    private final ConnectionManager connections = new ConnectionManager();
    private MySqlAuthAccess authAccess;
    private MySqlGameAccess gameAccess;

    public WebSocketHandler() {
        this.authAccess = staticAuthAccess;
        this.gameAccess = staticGameAccess;
    }

    public static void injectDependencies(MySqlAuthAccess authAccess, MySqlGameAccess gameAccess) {
        staticAuthAccess = authAccess;
        staticGameAccess = gameAccess;
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
            connections.broadcast("", new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage()));
        }

    }

    private String getUsername(String authToken) {
        try {
            AuthData data = authAccess.getAuth(authToken);
            return data.username();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        ChessGame.TeamColor color = command.getColor();

        ChessGame game;
        try {
            game = gameAccess.getGame(gameID).game();
            if (game == null) {
                throw new IOException("Error: Game not found.");
            }
        } catch (DataAccessException e) {
            throw new IOException("Error: failed to get game data");
        }

        if (connections.isConnected(authToken, gameID, session)) {
            throw new IOException("Error: User already connected to this game.");
        }

        try {
            gameAccess.joinGame(color, gameID, getUsername(authToken));
        } catch (TakenException | BadRequestException e) {
            throw new IOException(e.getMessage());
        } catch (DataAccessException | SQLException e) {
            throw new IOException("Error: Failed to join game.");
        }

        connections.add(authToken, gameID, session);
        var message = String.format("%s joined the game", getUsername(authToken));
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, notification);
    }
}
