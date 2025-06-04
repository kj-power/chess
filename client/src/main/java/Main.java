import chess.*;
import server.ServerFacade;
import ui.client.BoardMaker;
import ui.client.Repl;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ");
        var serverUrl = "http://localhost:8080";
        new Repl(serverUrl).run();
    }
}