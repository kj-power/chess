package dataaccess;

import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserAccess implements UserAccess{

    private DatabaseManager db = new DatabaseManager();
    public MySqlUserAccess() throws DataAccessException, SQLException {
    }

    public boolean isEmpty() throws DataAccessException {
        String tableName = "user";
        return db.isEmpty(tableName);
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO user (username, password, email) VALUES(?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to read data");
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT email, password FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var email = rs.getString("email");
                        var password = rs.getString("password");
                        return new UserData(username, password, email);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error: unable to read data");
        }
    }



    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE user";
        db.executeUpdate(statement);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              username varchar(256) NOT NULL,
              password varchar(256) NOT NULL,
              email varchar(256) NOT NULL,
              PRIMARY KEY (username)
            )"""
    };

}
