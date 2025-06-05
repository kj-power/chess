package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import requests.CreateRequest;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Object clear() throws exception.ResponseException {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, null);
    }

    public RegisterResult register(RegisterRequest req) {
        var path = "/user";
        try {
            return this.makeRequest("POST", path, req, RegisterResult.class);
        }
        catch (exception.ResponseException e) {
            System.out.println("Invalid entries");
            return null;
        }
    }

    public LoginResult login(LoginRequest req) {
        var path = "/session";
        try {
            return this.makeRequest("POST", path, req, LoginResult.class);
        }
        catch (exception.ResponseException e) {
            System.out.println("Invalid entries");
            return null;
        }
    }

    public results.ListResult list() throws exception.ResponseException {
        var path = "/game";
        try {
            return this.makeRequest("GET", path, null, ListResult.class);
        }
        catch (exception.ResponseException e) {
            System.out.println("Failed to list games");
            return null;
        }
    }

    public CreateResult create(CreateRequest req) {
        var path = "/game";
        try {
            return this.makeRequest("POST", path, req, CreateResult.class);
        }
        catch (exception.ResponseException e) {
            System.out.println("Invalid entries");
            return null;
        }
    }

    public boolean join(JoinRequest req) {
        var path = "/game";
        try {
            this.makeRequest("PUT", path, req, null);
            System.out.println("Joined game successfully.");
            return true;
        } catch (exception.ResponseException e) {
            System.out.println("Failed to join game");
            return false;
        }
    }

    public void logout() {
        var path = "/session";
        try {
            this.makeRequest("DELETE", path, null, null);
        }
        catch (exception.ResponseException e) {
            System.out.println("Invalid entries");
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws exception.ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (exception.ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new exception.ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            Gson gson = new GsonBuilder().serializeNulls().create();
            String reqData = gson.toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, exception.ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw exception.ResponseException.fromJson(respErr);
                }
            }

            throw new exception.ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
