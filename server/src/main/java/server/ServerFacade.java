package server;

import com.google.gson.Gson;
import requests.*;
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

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public Object clear() throws exception.ResponseException {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, null);
    }

    public RegisterResult register(RegisterRequest req) throws exception.ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, req, RegisterResult.class);
    }

    public LoginResult login(LoginRequest req) throws exception.ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, req, LoginResult.class);
    }

    public ListResult list(ListRequest req) throws exception.ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, req, null);
    }

    public CreateResult create(CreateRequest req) throws exception.ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, req, CreateResult.class);
    }

    public Object join(JoinRequest req) throws exception.ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, req, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws exception.ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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
            String reqData = new Gson().toJson(request);
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
