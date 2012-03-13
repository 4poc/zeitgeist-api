package li.zeitgeist.api;

import java.util.List;

import li.zeitgeist.api.error.*;

public class Testificus {

    public static void main(String[] args) {
        
        ZeitgeistApi zg = new ZeitgeistApi("http://zeitgeist.li/");

        try {
            List<Item> items = zg.listByTag("comic");
            for (Item item : items) {
                System.out.println(String.valueOf(item.getId()));
            }
            List<Tag> tags = zg.searchTags("lou");
            for (Tag tag : tags) {
                System.out.println("Tag: " + tag.getName());
            }
        }
        catch (ZeitgeistError e) {
            System.err.println("An Error Occured: " + e.getError());
            System.err.println("Type: " + e.getType());
        }

    }

}
