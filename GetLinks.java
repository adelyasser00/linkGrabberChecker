import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.*;

public class GetLinks {

    public void returnLinks(String url, List < Link > links) {
        try {
            /**
             * Attempt to connect to the link and get document.
             * Get the elements with the tag "a", and add them to
             * the links list.
             */
            URL tempUrl = new URL(url);
            String dom = tempUrl.getProtocol() + "://" + tempUrl.getHost();
            Document document = Jsoup.connect(url).get();
            Elements allLinks = document.getElementsByTag("a");
            for (Element link: allLinks) {
                Link temp = new Link();
                temp.url = link.attr("href");
                temp.text = link.text();
                if (!temp.url.startsWith("http")) {
                    temp.domain = dom;
                }
                links.add(temp);
            }

        } catch (Exception e) {
            /**
             * In case of failure when attempting to connect to the link
             * and get the document, do nothing. I.E. add no links to the list.
             */
        }
    }
}