package service;

import dataaccess.AuthAccess;
import dataaccess.UserAccess;

public class AuthService {

    public static void delete() {
        AuthAccess.clear();
    }
}
