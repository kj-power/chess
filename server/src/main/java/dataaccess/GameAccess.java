package dataaccess;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class GameAccess {
    private static final HashMap<String, GameData> data = new HashMap<>();

    public static int createGame(String name) {
        Random random = new Random();
        int gameID = random.nextInt(100);
        GameData game = new GameData(gameID, null, null, name, new ChessGame());
        data.put(name, game);
        return gameID;
    }

    public static GameData getGame(int gameName) {
        return data.get(gameName);
    }

    public static Collection<GameData> listGames() {
        return data.values();
    }

    public static void clear() {
        data.clear();
    }

}
