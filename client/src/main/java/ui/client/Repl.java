package ui.client;

import chess.ChessGame;
import model.GameData;
import server.ServerFacade;
import ui.client.websocket.NotificationHandler;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final PreLoginClient preClient;
    private final PostLoginClient postClient;
    private final ServerFacade sharedServer;
    private final String serverUrl;
    private ChessGame.TeamColor color;

    public Repl(String serverUrl) {
        sharedServer = new ServerFacade(serverUrl);
        preClient = new PreLoginClient(sharedServer, serverUrl, this);
        postClient = new PostLoginClient(sharedServer, serverUrl, this);
        this.serverUrl = serverUrl;
        color = null;
    }

    String whichClient = "pre";

    public void run() {
        System.out.println(WHITE_KING + "Welcome to Chess. Type help to get started");
        System.out.print(preClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (whichClient.equals("pre")) {
                    result = preClient.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    if (result.startsWith("Logged in as") || result.startsWith("Registered as")) {
                        whichClient = "post";
                        System.out.print("\n You're now signed in. Type 'help' for post-login commands.");
                    }
                } else if (whichClient.equals("post")){
                    result = postClient.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    if (result.startsWith("Logged out")) {
                        whichClient = "pre";
                        System.out.print("\n You're now signed out. Type 'help' for pre-login commands.");
                    }
                    if (result.startsWith("Joined")) {
                        whichClient = "game";
                        System.out.print("\n You're now in-game. Type 'help' for in-game commands.");
                        if (result.contains("black")) {
                            color = ChessGame.TeamColor.BLACK;
                        } else {
                            color = ChessGame.TeamColor.WHITE;
                        }
                    }
                } else if (whichClient.equals("game")) {
                    var listResult = sharedServer.list();
                    List<GameData> games = new ArrayList<>(listResult.games());
                    GameData joinGame = games.get(((int) result.charAt(4)));
                    InGameClient gameClient = new InGameClient(sharedServer, serverUrl, this, joinGame, color);
                    result = gameClient.eval(line);
                    if (result.startsWith("Left")) {
                        whichClient = "post";
                        System.out.print("\n You're now out of the game. Type 'help' for post-login commands.");
                    }
                }

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }

        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage serverMessage) {
//        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
//
//        }
//        else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
//            System.out.println(SET_TEXT_COLOR_RED + notific.getMessage());
//        }
//
//        printPrompt();
    }
}
