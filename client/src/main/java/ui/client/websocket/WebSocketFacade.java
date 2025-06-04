package ui.client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import webSocketMessages.Action;

import javax.management.Notification;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    client.websocket.NotificationHandler notificationHandler;


    public WebSocketFacade(String url, client.websocket.NotificationHandler notificationHandler) throws exception.ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    webSocketMessages.Notification notification = new Gson().fromJson(message, webSocketMessages.Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new exception.ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void login(String username) throws exception.ResponseException {
        try {
            var action = new Action(Action.Type.LOGIN, username);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new exception.ResponseException(500, ex.getMessage());
        }
    }

    public void logout(String username) throws exception.ResponseException {
        try {
            var action = new Action(Action.Type.LOGOUT, username);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new exception.ResponseException(500, ex.getMessage());
        }
    }
}
