package dataaccess;
import chess.ChessGame;
import model.GameData;
import service.BadRequestException;
import service.TakenException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class MemoryGameAccess implements GameAccess {
    private static final HashMap<Integer, GameData> DATA_HASH_MAP = new HashMap<>();

    public boolean isEmpty() {
        if (DATA_HASH_MAP.isEmpty()) {
            return true;
        }
        return false;
    }

    public int createGame(String name) {
        int gameID;
        do {
            gameID = new Random().nextInt(Integer.MAX_VALUE);
        } while (DATA_HASH_MAP.containsKey(gameID));
        GameData game = new GameData(gameID, null, null, name, new ChessGame());
        DATA_HASH_MAP.put(gameID, game);
        return gameID;
    }

    public GameData getGame(int gameID) {
        return DATA_HASH_MAP.get(gameID);
    }

    public void joinGame(ChessGame.TeamColor color, int gameID, String username) {
        GameData game = getGame(gameID);
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (color == null) {
            throw new BadRequestException("Error: color is required");
        }

        GameData newGame;

        switch (color) {
            case WHITE:
                if (game.whiteUsername() != null) {
                    throw new TakenException("Error: already taken");
                }
                newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
                break;

            case BLACK:
                if (game.blackUsername() != null) {
                    throw new TakenException("Error: already taken");
                }
                newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
                break;

            default:
                throw new BadRequestException("Error: invalid color");
        }

        DATA_HASH_MAP.put(gameID, newGame);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    public Collection<GameData> listGames() {
        return DATA_HASH_MAP.values();
    }

    public void clear() {
        DATA_HASH_MAP.clear();
    }

}
