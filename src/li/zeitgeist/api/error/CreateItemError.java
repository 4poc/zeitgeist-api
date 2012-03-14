package li.zeitgeist.api.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import li.zeitgeist.api.Item;

/**
 * Container error class, thrown by upload/remote if an error occured.
 * 
 * This contains the list of successfully created items (if multiple
 * items created) and the error that occured and stopped the process.
 */
public class CreateItemError extends ZeitgeistError {

    /**
     * Error that occured and stopped the creation.
     */
    private ZeitgeistError error;
    
    /**
     * List of items that were successfully created up to this point.
     */
    private List<Item> items;

    /**
     * Construct Error by message.
     * @param message
     */
    public CreateItemError(String message) {
        super(message);
    }

    /**
     * Construct by json primitive map.
     * @param jsonObject
     */
    public CreateItemError(Map<String, ?> jsonObject) {
        super(jsonObject);

        this.error = new ZeitgeistError((Map<String, ?>)jsonObject.get("error"));
        this.items = new Vector<Item>();
        for (Map<String, ?> itemObject : (ArrayList<Map<String, ?>>)jsonObject.get("items")) {
            this.items.add(new Item(itemObject));
        }
    }
    
    /**
     * Error that occured and stopped the creation.
     * @return error object
     */
    public ZeitgeistError getInnerError() {
        return error;
    }

    /**
     * List of items that were successfully created up to this point.
     * @return error object
     */
    public List<Item> getItems() {
        return items;
    }

}

