package dataaccess;
import java.util.Collection;
import java.util.UUID;
import model.AuthData;

public class AuthAccess {
    private Collection<AuthData> data;

    void createAuth(String username) {
        String token = UUID.randomUUID().toString();
        data.add(new AuthData(token, username));
    }

    AuthData getAuth(String token) {
        for (AuthData indAuth : data) {
            if (indAuth.authToken().equals(token)) {
                return indAuth;
            }
        }
        return null;
    }

    void deleteAuth(String token) {
        data.removeIf(indAuth -> indAuth.authToken().equals(token));
    }

    void clear() {
        data.clear();
    }
}
