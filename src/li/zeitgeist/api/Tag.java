package li.zeitgeist.api;

import java.util.Map;

public class Tag {
    private int id;
    private String name;
    private int count;

    public Tag(Map<String, ?> tagObject) {
        id = ((Double)tagObject.get("id")).intValue();
        name = (String)tagObject.get("tagname");
        count = ((Double)tagObject.get("count")).intValue();
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getCount() {
        return this.count;
    }

    public String toString() {
        return name;
    }
}

