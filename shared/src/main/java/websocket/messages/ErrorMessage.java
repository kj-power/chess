package websocket.messages;

import chess.ChessGame;

public class ErrorMessage extends ServerMessage{

    private final String message;

    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}