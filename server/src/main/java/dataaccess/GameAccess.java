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

public class GameAccess {
    private static final HashMap<Integer, GameData> data = new HashMap<>();

    public static int createGame(String name) {
        Random random = new Random();
        int gameID = random.nextInt(100);
        GameData game = new GameData(gameID, null, null, name, new ChessGame());
        data.put(gameID, game);
        return gameID;
    }

    public static GameData getGame(int gameID) {
        return data.get(gameID);
    }

    public static int joinGame(String color, int gameID, String username) {
        GameData game = getGame(gameID);
        if (game == null) {
            throw new BadRequestException("Error:bad request");
        }
        if (Objects.equals(color, "WHITE")) {
            if (game.whiteUsername() != null) {
                throw new TakenException("Error: already taken");
            }
            game = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
        }
        if (Objects.equals(color, "BLACK")) {
            if (game.blackUsername() != null) {
                throw new TakenException("Error: already taken");
            }
            game = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
        }
        data.put(game.gameID(), game);
        return game.gameID();
    }

    public static Collection<GameData> listGames() {
        return data.values();
    }

    public static void clear() {
        data.clear();
    }

}
