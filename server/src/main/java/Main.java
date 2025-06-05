import chess.*;
import dataaccess.*;
import server.Server;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Spark;

import java.sql.SQLException;


public class Main {

    public static void main(String[] args) throws SQLException, DataAccessException {
        UserAccess userAccess = new MySqlUserAccess();
        AuthAccess authAccess = new MySqlAuthAccess();
        GameAccess gameAccess = new MySqlGameAccess();
        var userService = new UserService(userAccess, authAccess);
        var authService = new AuthService(authAccess);
        var gameService = new GameService(gameAccess, authAccess);
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        DatabaseManager.createDatabase();
        new Server().run(8080);

    }
}

