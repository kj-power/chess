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

    /*GameData getGame(int gameID) {
        return(data.get(gameID))
    }

    Collection<GameData> listGames() {
        return data;
    }*/

    public static void clear() {
        data.clear();
    }

}
