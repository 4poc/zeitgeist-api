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

import li.zeitgeist.api.error.*;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Zeitgeist API methods.
 * 
 * Uses a HTTP interface to communicate with a zeitgeist
 * installation.
 */
public class ZeitgeistApi {
    /**
     * Apache HTTP Client instance.
     */
    private AbstractHttpClient client;
    /**
     * Base URL of the zeitgeist installation.
     */
    private String baseUrl;
    /**
     * User eMail for authentication.
     */
    private String email = null;
    /**
     * User API Secret used for authentication.
     */
    private String apiSecret = null;

    /**
     * Construct a API instance with the provided baseUrl.
     * @param baseUrl
     */
    public ZeitgeistApi(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = new DefaultHttpClient();
    }

    /**
     * Construct a API instance with provided base URL and authentication.
     * @param baseUrl
     * @param email
     * @param apiSecret
     */
    public ZeitgeistApi(String baseUrl, String email, String apiSecret) {
        this.baseUrl = baseUrl;
        this.email = email;
        this.apiSecret = apiSecret;
        this.client = new DefaultHttpClient();
    }

    /**
     * Upload image as a new item.
     * @param file
     * @return the created item instance
     * @throws ZeitgeistError
     */
    public Item createByFile(File file)
      throws ZeitgeistError {
        return createByFile(file, ""); 
    }

    /**
     * Upload image as a new item, assign with tags provided.
     * @param file File instance
     * @param tags Comma seperated list of tags.
     * @return the created item instance
     * @throws ZeitgeistError
     */
    public Item createByFile(File file, String tags)
      throws ZeitgeistError {
        return createByFile(file, tags, false); 
    }

    /**
     * Upload image as a new item, assign with tags provided and announce.
     * @param file File instance
     * @param tags Array of tag strings.
     * @param announce True if the item should be announced in irc.
     * @return the created item instance
     * @throws ZeitgeistError
     */
    public Item createByFile(File file, List<String> tags, boolean announce)
      throws ZeitgeistError {
        return createByFile(file, Utils.join(tags.toArray(new String[0]), ","), announce); 
    }

    /**
     * Upload image as a new item, assign with tags provided and announce.
     * @param file File instance
     * @param tags Comma seperated list of tags.
     * @param announce True if the item should be announced in irc.
     * @return the created item instance
     * @throws ZeitgeistError
     */
    public Item createByFile(File file, String tags, boolean announce)
      throws ZeitgeistError {
        List<File> files = new Vector<File>();
        files.add(file);
        return createByFiles(files, tags, announce).get(0); 
    }

    /**
     * Upload multiple files at once.
     * @param files
     * @return list of created items
     * @throws ZeitgeistError
     */
    public List<Item> createByFiles(List<File> files)
      throws ZeitgeistError {
        return createByFiles(files, ""); 
    }

    /**
     * Upload multiple files at once with tags.
     * @param files
     * @param tags Comma seperated list of tags.
     * @return list of created items
     * @throws ZeitgeistError
     */
    public List<Item> createByFiles(List<File> files, String tags)
      throws ZeitgeistError {
        return createByFiles(files, tags, false); 
    }

    /**
     * Upload multiple files at once with tags and announce.
     * @param files
     * @param tags Array of tag strings.
     * @param announce True if the item should be announced in irc.
     * @return list of created items
     * @throws ZeitgeistError
     */
    public List<Item> createByFiles(List<File> files, List<String> tags, boolean announce)
      throws ZeitgeistError {
        return createByFiles(files, Utils.join(tags.toArray(new String[0]), ","), announce); 
    }

    /**
     * Upload multiple files at once with tags and announce.
     * @param files
     * @param tags Comma seperated list of tags.
     * @param announce True if the item should be announced in irc.
     * @return list of created items
     * @throws ZeitgeistError
     */
    public List<Item> createByFiles(List<File> files, String tags, boolean announce)
      throws ZeitgeistError {
        return createByFiles(files, tags, announce, null);
    }

    public List<Item> createByFiles(List<File> files, String tags, 
            boolean announce, OnProgressListener listener) 
            throws ZeitgeistError {
        MultipartEntity entity;
        if (listener == null) {
            entity = new MultipartEntity();
        }
        else {
            entity = new MultipartEntityWithProgress(listener);
        }
        
        for (File file : files) {
            entity.addPart("image_upload[]", new FileBody(file));
        }

        try {
            entity.addPart("tags", new StringBody(tags));
            entity.addPart("announce", new StringBody(announce ? "true" : "false"));
        } catch (UnsupportedEncodingException e) {
            throw new ZeitgeistError("UnsupportedEncoding: " + e.getMessage());
        }
        
        Map<String, ?> jsonObject = postRequest("/new", entity);

        ArrayList<Map<String, ?>> itemObjects = (ArrayList<Map<String, ?>>)jsonObject.get("items");

        List<Item> items = new Vector<Item>();
        for (Map<String, ?> itemObject : itemObjects) {
            items.add(new Item((Map<String, ?>)itemObject.get("item"), baseUrl));
        }

        return items;
    }

    public interface OnProgressListener {
        public void onProgress(long transferred);
    }
    
    // based on this idea: 
    //  http://toolongdidntread.com/android/android-multipart-post-with-progress-bar/
    private class MultipartEntityWithProgress extends MultipartEntity {
        private OnProgressListener listener;
        
        public MultipartEntityWithProgress(OnProgressListener listener) {
            this.listener = listener;
        }

        @Override
        public void writeTo(final OutputStream out) throws IOException {
            super.writeTo(new CountingOutputStream(out, this.listener));
        }

        private class CountingOutputStream extends FilterOutputStream {

            private final OnProgressListener listener;
            private long transferred;

            public CountingOutputStream(final OutputStream out,
                    final OnProgressListener listener) {
                super(out);
                this.listener = listener;
                this.transferred = 0;
            }

            public void write(byte[] b, int off, int len) throws IOException {
                out.write(b, off, len);
                this.transferred += len;
                this.listener.onProgress(this.transferred);
            }

            public void write(int b) throws IOException {
                out.write(b);
                this.transferred++;
                this.listener.onProgress(this.transferred);
            }
        }
    }
        
    /**
     * Remote upload/create by image/video/audio url.
     * @param url
     * @return the item instance created.
     * @throws ZeitgeistError
     */
    public Item createByUrl(String url)
      throws ZeitgeistError {
        return createByUrl(url, ""); 
    }

    /**
     * Remote upload/create by image/video/audio url with tags.
     * @param url
     * @param tags Comma seperated list of tags.
     * @return the item instance created.
     * @throws ZeitgeistError
     */
    public Item createByUrl(String url, String tags)
      throws ZeitgeistError {
        return createByUrl(url, tags, false); 
    }

    /**
     * Remote upload/create by image/video/audio url with tags and announce.
     * @param url
     * @param tags Array of tag strings.
     * @return the item instance created.
     * @throws ZeitgeistError
     */
    public Item createByUrl(String url, List<String> tags, boolean announce)
      throws ZeitgeistError {
        return createByUrl(url, Utils.join(tags.toArray(new String[0]), ","), announce); 
    }

    /**
     * Remote upload/create by image/video/audio url with tags and announce.
     * @param url
     * @param tags Comma seperated list of tags.
     * @return the item instance created.
     * @throws ZeitgeistError
     */
    public Item createByUrl(String url, String tags, boolean announce)
      throws ZeitgeistError {
        List<String> urls = new Vector<String>();
        urls.add(url);
        return createByUrls(urls, tags, announce).get(0); 
    }

    /**
     * Multiple remote upload/create by image/video/audio url.
     * @param urls list of URLs
     * @return array of item instances created.
     * @throws ZeitgeistError
     */
    public List<Item> createByUrls(List<String> urls)
      throws ZeitgeistError {
        return createByUrls(urls, ""); 
    }

    /**
     * Multiple remote upload/create by image/video/audio url with tags.
     * @param urls list of URLs
     * @param tags Comma seperated list of tags.
     * @return array of item instances created.
     * @throws ZeitgeistError
     */
    public List<Item> createByUrls(List<String> urls, String tags)
      throws ZeitgeistError {
        return createByUrls(urls, tags, false); 
    }

    /**
     * Multiple remote upload/create by image/video/audio url with tags and announce.
     * @param urls list of URLs
     * @param tags Array of tag strings.
     * @return array of item instances created.
     * @throws ZeitgeistError
     */
    public List<Item> createByUrls(List<String> urls, List<String> tags, boolean announce)
      throws ZeitgeistError {
        return createByUrls(urls, Utils.join(tags.toArray(new String[0]), ","), announce); 
    }

    /**
     * Multiple remote upload/create by image/video/audio url with tags and announce.
     * @param urls list of URLs
     * @param tags Comma seperated list of tags.
     * @return array of item instances created.
     * @throws ZeitgeistError
     */
    public List<Item> createByUrls(List<String> urls, String tags, boolean announce)
      throws ZeitgeistError {
        List<NameValuePair> postData = new ArrayList<NameValuePair>();

        for (String url : urls) {
            postData.add(new BasicNameValuePair("remote_url[]", url));
        }

        postData.add(new BasicNameValuePair("tags", tags));

        postData.add(new BasicNameValuePair("announce", announce ? "true" : "false"));
        
        Map<String, ?> jsonObject = postRequest("/new", createEntityByNameValueList(postData));

        ArrayList<Map<String, ?>> itemObjects = (ArrayList<Map<String, ?>>)jsonObject.get("items");

        List<Item> items = new Vector<Item>();
        for (Map<String, ?> itemObject : itemObjects) {
            items.add(new Item(itemObject, baseUrl));
        }

        return items;
    }

    /**
     * Query for a single item instance by ID.
     * @param id
     * @return the item instance
     * @throws ZeitgeistError
     */
    public Item item(int id)
      throws ZeitgeistError {
        Map<String, ?> jsonObject = getRequest("/" + String.valueOf(id));

        Item item = new Item((Map<String, ?>)jsonObject.get("item"), baseUrl);

        return item;
    }

    /**
     * Lists the newest/frontpage items.
     * @return list of item objects.
     * @throws ZeitgeistError
     */
    public List<Item> list()
      throws ZeitgeistError {
        return list(-1, -1);
    }

    /**
     * Lists items that come before a specified ID.
     * @param before ID
     * @return list of item objects.
     * @throws ZeitgeistError
     */
    public List<Item> listBefore(int before)
      throws ZeitgeistError {
        return list(before, -1);
    }

    /**
     * Lists items that come after a specified ID.
     * @param after ID
     * @return list of item objects.
     * @throws ZeitgeistError
     */
    public List<Item> listAfter(int after)
      throws ZeitgeistError {
        return list(-1, after);
    }

    /**
     * Lists items that come before or after specified IDs.
     * @param before ID (optional -1)
     * @param after ID (optional -1)
     * @return list of item objects.
     * @throws ZeitgeistError
     */
    public List<Item> list(int before, int after)
      throws ZeitgeistError {
        StringBuilder query = new StringBuilder().append("/");
        if (before >= 0 || after >= 0) {
            query.append("?");
            if (before >= 0) query.append("before=" + String.valueOf(before));
            if (after >= 0) query.append("after=" + String.valueOf(after));
        }
        Map<String, ?> jsonObject = getRequest(query.toString());
        ArrayList<Map<String, ?>> itemObjects = (ArrayList<Map<String, ?>>)jsonObject.get("items");
        
        List<Item> items = new Vector<Item>();
        for (Map<String, ?> itemObject : itemObjects) {
            items.add(new Item(itemObject, baseUrl));
        }

        return items;
    }

    /**
     * Search for tags by partial name.
     * @param query
     * @return list of tag objects.
     * @throws ZeitgeistError
     */
    public List<Tag> searchTags(String query)
            throws ZeitgeistError {
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("q", query));
        Map<String, ?> jsonObject = postRequest("/search", createEntityByNameValueList(postData));
        ArrayList<Map<String, ?>> tagObjects = (ArrayList<Map<String, ?>>)jsonObject.get("tags");

        List<Tag> tags = new Vector<Tag>();
        for (Map<String, ?> tagObject : tagObjects) {
            tags.add(new Tag(tagObject));
        }

        return tags;
    }

    /**
     * List newest items that are associated with given tag.
     * @param tag
     * @return list of item objects.
     * @throws ZeitgeistError
     */
    public List<Item> listByTag(String tag)
            throws ZeitgeistError {
        return listByTag(tag, -1, -1);
    }

    /**
     * List items that are associated with given tag and come before specified ID.
     * @param tag
     * @param before ID (optional -1)
     * @return list of item objects.
     * @throws ZeitgeistError
     */
    public List<Item> listByTagBefore(String tag, int before)
            throws ZeitgeistError {
        return listByTag(tag, before, -1);
    }

    /**
     * List items that are associated with given tag and come after specified ID.
     * @param tag
     * @param after ID (optional -1)
     * @return list of item objects.
     * @throws ZeitgeistError
     */
    public List<Item> listByTagAfter(String tag, int after)
            throws ZeitgeistError {
        return listByTag(tag, -1, after);
    }

    /**
     * List items that are associated with given tag and come after or before specified IDs.
     * @param tag
     * @param before ID (optional -1)
     * @param after ID (optional -1)
     * @return list of item objects.
     * @throws ZeitgeistError
     */
    public List<Item> listByTag(String tag, int before, int after)
            throws ZeitgeistError {
        StringBuilder query = new StringBuilder();
        try {
            query.append("/show/tag/" + URLEncoder.encode(tag, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (before >= 0 || after >= 0) {
            query.append("?");
            if (before >= 0) query.append("before=" + String.valueOf(before));
            if (after >= 0) query.append("after=" + String.valueOf(after));
        }
        Map<String, ?> jsonObject = getRequest(query.toString());
        ArrayList<Map<String, ?>> itemObjects = (ArrayList<Map<String, ?>>)jsonObject.get("items");

        List<Item> items = new Vector<Item>();
        for (Map<String, ?> itemObject : itemObjects) {
            items.add(new Item(itemObject, baseUrl));
        }

        return items;
    }
    
    /**
     * Update the tags of a item.
     * 
     * This adds or removes taggings from an item that is specified
     * by ID. Tags is a comma seperated list with tags, each tag
     * can be prefixed by + or - to specify to add or delete a tag,
     * note that + is optional due to be the default.
     * 
     * @param id
     * @param tags
     * @return the updated item.
     * @throws ZeitgeistError
     */
    public Item update(int id, String tags)
      throws ZeitgeistError {
      Vector<String> addTags = new Vector<String>();
      Vector<String> delTags = new Vector<String>();
      String[] tagsArray = tags.split(",");
      for (String tag : tagsArray) {
          tag = tag.trim();
          if (tag.charAt(0) == '-') {
              tag = tag.substring(1);
              delTags.add(tag);
          }
          else {
              if (tag.charAt(0) == '+') {
                  tag = tag.substring(1);
              }
              addTags.add(tag);
          }
      }

      return this.update(id, addTags.toArray(new String[0]), delTags.toArray(new String[0]));
    }

    /**
     * Update the tags of a item.
     * 
     * This adds or removes taggings from an item that is specified
     * by ID.
     * 
     * @param id
     * @param addTags array of tags to add
     * @param delTags array of tags to delete
     * @return the updated item.
     * @throws ZeitgeistError
     */
    public Item update(int id, String[] addTags, String[] delTags)
      throws ZeitgeistError {
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("id", String.valueOf(id)));
        postData.add(new BasicNameValuePair("add_tags", Utils.join(addTags, ",")));
        postData.add(new BasicNameValuePair("del_tags", Utils.join(delTags, ",")));

        Map<String, ?> jsonObject = postRequest("/update", createEntityByNameValueList(postData));

        Item item = new Item((Map<String, ?>)jsonObject.get("item"), baseUrl);

        return item;
    }

    /**
     * Delete a item specified by ID.
     * 
     * Only the creator (owner) or an admin can delete items.
     * 
     * @param id
     * @return the ID of the deleted item.
     * @throws ZeitgeistError
     */
    public int delete(int id)
      throws ZeitgeistError {
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("id", String.valueOf(id)));
        Map<String, ?> jsonObject = postRequest("/delete", createEntityByNameValueList(postData));
        return ((Double)jsonObject.get("id")).intValue();
    }

    /**
     * Upvote (+1) an item specified by ID.
     * @param id
     * @return number of upvotes the item has.
     * @throws ZeitgeistError
     */
    public int upvote(int id)
      throws ZeitgeistError {
        return upvote(id, false);
    }

    /**
     * Upvote (+1) an item specified by ID.
     * 
     * The remove parameter specify if the upvote should be undone/removed.
     * 
     * @param id
     * @param remove True if the upvote should be deleted.
     * @return number of upvotes the item has.
     * @throws ZeitgeistError
     */
    public int upvote(int id, boolean remove)
      throws ZeitgeistError {
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("id", String.valueOf(id)));
        postData.add(new BasicNameValuePair("remove", remove ? "true" : "false"));
        Map<String, ?> jsonObject = postRequest("/upvote", createEntityByNameValueList(postData));
        return ((Double)jsonObject.get("upvotes")).intValue();
    }

    /**
     * Creates urlencoded data from a pair list for POST requests.
     * @param postData
     * @return entity
     * @throws ZeitgeistError
     */
    private HttpEntity createEntityByNameValueList(List<NameValuePair> postData) 
      throws ZeitgeistError {
        try {
            return new UrlEncodedFormEntity(postData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ZeitgeistError("UnsupportedEncoding: " + e.getMessage());
        }
    }

    /**
     * Perform a POST request. 
     * @param query URI from url base.
     * @param entity
     * @return json map of primitives.
     * @throws ZeitgeistError
     */
    private Map<String, ?> postRequest(String query, HttpEntity entity)
      throws ZeitgeistError {
        Map<String, ?> jsonObject = null;

        HttpPost request = new HttpPost(this.baseUrl + query);
        setHeaders(request);

        request.setEntity(entity);

        jsonObject = executeRequest(request);

        return jsonObject;
    }

    /**
     * Perform a GET request. 
     * @param query URI from url base.
     * @return json map of primitives.
     * @throws ZeitgeistError
     */
    private Map<String, ?> getRequest(String query)
      throws ZeitgeistError {
        Map<String, ?> jsonObject = null;

        HttpGet request = new HttpGet(this.baseUrl + query);
        setHeaders(request);
        jsonObject = executeRequest(request);

        return jsonObject;
    }

    /**
     * Execute a HTTP request and parse the result as JSON, also unifies
     * Exceptions into the ZeitgeistError class.
     * @param request
     * @return json map of primitives.
     * @throws ZeitgeistError
     */
    private Map<String, ?> executeRequest(HttpRequestBase request) 
      throws ZeitgeistError {
        Map<String, ?> jsonObject = null;

        try {
            HttpResponse response = this.client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());
            jsonObject = parseJson(jsonString);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) { // parse json into an ZeitgeistException and throw
                ZeitgeistError error = null;
                if (((String)jsonObject.get("type")).equals("CreateItemError")) {
                    error = new CreateItemError(jsonObject, baseUrl);
                }
                else {
                    error = new ZeitgeistError(jsonObject);
                }

                throw error;
            }
        } catch (ClientProtocolException e) {
            throw new ZeitgeistError("ClientProtocolException: " + e.getMessage());
        } catch (IOException e) {
            throw new ZeitgeistError("IOException: " + e.getMessage());
        } catch (ZeitgeistError e) {
            throw e; // just passthrough
        }

        return jsonObject;
    }

    /**
     * Set required Headers for the API, json accept only and authentication.
     * @param request
     */
    private void setHeaders(HttpRequestBase request) {
        request.setHeader("Accept", "application/json");
        if (this.email != null && this.apiSecret != null &&
            this.email.length() > 0 && this.apiSecret.length() > 0) {
            request.setHeader("X-API-Auth", this.email + "|" + this.apiSecret);
        }
    }

    /**
     * Parses json by string, returns a map of primitives by string key.
     * @param jsonString
     * @return json "primitive" map
     * @throws ZeitgeistError
     */
    private Map<String, ?> parseJson(String jsonString)
      throws ZeitgeistError {
        Map<String, ?> json = null;
        try {
            json = new Gson().fromJson(jsonString, Map.class);
        } catch (JsonParseException e) {
            throw new ZeitgeistError("JsonParseException: " + e.getMessage());
        }

        return json;
    }

    /**
     * The Base URL used by this API instance.
     * @return string URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }
}

