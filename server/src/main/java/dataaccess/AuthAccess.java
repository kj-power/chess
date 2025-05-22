package dataaccess;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import model.AuthData;
import model.UserData;

public class AuthAccess {
    private static final HashMap<String, AuthData> data = new HashMap<>();

    public static String createAuth(String username) {
        String token = UUID.randomUUID().toString();
        data.put(token, new AuthData(token, username));
        return token;
    }

    public static AuthData getAuth(String token) {
        return data.get(token);
    }


    public static void clear() {
        data.clear();
    }

    public static void deleteToken(String token) {
        data.remove(token);
    }

}
