package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import requests.*;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.RegisterResult;
import service.*;
import spark.*;

import java.util.Map;

public class Server {

    private final UserService userService;
    private final AuthService authService;
    private final AuthAccess authAccess;
    private final UserAccess userAccess;
    private final GameService gameService;
    private final GameAccess gameAccess;

    public Server() {
        userAccess = new MemoryUserAccess();
        authAccess = new MemoryAuthAccess();
        gameAccess = new MemoryGameAccess();
        userService = new UserService(userAccess, authAccess);
        authService = new AuthService(authAccess);
        gameService = new GameService(gameAccess, authAccess);
    }

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::create);
        Spark.get("/game", this::list);
        Spark.put("/game", this::join);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object catchHelper(Response res, Exception e) {
        if (e instanceof BadRequestException) {
            res.status(400);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
        if (e instanceof UnauthorizedException) {
            res.status(401);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
        if (e instanceof TakenException) {
            res.status(403);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
        res.status(500);
        return new Gson().toJson(Map.of("message", e.getMessage()));
    }

    private Object join(Request req, Response res) {
        try {
            System.out.println("Raw body: " + req.body());
            String authToken = req.headers("Authorization");
            if (authToken == null) {
                throw new UnauthorizedException("Error: unauthorized");
            }
            AuthData data = authAccess.getAuth(authToken);
            if (data == null) {
                throw new UnauthorizedException("Error: unauthorized");
            }
            String username = data.username();
            JoinRequest user = new Gson().fromJson(req.body(), JoinRequest.class);
            System.out.println("Parsed color: " + user.playerColor());
            gameService.join(user, username);
            return "";
        } catch(Exception e) {
            return catchHelper(res, e);
        }
    }

    private Object list(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null) {
                throw new BadRequestException("Error: bad request");
            }
            ListResult result = gameService.list(authToken);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch(Exception e) {
            return catchHelper(res, e);
        }
    }

    private Object create(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null) {
                throw new BadRequestException("Error: bad request");
            }
            AuthData data = authAccess.getAuth(authToken);
            if (data == null) {
                throw new UnauthorizedException("Error: unauthorized");
            }
            CreateRequest user = new Gson().fromJson(req.body(), CreateRequest.class);
            CreateResult result = gameService.create(user);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch(Exception e) {
            return catchHelper(res, e);
        }
    }

    private Object logout(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            LogoutRequest user = new LogoutRequest(authToken);
            userService.logout(user);
            res.type("application/json");
            return "";
        } catch(Exception e) {
            return catchHelper(res, e);
        }
    }

    private Object login(Request req, Response res) {
        try {
            LoginRequest user = new Gson().fromJson(req.body(), LoginRequest.class);
            LoginResult result = userService.login(user);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch(Exception e) {
            return catchHelper(res, e);
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) {
        try {
            RegisterRequest user = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegisterResult result = userService.register(user);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch(Exception e) {
            return catchHelper(res, e);
        }

    }

    private Object clear(Request req, Response res) {
        try {
            userService.delete();
            gameService.delete();
            authService.delete();
            res.status(200);
            return "";
        } catch(Exception e) {
            return catchHelper(res, e);
        }
    }
}
