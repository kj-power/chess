package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTests {
    private UserService userService;
    private GameService gameService;
    private AuthService authService;
    private AuthAccess authAccess;
    private GameAccess gameAccess;
    private UserAccess userAccess;

    public DAOTests() {
    }

    @BeforeEach
    public void setup() throws DataAccessException, SQLException {
        userAccess = new MySqlUserAccess();
        authAccess = new MySqlAuthAccess();
        gameAccess = new MySqlGameAccess();
        userService = new UserService(userAccess, authAccess);
        gameService = new GameService(gameAccess, authAccess);
        authService = new AuthService(authAccess);
        userService.delete();
        gameService.delete();
        authService.delete();
    }

    @Test
    @DisplayName("createAuth - Positive")
    void createAuth_ValidUser_Success() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        String token = authAccess.createAuth("user1");
        assertNotNull(token);
        AuthData data = authAccess.getAuth(token);
        assertEquals("user1", data.username());
    }

    @Test
    @DisplayName("createAuth - Negative")
    void createAuth_DBError_ThrowsException() throws Exception {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();

        try (var conn = DatabaseManager.getConnection()) {
            conn.createStatement().execute("DROP TABLE IF EXISTS auth");
        }

        assertThrows(DataAccessException.class, () -> {
            authAccess.createAuth("user2");
        });
    }

    @Test
    @DisplayName("getAuth - Positive")
    void getAuth_ValidToken_ReturnsAuthData() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        String token = authAccess.createAuth("user3");
        AuthData data = authAccess.getAuth(token);
        assertEquals("user3", data.username());
    }

    @Test
    @DisplayName("getAuth - Negative")
    void getAuth_InvalidToken_ReturnsNull() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        AuthData data = authAccess.getAuth("invalid-token");
        assertNull(data);
    }

    @Test
    @DisplayName("deleteToken - Positive")
    void deleteToken_ValidToken_Success() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        String token = authAccess.createAuth("user4");
        authAccess.deleteToken(token);
        assertNull(authAccess.getAuth(token));
    }

    @Test
    @DisplayName("deleteToken - Negative")
    void deleteToken_InvalidToken_NoEffect() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        assertDoesNotThrow(() -> authAccess.deleteToken("non-existent-token"));
    }

    @Test
    @DisplayName("isEmpty - Positive")
    void isEmpty_EmptyAuthTable_ReturnsTrue() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        authAccess.clear();
        assertTrue(authAccess.isEmpty());
    }

    @Test
    @DisplayName("isEmpty - Negative")
    void isEmpty_NonEmptyAuthTable_ReturnsFalse() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        authAccess.createAuth("user5");
        assertFalse(authAccess.isEmpty());
    }

    @Test
    @DisplayName("clear - Positive")
    void clear_AuthTable_EmptiesTable() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        authAccess.createAuth("user6");
        authAccess.clear();
        assertTrue(authAccess.isEmpty());
    }
}

