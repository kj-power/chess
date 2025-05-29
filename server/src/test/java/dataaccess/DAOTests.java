package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.*;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTests {
    private UserService userService;
    private GameService gameService;
    private AuthService authService;
    private AuthAccess authAccess;
    private GameAccess gameAccess;
    private UserAccess userAccess;

    {
        try {
            gameAccess = new MySqlGameAccess();
            userAccess = new MySqlUserAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final ServiceTests serviceTests;

    public DAOTests() {
        serviceTests = new ServiceTests();
    }

    @BeforeEach
    public void setup() throws DataAccessException, SQLException {
        serviceTests.setup();
    }

    @Test
    @DisplayName("createAuth - Positive")
    void createAuthSuccess() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        String token = authAccess.createAuth("user1");
        assertNotNull(token);
        AuthData data = authAccess.getAuth(token);
        assertEquals("user1", data.username());
    }

    @Test
    @DisplayName("createAuth - Negative")
    void createAuthFail() throws Exception {
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
    void getAuthSuccess() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        String token = authAccess.createAuth("user3");
        AuthData data = authAccess.getAuth(token);
        assertEquals("user3", data.username());
    }

    @Test
    @DisplayName("getAuth - Negative")
    void getAuthFail() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        AuthData data = authAccess.getAuth("invalid-token");
        assertNull(data);
    }

    @Test
    @DisplayName("deleteToken - Positive")
    void deleteTokenSuccess() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        String token = authAccess.createAuth("user4");
        authAccess.deleteToken(token);
        assertNull(authAccess.getAuth(token));
    }

    @Test
    @DisplayName("deleteToken - Negative")
    void deleteTokenFail() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        assertDoesNotThrow(() -> authAccess.deleteToken("non-existent-token"));
    }

    @Test
    @DisplayName("isEmpty - Positive")
    void isEmptySuccess() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        authAccess.clear();
        assertTrue(authAccess.isEmpty());
    }

    @Test
    @DisplayName("isEmpty - Negative")
    void isEmptyFail() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        authAccess.createAuth("user5");
        assertFalse(authAccess.isEmpty());
    }

    @Test
    @DisplayName("clear - Positive")
    void clearSuccess() throws DataAccessException, SQLException {
        MySqlAuthAccess authAccess = new MySqlAuthAccess();
        authAccess.createAuth("user6");
        authAccess.clear();
        assertTrue(authAccess.isEmpty());
    }

    @Test
    @DisplayName("createGame - Positive")
    void createGameSuccess() throws DataAccessException {
        int gameID = gameAccess.createGame("Test Game");
        assertTrue(gameID > 0);

        GameData game = gameAccess.getGame(gameID);
        assertNotNull(game);
        assertEquals("Test Game", game.gameName());
    }

    @Test
    @DisplayName("createGame - Negative - DB Error")
    void createGamFail() throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            conn.createStatement().execute("DROP TABLE IF EXISTS game");
        }

        assertThrows(DataAccessException.class, () -> {
            gameAccess.createGame("Fail Game");
        });
    }

    @Test
    @DisplayName("getGame - Positive")
    void getGameSuccess() throws DataAccessException {
        int gameID = gameAccess.createGame("Sample Game");
        GameData game = gameAccess.getGame(gameID);
        assertNotNull(game);
        assertEquals("Sample Game", game.gameName());
    }

    @Test
    @DisplayName("getGame - Negative")
    void getGameFail() throws DataAccessException {
        GameData game = gameAccess.getGame(-1); // assuming invalid ID
        assertNull(game);
    }

    @Test
    @DisplayName("joinGame - Positive")
    void joinGameSuccess() throws DataAccessException, SQLException {
        int gameID = gameAccess.createGame("Joinable Game");

        gameAccess.joinGame(ChessGame.TeamColor.WHITE, gameID, "user1");
        GameData game = gameAccess.getGame(gameID);
        assertEquals("user1", game.whiteUsername());

        gameAccess.joinGame(ChessGame.TeamColor.BLACK, gameID, "user2");
        game = gameAccess.getGame(gameID);
        assertEquals("user2", game.blackUsername());
    }

    @Test
    @DisplayName("joinGame - Negative - Game Does Not Exist")
    void joinGameFail() {
        assertThrows(BadRequestException.class, () -> {
            gameAccess.joinGame(ChessGame.TeamColor.WHITE, -1, "user");
        });
    }

    @Test
    @DisplayName("listGames - Positive")
    void listGamesSuccess() throws DataAccessException {
        gameAccess.createGame("Game 1");
        gameAccess.createGame("Game 2");

        Collection<GameData> games = gameAccess.listGames();

        assertNotNull(games);
        assertTrue(games.size() >= 2);

        boolean foundGame1 = games.stream().anyMatch(g -> "Game 1".equals(g.gameName()));
        boolean foundGame2 = games.stream().anyMatch(g -> "Game 2".equals(g.gameName()));

        assertTrue(foundGame1);
        assertTrue(foundGame2);
    }

    @Test
    @DisplayName("listGames - Negative - DB Error")
    void listGamesFail() throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            conn.createStatement().execute("DROP TABLE IF EXISTS game");
        }

        assertThrows(DataAccessException.class, () -> {
            gameAccess.listGames();
        });
    }

    @Test
    @DisplayName("createUser - Positive")
    void createUserSuccess() throws DataAccessException {
        userAccess.createUser("testuser", "password123", "test@example.com");

        UserData user = userAccess.getUser("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.username());
        assertEquals("password123", user.password());
        assertEquals("test@example.com", user.email());
    }

    @Test
    @DisplayName("createUser - Negative - Duplicate User")
    void createUserFail() throws DataAccessException {
        userAccess.createUser("duplicateUser", "pass", "dup@example.com");

        assertThrows(DataAccessException.class, () -> {
            userAccess.createUser("duplicateUser", "pass2", "dup2@example.com");
        });
    }

    @Test
    @DisplayName("getUser - Positive")
    void getUserSuccess() throws DataAccessException {
        userAccess.createUser("findMe", "mypassword", "findme@example.com");

        UserData user = userAccess.getUser("findMe");
        assertNotNull(user);
        assertEquals("findMe", user.username());
        assertEquals("mypassword", user.password());
        assertEquals("findme@example.com", user.email());
    }

    @Test
    @DisplayName("getUser - Negative - User Not Found")
    void getUserFail() throws DataAccessException {
        UserData user = userAccess.getUser("nonexistentUser");
        assertNull(user);
    }

}

