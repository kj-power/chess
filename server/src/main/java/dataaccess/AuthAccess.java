package dataaccess;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import model.AuthData;
import model.UserData;

public class AuthAccess {
    private static final HashMap<String, AuthData> data = new HashMap<>();

    public static void createAuth(String username) {
        String token = UUID.randomUUID().toString();
        data.put(username, new AuthData(token, username));
    }

    public static AuthData getAuth(String username) {
        return data.get(username);
    }


    public static void clear() {
        data.clear();
    }

}
