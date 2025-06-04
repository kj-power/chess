package webSocketMessages;

import com.google.gson.Gson;

public record Action(Type type, String username) {
    public enum Type {
        LOGIN,
        LOGOUT
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}