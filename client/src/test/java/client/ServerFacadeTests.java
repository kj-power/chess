package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import requests.*;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.RegisterResult;
import server.Server;
import server.ServerFacade;
import service.BadRequestException;
import service.TakenException;
import service.UnauthorizedException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void before() throws exception.ResponseException {
        facade.clear();
    }

    @Test
    @DisplayName("Register - Positive")
    void register() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(req);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    @DisplayName("Register - Negative")
    void registerFalse() throws Exception {
        RegisterRequest req = new RegisterRequest("player2", "password", null);
        Assertions.assertThrows(exception.ResponseException.class, () -> {
            facade.register(req);
        });
    }

    @Test
    @DisplayName("Login - Positive")
    void login() throws Exception {
        RegisterRequest req1 = new RegisterRequest("player1", "password", "email");
        facade.register(req1);
        LoginRequest req = new LoginRequest("player1", "password");
        var authData = facade.login(req);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    @DisplayName("Login - Negative")
    void loginFalse() throws Exception {
        RegisterRequest req1 = new RegisterRequest("player1", "password", "email");
        facade.register(req1);
        LoginRequest req = new LoginRequest("player1", "wrong");
        Assertions.assertThrows(exception.ResponseException.class, () -> {
            facade.login(req);
        });
    }

    @Test
    @DisplayName("Clear - Positive")
    public void testClearPositive() throws DataAccessException, exception.ResponseException {
        RegisterRequest regRequest = new RegisterRequest("user1", "pass", "email@email.com");
        facade.register(regRequest);

        facade.clear();

        Assertions.assertThrows(exception.ResponseException.class, () -> {
            facade.login(new LoginRequest("user1", "pass"));
        });

    }

    @Test
    @DisplayName("Create - Positive")
    public void testCreatePositive() throws DataAccessException, exception.ResponseException {
        RegisterRequest reg = new RegisterRequest("user1", "pass", "email@email.com");
        var regResult = facade.register(reg);
        facade.setAuthToken(regResult.authToken());

        CreateRequest request = new CreateRequest("game1");
        CreateResult result = facade.create(request);

        assertNotNull(result);
        assertEquals("game1", result.gameName());
    }

    @Test
    @DisplayName("Create - Negative")
    public void testCreateNegative() throws exception.ResponseException {
        CreateRequest req = new CreateRequest("game1");
        Assertions.assertThrows(exception.ResponseException.class, () -> {
            facade.create(req);
        });
    }

    @DisplayName("Join - Positive")
    public void testJoinPositive() throws DataAccessException, exception.ResponseException {
        RegisterRequest reg = new RegisterRequest("user1", "pass", "email@email.com");
        var regResult = facade.register(reg);
        facade.setAuthToken(regResult.authToken());

        CreateRequest request = new CreateRequest("game1");
        CreateResult result = facade.create(request);

        JoinRequest req = new JoinRequest(ChessGame.TeamColor.WHITE, result.gameID());
        facade.join(req);
    }

    @DisplayName("Join - Negative")
    public void testJoinNegative() throws DataAccessException, exception.ResponseException {
        RegisterRequest reg = new RegisterRequest("user1", "pass", "email@email.com");
        var regResult = facade.register(reg);
        facade.setAuthToken(regResult.authToken());

        CreateRequest request = new CreateRequest("game1");
        CreateResult result = facade.create(request);

        JoinRequest req = new JoinRequest(ChessGame.TeamColor.WHITE, result.gameID());
        Assertions.assertThrows(exception.ResponseException.class, () -> {
            facade.join(req);
        });
    }

    @Test
    @DisplayName("Logout - Positive")
    void logout() throws Exception {
        RegisterRequest req1 = new RegisterRequest("player1", "password", "email");
        var regResult = facade.register(req1);
        facade.setAuthToken(regResult.authToken());
        facade.logout();
        Assertions.assertThrows(exception.ResponseException.class, () -> {
            facade.list();
        });
    }

    @Test
    @DisplayName("Logout - Negative")
    void logoutFalse() throws Exception {
        Assertions.assertThrows(exception.ResponseException.class, () -> {
            facade.logout();
        });
    }

    @Test
    @DisplayName("List - Positive")
    void listPositive() throws Exception {
        RegisterRequest reg = new RegisterRequest("user2", "pass", "email2@email.com");
        var regResult = facade.register(reg);
        facade.setAuthToken(regResult.authToken());

        CreateRequest req = new CreateRequest("Game to List");
        facade.create(req);

        ListResult result = facade.list();
        Assertions.assertTrue(result.games().size() >= 1);
    }

    @Test
    @DisplayName("List - Negative")
    void listNegative() {
        facade.setAuthToken(null);
        Assertions.assertThrows(exception.ResponseException.class, () -> {
            facade.list();
        });
    }
}
