import chess.*;
import dataaccess.*;
import server.Server;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Spark;


public class Main {

    public static void main(String[] args) {
        UserAccess userAccess = new MemoryUserAccess();
        AuthAccess authAccess = new MemoryAuthAccess();
        GameAccess gameAccess = new MemoryGameAccess();
        var userService = new UserService(userAccess, authAccess);
        var authService = new AuthService(authAccess);
        var gameService = new GameService(gameAccess, authAccess);

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        new Server().run(8080);

    }
}

