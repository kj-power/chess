package ui.client.websocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage message) throws Exception;
}