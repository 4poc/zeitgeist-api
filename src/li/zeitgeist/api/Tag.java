package li.zeitgeist.api;

import java.util.Map;

public class Tag {
    private int id;
    private String name;

    public Tag(Map<String, ?> tagObject) {
        id = ((Double)tagObject.get("id")).intValue();
        name = (String)tagObject.get("tagname");
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return name;
    }
}

