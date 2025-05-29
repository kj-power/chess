package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.*;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.RegisterResult;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlServiceTests {
    private UserService userService;
    private GameService gameService;
    private AuthService authService;
    private AuthAccess authAccess;
    private GameAccess gameAccess;
    private UserAccess userAccess;

    public SqlServiceTests() {
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
    @DisplayName("Register - Positive")
    public void testRegisterPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("user1", "pass", "email@email.com");
        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertEquals("user1", result.username());
        assertEquals("user1", result.username());

        assertNotNull(result.authToken());
        AuthData auth = authAccess.getAuth(result.authToken());
        assertNotNull(auth);
        assertEquals(result.username(), auth.username());
    }

    @Test
    @DisplayName("Register - Negative")
    public void testRegisterNegative() {
        var ref = new Object() {
            RegisterRequest request = new RegisterRequest(null, "pass", "email@email.com");
        };
        Exception exception = assertThrows(BadRequestException.class, () -> {
            userService.register(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

        ref.request = new RegisterRequest("user1", null, "email@email.com");
        exception = assertThrows(BadRequestException.class, () -> {
            userService.register(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

    }

    @Test
    @DisplayName("Login - Positive")
    public void testLoginPositive() throws DataAccessException {
        LoginRequest request = new LoginRequest("user1", "pass1");
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "email@email.com");
        userService.register(regRequest);
        LoginResult result = userService.login(request);

        assertNotNull(result);
        assertEquals("user1", result.username());
        assertEquals("user1", result.username());

        assertNotNull(result.authToken());
        AuthData auth = authAccess.getAuth(result.authToken());
        assertNotNull(auth);
        assertEquals(result.username(), auth.username());
    }

    @Test
    @DisplayName("Login - Negative")
    public void testLoginNegative() {
        var ref = new Object() {
            LoginRequest request = new LoginRequest(null, "pass");
        };
        Exception exception = assertThrows(BadRequestException.class, () -> {
            userService.login(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

        ref.request = new LoginRequest("user1", null);
        exception = assertThrows(BadRequestException.class, () -> {
            userService.login(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

    }

    @Test
    @DisplayName("Clear - Positive")
    public void testClearPositive() throws DataAccessException {
        RegisterRequest regRequest = new RegisterRequest("user1", "pass", "email@email.com");
        RegisterResult regResult = userService.register(regRequest);
        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = gameService.create(createRequest);

        assertFalse(authAccess.isEmpty());
        assertFalse(gameAccess.isEmpty());
        assertFalse(userAccess.isEmpty());

        userService.delete();
        gameService.delete();
        authService.delete();

        assertTrue(authAccess.isEmpty());
        assertTrue(gameAccess.isEmpty());
        assertTrue(userAccess.isEmpty());

    }

    @Test
    @DisplayName("Create - Positive")
    public void testCreatePositive() throws DataAccessException {
        CreateRequest request = new CreateRequest("game1");
        CreateResult result = gameService.create(request);

        assertNotNull(result);
        assertEquals("game1", result.gameName());

        GameData game = gameAccess.getGame(result.gameID());
        assertNotNull(game);
        assertEquals(result.gameID(), game.gameID());
    }

    @Test
    @DisplayName("Create - Negative")
    public void testCreateNegative() {
        var ref = new Object() {
            CreateRequest request = new CreateRequest(null);
        };
        Exception exception = assertThrows(BadRequestException.class, () -> {
            gameService.create(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

    }

    @Test
    @DisplayName("Join - Positive")
    public void testJoinPositive() throws DataAccessException, SQLException {
        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = gameService.create(createRequest);

        GameData game = gameAccess.getGame(createResult.gameID());
        int gameID = game.gameID();

        JoinRequest request = new JoinRequest(ChessGame.TeamColor.WHITE, gameID);
        gameService.join(request, "user1");
        game = gameAccess.getGame(createResult.gameID());

        assertFalse(gameAccess.isEmpty());
        assertEquals("user1", game.whiteUsername());
    }

    @Test
    @DisplayName("Join - Negative")
    public void testJoinNegative() throws DataAccessException, SQLException {
        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = gameService.create(createRequest);

        GameData game = gameAccess.getGame(createResult.gameID());
        int gameID = game.gameID();

        var ref = new Object() {
            JoinRequest request = new JoinRequest(ChessGame.TeamColor.WHITE, gameID);
        };
        gameService.join(ref.request, "user1");

        ref.request = new JoinRequest(ChessGame.TeamColor.WHITE, gameID);

        Exception exception = assertThrows(TakenException.class, () -> {
            gameService.join(ref.request, "user2");
        });

        assertEquals("Error: already taken", exception.getMessage());

    }

    @Test
    @DisplayName("Logout - Positive")
    public void testLogoutPositive() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("user1", "pass1");
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "email@email.com");
        userService.register(regRequest);
        LoginResult loginResult = userService.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        userService.logout(logoutRequest);
        AuthData auth = authAccess.getAuth(loginResult.authToken());

        assertNull(auth);
    }

    @Test
    @DisplayName("Logout - Negative")
    public void testLogoutNegative() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("user1", "pass1");
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "email@email.com");
        userService.register(regRequest);
        LoginResult loginResult = userService.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        userService.logout(logoutRequest);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userService.logout(logoutRequest);
        });

        assertEquals("Error: unauthorized", exception.getMessage());

    }


    @Test
    @DisplayName("List - Positive")
    public void testListPositive() throws DataAccessException {
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "email@email.com");
        RegisterResult regResult = userService.register(regRequest);
        String token = regResult.authToken();

        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = gameService.create(createRequest);

        createRequest = new CreateRequest("game2");
        createResult = gameService.create(createRequest);

        createRequest = new CreateRequest("game3");
        createResult = gameService.create(createRequest);

        ListResult listResult = gameService.list(token);

        assertNotNull(listResult);
        assertNotNull(listResult.games());
        assertEquals(3, listResult.games().size());

        List<String> gameNames = listResult.games().stream()
                .map(GameData::gameName)
                .toList();

        assertTrue(gameNames.contains("game1"));
        assertTrue(gameNames.contains("game2"));
        assertTrue(gameNames.contains("game3"));

    }

    @Test
    @DisplayName("List - Negative")
    public void testListNegative() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = gameService.create(createRequest);

        createRequest = new CreateRequest("game2");
        createResult = gameService.create(createRequest);

        createRequest = new CreateRequest("game3");
        createResult = gameService.create(createRequest);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            gameService.list("badToken");
        });

        assertEquals("Error: unauthorized", exception.getMessage());

    }

}

