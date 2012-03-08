package li.zeitgeist.api;

import li.zeitgeist.api.error.*;

import java.io.*;
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
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class ZeitgeistApi {
    private AbstractHttpClient client;
    private String baseURL;
    private String email = null;
    private String apiSecret = null;

    public ZeitgeistApi(String baseURL) {
        this.baseURL = baseURL;
        this.client = new DefaultHttpClient();
    }

    public ZeitgeistApi(String baseURL, String email, String apiSecret) {
        this.baseURL = baseURL;
        this.email = email;
        this.apiSecret = apiSecret;
        this.client = new DefaultHttpClient();
    }

    public Item createByFile(File file)
      throws ZeitgeistError {
        return createByFile(file, ""); 
    }

    public Item createByFile(File file, String tags)
      throws ZeitgeistError {
        return createByFile(file, tags, false); 
    }

    public Item createByFile(File file, List<String> tags, boolean announce)
      throws ZeitgeistError {
        return createByFile(file, Utils.join(tags.toArray(new String[0]), ","), announce); 
    }

    public Item createByFile(File file, String tags, boolean announce)
      throws ZeitgeistError {
        List<File> files = new Vector<File>();
        files.add(file);
        return createByFiles(files, tags, announce).get(0); 
    }

    public List<Item> createByFiles(List<File> files)
      throws ZeitgeistError {
        return createByFiles(files, ""); 
    }

    public List<Item> createByFiles(List<File> files, String tags)
      throws ZeitgeistError {
        return createByFiles(files, tags, false); 
    }

    public List<Item> createByFiles(List<File> files, List<String> tags, boolean announce)
      throws ZeitgeistError {
        return createByFiles(files, Utils.join(tags.toArray(new String[0]), ","), announce); 
    }

    public List<Item> createByFiles(List<File> files, String tags, boolean announce)
      throws ZeitgeistError {
        
        MultipartEntity entity = new MultipartEntity();
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
            items.add(new Item((Map<String, ?>)jsonObject.get("item")));
        }

        return items;
    }

    public Item createByUrl(String url)
      throws ZeitgeistError {
        return createByUrl(url, ""); 
    }

    public Item createByUrl(String url, String tags)
      throws ZeitgeistError {
        return createByUrl(url, tags, false); 
    }

    public Item createByUrl(String url, List<String> tags, boolean announce)
      throws ZeitgeistError {
        return createByUrl(url, Utils.join(tags.toArray(new String[0]), ","), announce); 
    }

    public Item createByUrl(String url, String tags, boolean announce)
      throws ZeitgeistError {
        List<String> urls = new Vector<String>();
        urls.add(url);
        return createByUrls(urls, tags, announce).get(0); 
    }

    public List<Item> createByUrls(List<String> urls)
      throws ZeitgeistError {
        return createByUrls(urls, ""); 
    }

    public List<Item> createByUrls(List<String> urls, String tags)
      throws ZeitgeistError {
        return createByUrls(urls, tags, false); 
    }

    public List<Item> createByUrls(List<String> urls, List<String> tags, boolean announce)
      throws ZeitgeistError {
        return createByUrls(urls, Utils.join(tags.toArray(new String[0]), ","), announce); 
    }

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
            items.add(new Item(itemObject));
        }

        return items;
    }

    public Item item(int id)
      throws ZeitgeistError {
        Map<String, ?> jsonObject = getRequest("/" + String.valueOf(id));

        Item item = new Item((Map<String, ?>)jsonObject.get("item"));

        return item;
    }

    public List<Item> list()
      throws ZeitgeistError {
        return list(-1, -1);
    }

    public List<Item> listBefore(int before)
      throws ZeitgeistError {
        return list(before, -1);
    }

    public List<Item> listAfter(int after)
      throws ZeitgeistError {
        return list(-1, after);
    }

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
            items.add(new Item(itemObject));
        }

        return items;
    }

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

    public Item update(int id, String[] addTags, String[] delTags)
      throws ZeitgeistError {
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("id", String.valueOf(id)));
        postData.add(new BasicNameValuePair("add_tags", Utils.join(addTags, ",")));
        postData.add(new BasicNameValuePair("del_tags", Utils.join(delTags, ",")));

        Map<String, ?> jsonObject = postRequest("/update", createEntityByNameValueList(postData));

        Item item = new Item((Map<String, ?>)jsonObject.get("item"));

        return item;
    }

    public int delete(int id)
      throws ZeitgeistError {
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("id", String.valueOf(id)));
        Map<String, ?> jsonObject = postRequest("/delete", createEntityByNameValueList(postData));
        return ((Double)jsonObject.get("id")).intValue();
    }

    public int upvote(int id)
      throws ZeitgeistError {
        return upvote(id, false);
    }

    public int upvote(int id, boolean remove)
      throws ZeitgeistError {
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("id", String.valueOf(id)));
        postData.add(new BasicNameValuePair("remove", remove ? "true" : "false"));
        Map<String, ?> jsonObject = postRequest("/upvote", createEntityByNameValueList(postData));
        return ((Double)jsonObject.get("upvotes")).intValue();
    }

    private HttpEntity createEntityByNameValueList(List<NameValuePair> postData) 
      throws ZeitgeistError {
        try {
            return new UrlEncodedFormEntity(postData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ZeitgeistError("UnsupportedEncoding: " + e.getMessage());
        }
    }

    private Map<String, ?> postRequest(String query, HttpEntity entity)
      throws ZeitgeistError {
        Map<String, ?> jsonObject = null;

        HttpPost request = new HttpPost(this.baseURL + query);
        setHeaders(request);

        request.setEntity(entity);

        jsonObject = executeRequest(request);

        return jsonObject;
    }

    private Map<String, ?> getRequest(String query)
      throws ZeitgeistError {
        Map<String, ?> jsonObject = null;

        HttpGet request = new HttpGet(this.baseURL + query);
        setHeaders(request);
        jsonObject = executeRequest(request);

        return jsonObject;
    }

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
                    error = new CreateItemError(jsonObject);
                }
                else if (false) {
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

    private void setHeaders(HttpRequestBase request) {
        request.setHeader("Accept", "application/json");
        if (this.email != null && this.apiSecret != null) {
            request.setHeader("X-API-Auth", this.email + "|" + this.apiSecret);
        }
    }

    private Map<String, ?> parseJson(String jsonString)
      throws ZeitgeistError {
      System.out.println(jsonString);
        Map<String, ?> json = null;
        try {
            json = new Gson().fromJson(jsonString, Map.class);
        } catch (JsonParseException e) {
            throw new ZeitgeistError("JsonParseException: " + e.getMessage());
        }

        return json;
    }

    public String getBaseUrl() {
        return baseURL;
    }
}

