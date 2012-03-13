package li.zeitgeist.api.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import li.zeitgeist.api.Item;

public class CreateItemError extends ZeitgeistError {

    private ZeitgeistError error;
    private List<Item> items;

    public CreateItemError(String message) {
        super(message);
    }

    public CreateItemError(Map<String, ?> jsonObject) {
        super(jsonObject);

        this.error = new ZeitgeistError((Map<String, ?>)jsonObject.get("error"));
        this.items = new Vector<Item>();
        for (Map<String, ?> itemObject : (ArrayList<Map<String, ?>>)jsonObject.get("items")) {
            this.items.add(new Item(itemObject));
        }
    }
    
    public ZeitgeistError getInnerError() {
        return error;
    }

    public List<Item> getItems() {
        return items;
    }

}

