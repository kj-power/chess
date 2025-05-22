package dataaccess;
import model.UserData;

import java.util.HashMap;

public class UserAccess {
    private static final HashMap<String, UserData> DATA_HASH_MAP = new HashMap<>();

    public static void createUser(String username, String password, String email) {
        DATA_HASH_MAP.put(username, new UserData(username, password, email));
    }

    public static UserData getUser(String username) {
        return DATA_HASH_MAP.get(username);
    }

    public static void clear() {
        DATA_HASH_MAP.clear();
    }
}
