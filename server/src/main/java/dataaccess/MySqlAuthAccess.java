package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlAuthAccess implements AuthAccess {
    private DatabaseManager db = new DatabaseManager();

    public MySqlAuthAccess() throws DataAccessException, SQLException {
        db.configureDatabase(createStatements);
    }

    private static final HashMap<String, AuthData> DATA_HASH_MAP = new HashMap<>();

    public boolean isEmpty() throws DataAccessException {
        String tableName = "auth";
        return db.isEmpty(tableName);
    }

    public String createAuth(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auth (username, authToken) VALUES(?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                String token = UUID.randomUUID().toString();
                ps.setString(1, username);
                ps.setString(2, token);
                ps.executeUpdate();
                return token;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to read data");
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var username = rs.getString("username");
                        return new AuthData(authToken, username);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Unable to read data");
        }
    }


    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE auth";
        db.executeUpdate(statement);
    }

    public void deleteToken(String token) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        db.executeUpdate(statement, token);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              username varchar(256) NOT NULL,
              authToken varchar(256) NOT NULL,
              PRIMARY KEY (authToken)
            )"""
    };

}
