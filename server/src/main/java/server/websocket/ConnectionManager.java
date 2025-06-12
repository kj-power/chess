package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, int gameID, Session session) {
        var connection = new Connection(authToken, gameID, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public boolean isConnected(String authToken, int gameID, Session session) {
        var connection = connections.get(authToken);
        return connection != null && connection.gameID == gameID && connection.session.equals(session);
    }

    public void broadcast(int gameID, String excludeToken, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        Gson gson = new Gson();
        ConcurrentHashMap<String, Connection> gameConns = new ConcurrentHashMap<>();
        for (var c : connections.values()) {
            if (c.gameID == gameID) {
                gameConns.put(c.authToken, c);
            }
        }

        for (var c : gameConns.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeToken)) {
                    c.send(gson.toJson(serverMessage));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    public void oneBroadcast(String authToken, ServerMessage serverMessage) throws IOException {
        System.out.println("Connections contains token? " + connections.containsKey(authToken));
        Gson gson = new Gson();
        Connection c = connections.get(authToken);
        if (c != null && c.session.isOpen()) {
            c.send(gson.toJson(serverMessage));
        } else {
            connections.remove(authToken);
        }
    }
}
