package dataaccess;
import model.AuthData;
import model.UserData;

import java.util.Collection;

public class UserAccess {
    private static Collection<UserData> data;

    public static void createUser(String username, String password, String email) {
        data.add(new UserData(username, password, email));
    }

    public static UserData getUser(String username) {
        for (UserData indUser : data) {
            if (indUser.username().equals(username)) {
                return indUser;
            }
        }
        return null;
    }

    public void clear() {
        data.clear();
    }
}
