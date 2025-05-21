package service;
import dataaccess.UserAccess;
import dataaccess.AuthAccess;
import model.UserData;

public class UserService {

    public RegisterResult register(RegisterRequest registerRequest) throws ServiceException{
        UserData user = UserAccess.getUser(registerRequest.username());
        if (user == null) {
            UserAccess.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            AuthAccess.createAuth(registerRequest.username());
            RegisterResult result = new RegisterResult(registerRequest.username(), AuthAccess.getToken(registerRequest.username()));
            return result;
        }
        else {
            throw new ServiceException("User already exists");
        }
    }
    public LoginResult login(LoginRequest loginRequest) {

    }
    public void logout(LogoutRequest logoutRequest) {}
}
