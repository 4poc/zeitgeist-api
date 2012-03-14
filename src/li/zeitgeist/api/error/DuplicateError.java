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
package li.zeitgeist.api.error;

import java.util.Map;

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

