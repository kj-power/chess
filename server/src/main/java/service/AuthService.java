package service;

import dataaccess.AuthAccess;
import dataaccess.DataAccessException;

public class AuthService {
    private final AuthAccess authAccess;

    public AuthService(AuthAccess authAccess) {
        this.authAccess = authAccess;
    }

    public void delete() throws DataAccessException {
        authAccess.clear();
    }
}
