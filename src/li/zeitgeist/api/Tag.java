package li.zeitgeist.api;

import java.util.Map;

/**
 * Tag class with name and other information.
 */
public class Tag {
    /**
     * Unique internal ID of this tag.
     */
    private int id;
    /**
     * The tag itself.
     */
    private String name;
    /**
     * Number of items that are associated with this tag.
     */
    private int count;

    /**
     * Constructs a tag by json primitive map.
     * @param tagObject
     */
    public Tag(Map<String, ?> tagObject) {
        id = ((Double)tagObject.get("id")).intValue();
        name = (String)tagObject.get("tagname");
        count = ((Double)tagObject.get("count")).intValue();
    }

    /**
     * Unique internal ID.
     * @return integer
     */
    public int getId() {
        return this.id;
    }

    /**
     * The tag (name) itself.
     * @return string tag
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the number of items that are associated with this tag.
     * @return integer count
     */
    public int getCount() {
        return this.count;
    }

    public String toString() {
        return name;
    }
}

