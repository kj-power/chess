package dataaccess;
import java.util.HashMap;
import java.util.UUID;
import model.AuthData;

public class MemoryAuthAccess implements AuthAccess {
    private static final HashMap<String, AuthData> DATA_HASH_MAP = new HashMap<>();

    public boolean isEmpty() {
        if (DATA_HASH_MAP.isEmpty()) {
            return true;
        }
        return false;
    }

    public String createAuth(String username) {
        String token = UUID.randomUUID().toString();
        DATA_HASH_MAP.put(token, new AuthData(token, username));
        return token;
    }

    public AuthData getAuth(String token) {
        return DATA_HASH_MAP.get(token);
    }


    public void clear() {
        DATA_HASH_MAP.clear();
    }

    public void deleteToken(String token) {
        DATA_HASH_MAP.remove(token);
    }

}
