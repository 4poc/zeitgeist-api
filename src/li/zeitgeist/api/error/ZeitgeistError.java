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
 * Generalized Error Class for API methods.
 * 
 * The library unifies all exceptions that may occur and zeitgeist
 * specific ones into this common base exception class.
 */
public class ZeitgeistError extends Exception {
    /**
     * The type of this error.
     */
    private String type = "Default";
    /**
     * Message describing the error occured.
     */
    private String message = "";

    /**
     * Construct a error only by message.
     * @param message
     */
    public ZeitgeistError(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Construct a error by json that the server returned.
     * @param jsonObject
     */
    public ZeitgeistError(Map<String, ?> jsonObject) {
        super((String)jsonObject.get("message"));
        type = (String)jsonObject.get("type");
        message = (String)jsonObject.get("message");
    }

    /**
     * The error message.
     * @return message
     */
    public String getError() {
        return message;
    }

    /**
     * Type of this error.
     * @return type string
     */
    public String getType()
    {
        return this.type;
    }
}

