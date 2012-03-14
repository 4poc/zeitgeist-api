package li.zeitgeist.api.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import li.zeitgeist.api.Item;

/**
 * Indicates if zeitgeist found a duplicate item.
 * 
 * The duplication check is currently performed by MD5 summing
 * the image and checking with stored hashes.
 */
public class DuplicateError extends ZeitgeistError {
    /**
     * The ID of the existing item that matches the submitted media. 
     */
    private int id;

    /**
     * Construct by message.
     * @param message
     */
    public DuplicateError(String message) {
        super(message);
    }

    /**
     * Construct by remote json primitive map.
     * @param jsonObject
     */
    public DuplicateError(Map<String, ?> jsonObject) {
        super(jsonObject);

        this.id = ((Double)jsonObject.get("id")).intValue();
    }

    /**
     * Returns the ID of the existing item that matches the submitted media. 
     * @return ID
     */
    public int getId() {
        return this.id;
    }
}

