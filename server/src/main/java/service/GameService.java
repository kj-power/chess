package service;

import dataaccess.*;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import results.CreateResult;
import results.ListResult;

import java.sql.SQLException;
import java.util.Collection;

public class GameService {

    private final GameAccess gameAccess;
    private final AuthAccess authAccess;

    public GameService(GameAccess gameAccess, AuthAccess authAccess) {
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    public void delete() throws DataAccessException {
        gameAccess.clear();
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        if (createRequest.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        int gameID = gameAccess.createGame(createRequest.gameName());
        return new CreateResult(createRequest.gameName(), gameID);
    }

    public void updateGame(String authToken, GameData gameData) throws UnauthorizedException, BadRequestException {
        try {
            authAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }

        try {
            gameAccess.updateGame(gameData);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public ListResult list(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        Collection<GameData> games = gameAccess.listGames();
        return new ListResult(games);
    }

    public void join(JoinRequest joinRequest, String username) throws DataAccessException, SQLException {
        gameAccess.joinGame(joinRequest.playerColor(), joinRequest.gameID(), username);
    }
}
