package service;

import dataaccess.GameAccess;
import dataaccess.UserAccess;

public class GameService {

    public static void delete() {
        GameAccess.clear();
    }
}
