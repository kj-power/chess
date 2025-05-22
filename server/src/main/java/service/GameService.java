package service;

import dataaccess.AuthAccess;
import dataaccess.GameAccess;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import results.CreateResult;
import results.ListResult;

import java.util.Collection;

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

    public static ListResult list(String authToken) {
        if (authToken == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (AuthAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        Collection<GameData> games = GameAccess.listGames();
        return new ListResult(games);
    }

    public static void join(JoinRequest joinRequest, String username) {
        GameAccess.joinGame(joinRequest.playerColor(), joinRequest.gameID(), username);
    }
}
