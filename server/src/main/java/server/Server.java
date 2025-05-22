package server;

import com.google.gson.Gson;
import dataaccess.AuthAccess;
import dataaccess.GameAccess;
import dataaccess.UserAccess;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.*;
import spark.*;

import java.util.Map;

public class Server {

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object logout(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            LogoutRequest user = new LogoutRequest(authToken);
            UserService.logout(user);
            res.type("application/json");
            return "";
        } catch(Exception e) {
            if (e instanceof UnauthorizedException) {
                res.status(401);
                return new Gson().toJson(Map.of("message", e.getMessage()));
            }
            res.status(500);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    private Object login(Request req, Response res) {
        try {
            LoginRequest user = new Gson().fromJson(req.body(), LoginRequest.class);
            LoginResult result = UserService.login(user);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch(Exception e) {
            if (e instanceof BadRequestException) {
                res.status(400);
                return new Gson().toJson(Map.of("message", e.getMessage()));
            }
            if (e instanceof UnauthorizedException) {
                res.status(401);
                return new Gson().toJson(Map.of("message", e.getMessage()));
            }
            res.status(500);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) {
        try {
            RegisterRequest user = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegisterResult result = UserService.register(user);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch(Exception e) {
            if (e instanceof TakenException) {
                res.status(403);
                return new Gson().toJson(Map.of("message", e.getMessage()));
            }
            if (e instanceof BadRequestException) {
                res.status(400);
                return new Gson().toJson(Map.of("message", e.getMessage()));
            }
            res.status(500);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }

    }

    private Object clear(Request req, Response res) {
        try {
            UserService.delete();
            GameService.delete();
            AuthService.delete();
            res.status(200);
            return "";
        } catch(Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }
}
