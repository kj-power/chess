package ui.client;

import com.sun.nio.sctp.NotificationHandler;
import server.ServerFacade;

import javax.swing.plaf.nimbus.State;

public class PreLoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

    public PetClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public PreLoginClient(ServerFacade server, String serverUrl, NotificationHandler notificationHandler) {
        this.server = server;
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }
}
