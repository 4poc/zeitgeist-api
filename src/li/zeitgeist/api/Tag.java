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

import java.io.Serializable;
import java.util.Map;

/**
 * Tag class with name and other information.
 */
public class Tag implements Serializable {
    
    /**
     * Object version ID.
     */
    private static final long serialVersionUID = -7619239658122990840L;
    
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

