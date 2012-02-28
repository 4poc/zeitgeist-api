package li.zeitgeist.api;

import java.io.File;

import li.zeitgeist.api.error.*;

public class Testificus {

	public static void main(String[] args) {
		
		ZeitgeistApi zg = new ZeitgeistApi("http://127.0.0.1:4567/", "apoc@sixserv.org", "MSHFf2pccTiKP5XvJa7eSNRD1CPZyAHtn0owjSXC59eQ0kIY");

        try {
            Item item = zg.createByFile(new File("/home/apoc/media/pictures/6936_dfdd.jpeg"));

            // System.out.println("Item uploaded: url = " + item.getImage().getImage());
            // System.out.println("Mimetype = " + item.getMimetype());

            // zg.list(1);

            // Item item = zg.item(19351);
            // System.out.println(item.getCreated().toString());

            zg.item(44);
            zg.upvote(44);
            zg.item(44);
            zg.upvote(44, true);
            zg.item(44);
            /*
            Item item = zg.item(1);
            System.out.println("image url: " + item.getImage().getImage());
            System.out.println(Utils.join(item.getTagNames(), ", "));

            zg.update(1, "foo, bar");
            zg.update(1, "-foo, -bar");



            zg.createByUrl("http://i2.ytimg.com/vi/qxKEG5-OQz4/default.jpg");

            */
        }
        catch (ZeitgeistError e) {
            System.err.println("An Error Occured: " + e.getError());
            System.err.println("Type: " + e.getType());
        }


	}

}
