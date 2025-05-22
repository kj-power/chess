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

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
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
            if (e instanceof ServiceException) {
                res.status(403);
                return new Gson().toJson(Map.of("message", e.getMessage()));
            }
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
            if (e instanceof ServiceException) {
                res.status(403);
                return new Gson().toJson(Map.of("message", e.getMessage()));
            }
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }
}
