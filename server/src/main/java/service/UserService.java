package service;
import dataaccess.UserAccess;
import dataaccess.AuthAccess;
import model.AuthData;
import model.UserData;

public class UserService {

    public static RegisterResult register(RegisterRequest registerRequest) throws BadRequestException {
        if (registerRequest.username() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (registerRequest.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (registerRequest.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData user = UserAccess.getUser(registerRequest.username());
        if (user == null) {
            UserAccess.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            AuthAccess.createAuth(registerRequest.username());
            String token = AuthAccess.getAuth(registerRequest.username()).authToken();
            RegisterResult result = new RegisterResult(registerRequest.username(), token);
            return result;
        }
        else {
            throw new TakenException("Error: already taken");
        }
    }

    public static LoginResult login(LoginRequest loginRequest) {
        if (loginRequest.username() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (loginRequest.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData user = UserAccess.getUser(loginRequest.username());
        if (user == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (!loginRequest.password().equals(user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        AuthAccess.createAuth(loginRequest.username());
        String token = AuthAccess.getAuth(loginRequest.username()).authToken();
        LoginResult result = new LoginResult(loginRequest.username(), token);
        return result;
    }

    public void logout(LogoutRequest logoutRequest) throws BadRequestException {
        AuthData data = AuthAccess.getAuth(logoutRequest.authToken());
        if (data == null) {
            throw new BadRequestException("Error: bad request");
        }
    }

    public static void delete() {
        UserAccess.clear();
    }
}
