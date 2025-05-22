package service;

import chess.ChessGame;
import dataaccess.AuthAccess;
import dataaccess.GameAccess;
import dataaccess.UserAccess;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    @BeforeEach
    public void setup() {
        UserService.delete();
        GameService.delete();
        AuthService.delete();
    }

    @Test
    @DisplayName("Register - Positive")
    public void testRegisterPositive() {
        RegisterRequest request = new RegisterRequest("user1", "pass", "email@email.com");
        RegisterResult result = UserService.register(request);

        assertNotNull(result);
        assertEquals("user1", result.username());
        assertEquals("user1", result.username());

        assertNotNull(result.authToken());
        AuthData auth = AuthAccess.getAuth(result.authToken());
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
            UserService.register(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

        ref.request = new RegisterRequest("user1", null, "email@email.com");
        exception = assertThrows(BadRequestException.class, () -> {
            UserService.register(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

    }

    @Test
    @DisplayName("Login - Positive")
    public void testLoginPositive() {
        LoginRequest request = new LoginRequest("user1", "pass1");
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "email@email.com");
        UserService.register(regRequest);
        LoginResult result = UserService.login(request);

        assertNotNull(result);
        assertEquals("user1", result.username());
        assertEquals("user1", result.username());

        assertNotNull(result.authToken());
        AuthData auth = AuthAccess.getAuth(result.authToken());
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
            UserService.login(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

        ref.request = new LoginRequest("user1", null);
        exception = assertThrows(BadRequestException.class, () -> {
            UserService.login(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

    }

    @Test
    @DisplayName("Clear - Positive")
    public void testClearPositive() {
        RegisterRequest regRequest = new RegisterRequest("user1", "pass", "email@email.com");
        RegisterResult regResult = UserService.register(regRequest);
        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = GameService.create(createRequest);

        assertFalse(AuthAccess.isEmpty());
        assertFalse(GameAccess.isEmpty());
        assertFalse(UserAccess.isEmpty());

        UserService.delete();
        GameService.delete();
        AuthService.delete();

        assertTrue(AuthAccess.isEmpty());
        assertTrue(GameAccess.isEmpty());
        assertTrue(UserAccess.isEmpty());

    }

    @Test
    @DisplayName("Create - Positive")
    public void testCreatePositive() {
        CreateRequest request = new CreateRequest("game1");
        CreateResult result = GameService.create(request);

        assertNotNull(result);
        assertEquals("game1", result.gameName());

        GameData game = GameAccess.getGame(result.gameID());
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
            GameService.create(ref.request);
        });

        assertEquals("Error: bad request", exception.getMessage());

    }

    @Test
    @DisplayName("Join - Positive")
    public void testJoinPositive() {
        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = GameService.create(createRequest);

        GameData game = GameAccess.getGame(createResult.gameID());
        int gameID = game.gameID();

        JoinRequest request = new JoinRequest(ChessGame.TeamColor.WHITE, gameID);
        GameService.join(request, "user1");
        game = GameAccess.getGame(createResult.gameID());

        assertFalse(GameAccess.isEmpty());
        assertEquals("user1", game.whiteUsername());
    }

    @Test
    @DisplayName("Join - Negative")
    public void testJoinNegative() {
        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = GameService.create(createRequest);

        GameData game = GameAccess.getGame(createResult.gameID());
        int gameID = game.gameID();

        var ref = new Object() {
            JoinRequest request = new JoinRequest(ChessGame.TeamColor.WHITE, gameID);
        };
        GameService.join(ref.request, "user1");

        ref.request = new JoinRequest(ChessGame.TeamColor.WHITE, gameID);

        Exception exception = assertThrows(TakenException.class, () -> {
            GameService.join(ref.request, "user2");
        });

        assertEquals("Error: already taken", exception.getMessage());

    }

    @Test
    @DisplayName("Logout - Positive")
    public void testLogoutPositive() {
        LoginRequest loginRequest = new LoginRequest("user1", "pass1");
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "email@email.com");
        UserService.register(regRequest);
        LoginResult loginResult = UserService.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        UserService.logout(logoutRequest);
        AuthData auth = AuthAccess.getAuth(loginResult.authToken());

        assertNull(auth);
    }

    @Test
    @DisplayName("Logout - Negative")
    public void testLogoutNegative() {
        LoginRequest loginRequest = new LoginRequest("user1", "pass1");
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "email@email.com");
        UserService.register(regRequest);
        LoginResult loginResult = UserService.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        UserService.logout(logoutRequest);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            UserService.logout(logoutRequest);
        });

        assertEquals("Error: unauthorized", exception.getMessage());

    }


    @Test
    @DisplayName("List - Positive")
    public void testListPositive() {
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "email@email.com");
        RegisterResult regResult = UserService.register(regRequest);
        String token = regResult.authToken();

        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = GameService.create(createRequest);

        createRequest = new CreateRequest("game2");
        createResult = GameService.create(createRequest);

        createRequest = new CreateRequest("game3");
        createResult = GameService.create(createRequest);

        ListResult listResult = GameService.list(token);

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
    public void testListNegative() {
        CreateRequest createRequest = new CreateRequest("game1");
        CreateResult createResult = GameService.create(createRequest);

        createRequest = new CreateRequest("game2");
        createResult = GameService.create(createRequest);

        createRequest = new CreateRequest("game3");
        createResult = GameService.create(createRequest);

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            GameService.list("badToken");
        });

        assertEquals("Error: unauthorized", exception.getMessage());

    }

}
