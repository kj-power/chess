package service;
import dataaccess.UserAccess;
import dataaccess.AuthAccess;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService {

    public static RegisterResult register(RegisterRequest registerRequest) {
        if (registerRequest.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (registerRequest.username() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData user = UserAccess.getUser(registerRequest.username());
        if (user == null) {
            UserAccess.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            String token = AuthAccess.createAuth(registerRequest.username());
            RegisterResult result = new RegisterResult(registerRequest.username(), token);
            return result;
        }
        else {
            throw new TakenException("Error: already taken");
        }
    }

    public static LoginResult login(LoginRequest loginRequest) {
        if (loginRequest.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (loginRequest.username() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData user = UserAccess.getUser(loginRequest.username());
        if (user == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (!Objects.equals(user.password(), loginRequest.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String token = AuthAccess.createAuth(loginRequest.username());
        LoginResult result = new LoginResult(loginRequest.username(), token);
        return result;
    }

    public static void logout(LogoutRequest logoutRequest) {
        if (logoutRequest == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (logoutRequest.authToken() == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData data = AuthAccess.getAuth(logoutRequest.authToken());
        if (data == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthAccess.deleteToken(logoutRequest.authToken());
    }

    public static void delete() {
        UserAccess.clear();
    }
}
