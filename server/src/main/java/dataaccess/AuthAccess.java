package dataaccess;
import java.util.Collection;
import java.util.UUID;
import model.AuthData;

public class AuthAccess {
    private static Collection<AuthData> data;

    public static void createAuth(String username) {
        String token = UUID.randomUUID().toString();
        data.add(new AuthData(token, username));
    }

    public AuthData getAuth(String token) {
        for (AuthData indAuth : data) {
            if (indAuth.authToken().equals(token)) {
                return indAuth;
            }
        }
        return null;
    }

    public static String getToken(String username) {
        for (AuthData indAuth : data) {
            if (indAuth.username().equals(username)) {
                return indAuth.authToken();
            }
        }
        return null;
    }

    public void deleteAuth(String token) {
        data.removeIf(indAuth -> indAuth.authToken().equals(token));
    }

    void clear() {
        data.clear();
    }
}
