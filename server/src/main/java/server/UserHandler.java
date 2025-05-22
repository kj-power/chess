package server;
import com.google.gson.Gson;
import model.UserData;
import service.LoginRequest;
import service.LoginResult;
import service.UserService;

public class UserHandler {
    private UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }


}
