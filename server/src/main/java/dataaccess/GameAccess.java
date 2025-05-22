package dataaccess;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import service.BadRequestException;
import service.TakenException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class GameAccess {
    private static final HashMap<Integer, GameData> data = new HashMap<>();

    public static int createGame(String name) {
        int gameID;
        do {
            gameID = new Random().nextInt(Integer.MAX_VALUE);
        } while (data.containsKey(gameID));
        GameData game = new GameData(gameID, null, null, name, new ChessGame());
        data.put(gameID, game);
        return gameID;
    }

    public static GameData getGame(int gameID) {
        return data.get(gameID);
    }

    public static void joinGame(ChessGame.TeamColor color, int gameID, String username) {
        GameData game = getGame(gameID);
        if (game == null) {
            throw new BadRequestException("Error:bad request");
        }
        GameData newGame = null;
        if (color == WHITE) {
            if (game.whiteUsername() != null) {
                throw new TakenException("Error: already taken");
            }
            newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
        }
        else if (color == BLACK) {
            System.out.println("Test: created gameID = " + newGame);
            if (game.blackUsername() != null) {
                throw new TakenException("Error: already taken");
            }
            newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
        }
        else {
            throw new BadRequestException("Error: bad request");
        }
        data.put(gameID, newGame);
    }

    public static Collection<GameData> listGames() {
        return data.values();
    }

    public static void clear() {
        data.clear();
    }

}
