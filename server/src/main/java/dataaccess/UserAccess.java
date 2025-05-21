package dataaccess;
import model.AuthData;
import model.UserData;

import java.util.Collection;

public class UserAccess {
    private Collection<UserData> data;

    void createUser(String username, String password, String email) {
        data.add(new UserData(username, password, email));
    }

    UserData getUser(String username) {
        for (UserData indUser : data) {
            if (indUser.username().equals(username)) {
                return indUser;
            }
        }
        return null;
    }

    void clear() {
        data.clear();
    }
}
