package dataaccess;

import model.UserData;

public interface UserAccess {
    boolean isEmpty() throws DataAccessException;

    void createUser(String username, String password, String email) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clear() throws DataAccessException;
}
