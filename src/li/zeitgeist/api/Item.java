package li.zeitgeist.api;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.joda.time.DateTime;

public class Item {

    public enum Type {
        IMAGE, AUDIO, VIDEO
    }

    public class Image {
        private String image;
        private String thumbnail;
        public Image(Map<String, ?> imageObject) {
            image = (String)imageObject.get("image");
            thumbnail = (String)imageObject.get("thumbnail");
        }
        public String getImage() {
            return this.image;
        }
        public String getThumbnail() {
            return this.thumbnail;
        }
    }

    public class Dimensions {
        private int width;
        private int height;
        public Dimensions(String dimensions) {
            String[] s = dimensions.split("x");
            width = Integer.parseInt(s[0]);
            height = Integer.parseInt(s[1]);
        }
        public String toString() {
            return String.valueOf(width) + "x" + String.valueOf(height);
        }
        public int getWidth() {
            return this.width;
        }
        public int getHeight() {
            return this.height;
        }
    }

    private int id;

    private Type type;

    private Image image;

    private String source;

    private String title;

    private DateTime created;

    private boolean nsfw;

    private int size;

    private String mimetype;

    private String checksum;
    private Dimensions dimensions;

    private int upvotes;

    private int userId = -1;

    private Vector<Tag> tags;

    public Item(Map<String, ?> itemObject) {
        id = ((Double)itemObject.get("id")).intValue();
        String typeString = (String)itemObject.get("type");
        if (typeString == "image") {
            type = Type.IMAGE;
        }
        else if (typeString == "audio") {
            type = Type.AUDIO;
        }
        else if (typeString == "video") {
            type = Type.VIDEO;
        }

        image = new Image((Map<String, ?>)itemObject.get("image"));

        source = (String)itemObject.get("source");

        title = (String)itemObject.get("title");

        created = DateTime.parse((String)itemObject.get("created_at"));

        nsfw = (Boolean)itemObject.get("nsfw");

        size = ((Double)itemObject.get("size")).intValue();

        mimetype = (String)itemObject.get("mimetype");

        checksum = (String)itemObject.get("checksum");

        dimensions = new Dimensions((String)itemObject.get("dimensions"));

        upvotes = ((Double)itemObject.get("upvote_count")).intValue();

        if (itemObject.get("dm_user_id") != null) {
            userId = ((Double)itemObject.get("dm_user_id")).intValue();
        }

        tags = new Vector<Tag>();
        ArrayList<Map<String, ?>> tagsObject = (ArrayList<Map<String, ?>>)itemObject.get("tags");
        for (Map<String, ?> tagObject : tagsObject) {
            if (tagObject == null) break;// why does this happen?
            addTag(new Tag(tagObject));
        }
    }

    public int getId() {
        return this.id;
    }

    public Type getType() {
        return this.type;
    }

    public Image getImage() {
        return this.image;
    }

    public String getSource() {
        return this.source;
    }

    public String getTitle() {
        return this.title;
    }

    public DateTime getCreated() {
        return this.created;
    }

    public boolean isNsfw() {
        return this.nsfw;
    }

    public int getSize() {
        return this.size;
    }

    public String getMimetype() {
        return this.mimetype;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public Dimensions getDimensions() {
        return this.dimensions;
    }

    public int getUpvotes() {
        return this.upvotes;
    }

    public int getUserId() {
        return this.userId;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public Vector<Tag> getTags() {
        return tags;
    }

    public String[] getTagNames() {
        if (tags == null) return null;
        String[] tagNames = new String[tags.size()];
        Tag tag = null;
        for (int i = 0; i < tagNames.length; i++) {
            tag = tags.get(i);
            tagNames[i] = tag.getName();
        }
        return tagNames;
    }
}


