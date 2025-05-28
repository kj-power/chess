package service;
import dataaccess.AuthAccess;
import dataaccess.DataAccessException;
import dataaccess.UserAccess;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;

import java.util.Objects;

public class UserService {

    private final UserAccess userAccess;
    private final AuthAccess authAccess;

    public UserService(UserAccess userAccess, AuthAccess authAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (registerRequest.username() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData user = userAccess.getUser(registerRequest.username());
        if (user == null) {
            userAccess.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            String token = authAccess.createAuth(registerRequest.username());
            RegisterResult result = new RegisterResult(registerRequest.username(), token);
            return result;
        }
        else {
            throw new TakenException("Error: already taken");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (loginRequest.username() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData user = userAccess.getUser(loginRequest.username());
        if (user == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (!Objects.equals(user.password(), loginRequest.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String token = authAccess.createAuth(loginRequest.username());
        LoginResult result = new LoginResult(loginRequest.username(), token);
        return result;
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        if (logoutRequest == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (logoutRequest.authToken() == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData data = authAccess.getAuth(logoutRequest.authToken());
        if (data == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        authAccess.deleteToken(logoutRequest.authToken());
    }

    public void delete() throws DataAccessException {
        userAccess.clear();
    }
}
