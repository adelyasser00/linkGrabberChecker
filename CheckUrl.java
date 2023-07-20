import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.jsoup.Jsoup;

public class CheckUrl implements Runnable {
    String url;
    Link temp;
    String tempUrl;
    List < Link > links;
    List < Integer > pos;
    URL fileUrl;
    int responseCode;

    public CheckUrl(List < Link > links, List < Integer > pos, String url) {
        this.links = links;
        this.pos = pos; // integer list of positions of links in the links list
        this.url = url; // the domain from which the links were fetched
    }

    @Override
    public void run() {
        int j = 0;
        while (j < pos.size()) {
            try {
                /**
                 * Get the link and if it is a relative link add the domain to it.
                 */
                tempUrl = links.get(pos.get(j)).url;
                if (!tempUrl.startsWith("http")) {
                    if (!tempUrl.startsWith("/")) {
                        tempUrl = "/" + tempUrl;
                    }
                    tempUrl = url + tempUrl;
                }

                /**
                 * Attempt to connect to the link and get document.
                 * If successful set replace the link object in the list with
                 * an exact copy but with the valid field set to true.
                 */
                Jsoup.connect(tempUrl).get();
                temp = new Link();
                temp = links.get(pos.get(j));
                temp.valid = true;
                links.set(pos.get(j), temp);
            } catch (Exception e) {
                /**
                 * Attempt to get a response code.
                 * If successful set replace the link object in the list with
                 * an exact copy but with the valid field and the file field set to true.
                 * Otherwise, the object will have both fields set to false.
                 */
                try {
                    fileUrl = new URL(tempUrl);
                    HttpURLConnection huc = (HttpURLConnection) fileUrl.openConnection();
                    responseCode = huc.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        temp = new Link();
                        temp = links.get(pos.get(j));
                        temp.valid = true;
                        temp.file = true;
                    } else {
                        temp = new Link();
                        temp = links.get(pos.get(j));
                        temp.valid = false;
                    }
                } catch (Exception a) {
                    temp = new Link();
                    temp = links.get(pos.get(j));
                    temp.valid = false;
                }
                links.set(pos.get(j), temp);
            }
            j++;
        }

    }
}