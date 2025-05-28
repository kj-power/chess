package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.util.UUID;

public interface AuthAccess {
    boolean isEmpty() throws DataAccessException;

    String createAuth(String username) throws DataAccessException;

    AuthData getAuth(String token) throws DataAccessException;


    void clear() throws DataAccessException;

    void deleteToken(String token) throws DataAccessException;
}
