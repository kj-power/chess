package service;

import dataaccess.AuthAccess;
import dataaccess.GameAccess;
import dataaccess.UserAccess;
import model.UserData;

public class GameService {

    public static void delete() {
        GameAccess.clear();
    }

    public static CreateResult create(CreateRequest createRequest) {
        if (createRequest.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        int gameID = GameAccess.createGame(createRequest.gameName());
        return new CreateResult(createRequest.gameName(), gameID);
    }
}
