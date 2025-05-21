package dataaccess;
import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.Random;

public class GameAccess {
    private Collection<GameData> data;

    void createGame(String name) {
        Random random = new Random();
        int gameID = random.nextInt(100);
        data.add(new GameData(gameID, null, null, name, new ChessGame()));
    }

    GameData getGame(int gameID) {
        for (GameData indGame : data) {
            if (indGame.gameID() == gameID) {
                return indGame;
            }
        }
        return null;
    }

    Collection<GameData> listGames() {
        return data;
    }

}
