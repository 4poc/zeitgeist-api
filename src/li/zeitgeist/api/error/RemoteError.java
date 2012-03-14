package li.zeitgeist.api.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import li.zeitgeist.api.Item;

/**
 * Error during remote uploading of a URL.
 * 
 * May happen if the URL provided could not be found or is of an
 * invalid type.  
 */
public class RemoteError extends ZeitgeistError {
    /**
     * The exact internal error message that occured, maybe something
     * like a InternalServerError or IOException, HostNotFound etc.
     */
    private ZeitgeistError error;
    
    /**
     * The URL of the remote upload.
     */
    private String url;

    /**
     * Construct error by message.
     * @param message
     */
    public RemoteError(String message) {
        super(message);
    }

    /**
     * Construct by json primitive map.
     * @param jsonObject
     */
    public RemoteError(Map<String, ?> jsonObject) {
        super(jsonObject);

        this.error = new ZeitgeistError((Map<String, ?>)jsonObject.get("error"));
        this.url = (String)jsonObject.get("url");
    }

    /**
     * The URL of the remote upload.
     * @return URL
     */
    public String getUrl() {
        return this.url;
    }
    
    /**
     * The exact internal error message that occured, maybe something
     * like a InternalServerError or IOException, HostNotFound etc.
     * @return error object
     */
    public ZeitgeistError getInnerError() {
        return error;
    }
}

