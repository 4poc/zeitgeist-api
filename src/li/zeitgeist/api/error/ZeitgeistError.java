package li.zeitgeist.api.error;

import java.util.Map;

public class ZeitgeistError extends Exception {
    private String type = "Default";
    private String message = "";

    public ZeitgeistError(String message) {
        super(message);
        this.message = message;
    }

    public ZeitgeistError(Map<String, ?> jsonObject) {
        super((String)jsonObject.get("message"));
        type = (String)jsonObject.get("type");
        message = (String)jsonObject.get("message");
    }

    public String getError() {
        return message;
    }

    public String getType()
    {
        return this.type;
    }
}

