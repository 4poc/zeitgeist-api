package li.zeitgeist.api.error;

import java.util.Map;

/**
 * Generalized Error Class for API methods.
 * 
 * The library unifies all exceptions that may occur and zeitgeist
 * specific ones into this common base exception class.
 */
public class ZeitgeistError extends Exception {
    /**
     * The type of this error.
     */
    private String type = "Default";
    /**
     * Message describing the error occured.
     */
    private String message = "";

    /**
     * Construct a error only by message.
     * @param message
     */
    public ZeitgeistError(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Construct a error by json that the server returned.
     * @param jsonObject
     */
    public ZeitgeistError(Map<String, ?> jsonObject) {
        super((String)jsonObject.get("message"));
        type = (String)jsonObject.get("type");
        message = (String)jsonObject.get("message");
    }

    /**
     * The error message.
     * @return message
     */
    public String getError() {
        return message;
    }

    /**
     * Type of this error.
     * @return type string
     */
    public String getType()
    {
        return this.type;
    }
}

