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
 * Error during remote uploading of a URL.
 * 
 * May happen if the URL provided could not be found or is of an
 * invalid type.  
 */
public class RemoteError extends ZeitgeistError {
    /**
     * The exact internal error message that occured, maybe something
     * like a InternalServerError or IOException, HostNotFound etc.
     */
    private ZeitgeistError error;
    
    /**
     * The URL of the remote upload.
     */
    private String url;

    /**
     * Construct error by message.
     * @param message
     */
    public RemoteError(String message) {
        super(message);
    }

    /**
     * Construct by json primitive map.
     * @param jsonObject
     */
    public RemoteError(Map<String, ?> jsonObject) {
        super(jsonObject);

        this.error = new ZeitgeistError((Map<String, ?>)jsonObject.get("error"));
        this.url = (String)jsonObject.get("url");
    }

    /**
     * The URL of the remote upload.
     * @return URL
     */
    public String getUrl() {
        return this.url;
    }
    
    /**
     * The exact internal error message that occured, maybe something
     * like a InternalServerError or IOException, HostNotFound etc.
     * @return error object
     */
    public ZeitgeistError getInnerError() {
        return error;
    }
}

