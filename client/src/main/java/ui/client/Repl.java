package ui.client;

import client.websocket.NotificationHandler;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preClient;
    private final PostLoginClient postClient;

    public Repl(String serverUrl) {
        ServerFacade sharedServer = new ServerFacade(serverUrl);
        preClient = new PreLoginClient(sharedServer, serverUrl);
        postClient = new PostLoginClient(sharedServer, serverUrl);
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

}
