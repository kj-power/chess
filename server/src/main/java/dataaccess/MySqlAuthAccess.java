package dataaccess;

import model.AuthData;

public class MySqlAuthAccess implements AuthAccess {


    @Override
    public boolean isEmpty() throws DataAccessException {
        return false;
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        return "";
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void deleteToken(String token) throws DataAccessException {

    }
}
