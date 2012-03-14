/**
 * Java Zeitgeist API
 * Copyright (C) 2012  Matthias Hecker <http://apoc.cc/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package li.zeitgeist.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.joda.time.DateTime;

/**
 * Zeitgeist Media Item.
 * 
 * Information about an image, audio or video item, contains
 * the thumbnail/location, tags and more.
 */
public class Item {

    /**
     * Supported media types, audio and video items are represented
     * only by their source link to Soundcloud or YouTube, etc.
     */
    public enum Type {
        IMAGE, AUDIO, VIDEO
    }

    /**
     * Location to the image or the video thumbnail.
     */
    public class Image {
        /**
         * Full size image location. Root is the URL base.
         */
        private String image;
        /**
         * Thumbnail, currently this is a 200x200 large gif/jpg/png image.
         */
        private String thumbnail;
        /**
         * Constructs a image object with locations to thumbnail/full-sized.
         * @param imageObject json object with primitives
         */
        public Image(Map<String, ?> imageObject) {
            if (imageObject.containsKey("image")) {
                image = (String)imageObject.get("image");
            }
            if (imageObject.containsKey("thumbnail")) {
                thumbnail = (String)imageObject.get("thumbnail");
            }
        }
        /**
         * The full-sized image.
         * @return location with the base URL as root.
         */
        public String getImage() {
            return this.image;
        }
        /**
         * The thumbnail, currently a 200x200 square image.
         * @return location with the base URL as root.
         */
        public String getThumbnail() {
            return this.thumbnail;
        }
    }

    /**
     * Store the dimensions of an image item.
     */
    public class Dimensions {
        private int width;
        private int height;
        /**
         * Constructs the dimensions object by a formatted string.
         * 
         * The format of the dimensions string is [width]x[height].
         * 
         * @param dimensions
         */
        public Dimensions(String dimensions) {
            String[] s = dimensions.split("x");
            width = Integer.parseInt(s[0]);
            height = Integer.parseInt(s[1]);
        }
        public String toString() {
            return String.valueOf(width) + "x" + String.valueOf(height);
        }
        /**
         * @return width value
         */
        public int getWidth() {
            return this.width;
        }
        /**
         * @return height value
         */
        public int getHeight() {
            return this.height;
        }
    }

    /**
     * Unique ID of this item, numerical, sequential.
     */
    private int id;

    /**
     * Item type, image, audio, video.
     */
    private Type type;

    /**
     * Locations to full-sized and thumbnail image.
     */
    private Image image;

    /**
     * Optional source for this item, can be a URL or filename.
     * 
     * May contain the URL to a video/audio, the URL the image was
     * remotely downloaded from or the filename of the upload. 
     */
    private String source;

    /**
     * Parsed item title, for instance the YouTube video title.
     */
    private String title;

    /**
     * Timestamp when this item was created.
     */
    private DateTime created;

    /**
     * Flag for "Not Safe For Work" (inappropriate) items.
     */
    private boolean nsfw;

    /**
     * Size of the full-sized image in bytes.
     */
    private int size;

    /**
     * Detected content mimetype.
     */
    private String mimetype;

    /**
     * Currently an hexdigest MD5.
     */
    private String checksum;
    
    /**
     * The width and height of the full-sized image.
     */
    private Dimensions dimensions;

    /**
     * Number of +1 votes.
     */
    private int upvotes;

    /**
     * ID of the user that created the item.
     */
    private int userId = -1;

    /**
     * List of tags associated with this item.
     */
    private List<Tag> tags;

    /**
     * Construct item object by json primitive map.
     * @param itemObject
     */
    public Item(Map<String, ?> itemObject) {
        id = ((Double)itemObject.get("id")).intValue();
        String typeString = (String)itemObject.get("type");
        if (typeString.equals("image")) {
            type = Type.IMAGE;
        }
        else if (typeString.equals("audio")) {
            type = Type.AUDIO;
        }
        else if (typeString.equals("video")) {
            type = Type.VIDEO;
        }

        if (itemObject.containsKey("image") && itemObject.get("image") != null)
            image = new Image((Map<String, ?>)itemObject.get("image"));

        if (itemObject.containsKey("source") && itemObject.get("source") != null)
            source = (String)itemObject.get("source");

        if (itemObject.containsKey("title") && itemObject.get("title") != null)
            title = (String)itemObject.get("title");

        if (itemObject.containsKey("created_at") && itemObject.get("created_at") != null)
            created = DateTime.parse((String)itemObject.get("created_at"));

        if (itemObject.containsKey("nsfw") && itemObject.get("nsfw") != null)
            nsfw = (Boolean)itemObject.get("nsfw");

        if (itemObject.containsKey("size") && itemObject.get("size") != null)
            size = ((Double)itemObject.get("size")).intValue();

        if (itemObject.containsKey("mimetype") && itemObject.get("mimetype") != null)
            mimetype = (String)itemObject.get("mimetype");

        if (itemObject.containsKey("checksum") && itemObject.get("checksum") != null)
            checksum = (String)itemObject.get("checksum");

        if (itemObject.containsKey("dimensions") && itemObject.get("dimensions") != null)
            dimensions = new Dimensions((String)itemObject.get("dimensions"));

        if (itemObject.containsKey("upvote_count") && itemObject.get("upvote_count") != null)
            upvotes = ((Double)itemObject.get("upvote_count")).intValue();

        if (itemObject.get("dm_user_id") != null && itemObject.get("dm_user_id") != null) {
            userId = ((Double)itemObject.get("dm_user_id")).intValue();
        }

        tags = new Vector<Tag>();
        ArrayList<Map<String, ?>> tagsObject = (ArrayList<Map<String, ?>>)itemObject.get("tags");
        for (Map<String, ?> tagObject : tagsObject) {
            if (tagObject == null) break;// why does this happen?
            addTag(new Tag(tagObject));
        }
    }

    /**
     * Unique ID for this item.
     * 
     * A numeric, sequential and unique ID.
     * 
     * @return integer id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Item type, image, audio, video.
     * @return Type enum
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Image object with location to full-sized and thumbnail image.
     * @return image object
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Optional source for this item, can be a URL or filename.
     * 
     * May contain the URL to a video/audio, the URL the image was
     * remotely downloaded from or the filename of the upload. 
     * @return string
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Parsed item title, for instance the YouTube video title.
     * @return string
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Timestamp when this item was created.
     * @return string
     */
    public DateTime getCreated() {
        return this.created;
    }

    /**
     * Flag for "Not Safe For Work" (inappropriate) items.
     * @return string
     */
    public boolean isNsfw() {
        return this.nsfw;
    }

    /**
     * Size of the full-sized image in bytes.
     * @return bytes
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Detected content mimetype.
     * @return mimetype, for instance: image/jpeg
     */
    public String getMimetype() {
        return this.mimetype;
    }

    /**
     * Currently an hexdigest MD5.
     * @return hex string
     */
    public String getChecksum() {
        return this.checksum;
    }

    /**
     * The width and height of the full-sized image.
     * @return dimensions object
     */
    public Dimensions getDimensions() {
        return this.dimensions;
    }

    /**
     * Number of +1 votes.
     * @return vote count
     */
    public int getUpvotes() {
        return this.upvotes;
    }

    /**
     * ID of the user that created the item.
     * @return ID
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Add tag object to the list.
     * @param tag
     */
    private void addTag(Tag tag) {
        tags.add(tag);
    }

    /**
     * List of tags associated with this item.
     * @return list of tag objects
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * List of the tag names without other information.
     * @return array of string
     */
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
    
    @Override
    public boolean equals(Object otherItem) {
        return getId() == ((Item) otherItem).getId();
    }
}
