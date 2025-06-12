package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.BadRequestException;
import service.TakenException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Random;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public interface GameAccess {
    boolean isEmpty() throws DataAccessException;

    int createGame(String name) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void joinGame(ChessGame.TeamColor color, int gameID, String username) throws DataAccessException, SQLException;

    void updateGame(GameData game) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void clear() throws DataAccessException;
}
