package li.zeitgeist.api.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import li.zeitgeist.api.Item;

public class RemoteError extends ZeitgeistError {
    private ZeitgeistError error;
    private String url;

    public RemoteError(String message) {
        super(message);
    }

    public RemoteError(Map<String, ?> jsonObject) {
        super(jsonObject);

        this.error = new ZeitgeistError((Map<String, ?>)jsonObject.get("error"));
        this.url = (String)jsonObject.get("url");
    }

    public String getUrl()
    {
        return this.url;
    }
    
    public ZeitgeistError getInnerError() {
        return error;
    }
}

