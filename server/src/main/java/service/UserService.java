package service;
import dataaccess.UserAccess;
import dataaccess.AuthAccess;
import model.AuthData;
import model.UserData;

public class UserService {

    public static RegisterResult register(RegisterRequest registerRequest) throws ServiceException{
        UserData user = UserAccess.getUser(registerRequest.username());
        if (user == null) {
            UserAccess.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            AuthAccess.createAuth(registerRequest.username());
            String token = AuthAccess.getAuth(registerRequest.username()).authToken();
            RegisterResult result = new RegisterResult(registerRequest.username(), token);
            return result;
        }
        else {
            throw new ServiceException("Error: already taken");
        }
    }

    /*public LoginResult login(LoginRequest loginRequest) throws ServiceException{
        UserData user = UserAccess.getUser(loginRequest.username());
        if (user == null) {
            throw new ServiceException("Error: bad request");
        }
        AuthAccess.createAuth(loginRequest.username());
        LoginResult result = new LoginResult(loginRequest.username(), AuthAccess.getToken(loginRequest.username()));
        return result;
    }*/

    public void logout(LogoutRequest logoutRequest) throws ServiceException{
        AuthData data = AuthAccess.getAuth(logoutRequest.authToken());
        if (data == null) {
            throw new ServiceException("Error: bad request");
        }
    }

    public static void delete() {
        UserAccess.clear();
    }
}
