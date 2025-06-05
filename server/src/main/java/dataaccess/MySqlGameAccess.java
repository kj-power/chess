package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.BadRequestException;
import service.TakenException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlGameAccess implements GameAccess{
    public MySqlGameAccess() throws DataAccessException, SQLException {
        db.configureDatabase(createStatements);
    }
    private DatabaseManager db = new DatabaseManager();

    @Override
    public boolean isEmpty() throws DataAccessException {
        String tableName = "game";
        return db.isEmpty(tableName);
    }

    @Override
    public int createGame(String name) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            int gameID = new Random().nextInt(Integer.MAX_VALUE);
            ChessGame game = new ChessGame();
            Gson gson = new Gson();
            String gameJson = gson.toJson(game);

            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                ps.setNull(2, NULL);
                ps.setNull(3, NULL);
                ps.setString(4, name);
                ps.setString(5, gameJson);
                ps.executeUpdate();
                return gameID;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to create game", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var gameJson = rs.getString("game");

                        Gson gson = new Gson();
                        ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error: unable to read data");
        }
    }

    @Override
    public void joinGame(ChessGame.TeamColor color, int gameID, String username) throws DataAccessException, SQLException {

        GameData game = getGame(gameID);
        if (game == null) {
            throw new BadRequestException("Error:bad request");
        }

        if (color == null) {
            throw new BadRequestException("Error: invalid color");
        }

        String updateField = null;


        if (color == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new TakenException("Error: already taken");
            }
            updateField = "whiteUsername";
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null) {
                throw new TakenException("Error: already taken");
            }
            updateField = "blackUsername";
        }
        else {
            throw new BadRequestException("Error: invalid color");
        }

        try (var conn = DatabaseManager.getConnection()) {
            String statement = String.format("UPDATE game SET %s=? WHERE gameID=?", updateField);
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to update game", e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        Gson gson = new Gson();

        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (var ps = conn.prepareStatement(statement);
                 var rs = ps.executeQuery()) {

                while (rs.next()) {
                    int gameID = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String gameJson = rs.getString("game");

                    ChessGame game = gson.fromJson(gameJson, ChessGame.class);

                    games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to list games", e);
        }

        return games;
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE game";
        db.executeUpdate(statement);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              gameID INT PRIMARY KEY,
              whiteUsername VARCHAR(256),
              blackUsername VARCHAR(256),
              gameName VARCHAR(256) NOT NULL,
              game TEXT NOT NULL
            )"""
    };
}
