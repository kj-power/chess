package dataaccess;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class UserAccess {
    private static final HashMap<String, UserData> data = new HashMap<>();

    public static void createUser(String username, String password, String email) {
        data.put(username, new UserData(username, password, email));
    }

    public static UserData getUser(String username) {
        return data.get(username);
    }

    public static void clear() {
        data.clear();
    }
}
