package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private final ChessGame game;
    private final ChessGame.TeamColor color;

    public LoadGameMessage(ChessGame game, ChessGame.TeamColor color) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.color = color;
    }


}
