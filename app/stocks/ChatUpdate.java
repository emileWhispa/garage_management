package stocks;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import static java.util.Objects.requireNonNull;

/** A JSON presentation class for stock updates. */
public class ChatUpdate {
    private final String symbol;
    private final String type;
    private final JsonNode user;

    public ChatUpdate(String symbol, String type, JsonNode user) {
        this.symbol = requireNonNull(symbol);
        this.type = requireNonNull(type);
        this.user = requireNonNull(user);
    }

    public String getType() {
        return "stockupdatex";
    }

    public String getVar() {
        return "stockupdateMend";
    }

    public String getContent() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public JsonNode getUser() {
        return user;
    }
}
