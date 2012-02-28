package li.zeitgeist.api.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import li.zeitgeist.api.Item;

public class DuplicateError extends ZeitgeistError {
    private int id;

    public DuplicateError(String message) {
        super(message);
    }

    public DuplicateError(Map<String, ?> jsonObject) {
        super(jsonObject);

        this.id = ((Double)jsonObject.get("id")).intValue();
    }

    public int getId()
    {
        return this.id;
    }
}

