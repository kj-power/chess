package dataaccess;
import model.UserData;

import java.util.HashMap;

public class MemoryUserAccess implements UserAccess{
    private static final HashMap<String, UserData> DATA_HASH_MAP = new HashMap<>();

    public boolean isEmpty() {
        if (DATA_HASH_MAP.isEmpty()) {
            return true;
        }
        return false;
    }

    public void createUser(String username, String password, String email) {
        DATA_HASH_MAP.put(username, new UserData(username, password, email));
    }

    public UserData getUser(String username) {
        return DATA_HASH_MAP.get(username);
    }

    public void clear() {
        DATA_HASH_MAP.clear();
    }
}
