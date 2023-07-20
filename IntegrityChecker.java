import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegrityChecker {
    public static void checkUrl(List < Link > url, int depth, List < Thread > threads, List < Link > links) throws InterruptedException, MalformedURLException {
        GetLinks getLinks = new GetLinks();
        URL tempUrl;
        String hostUrl = null;
        List < Link > oldLinks = new ArrayList < Link > (links);
        int k = 0;

        for (k = 0; k < url.size(); k++) {

            /**
             *  If the link is a file, do not attempt to extract links
             *  from it and skip it.
             */
            if (url.get(k).file) { // If the link is a file, do not attempt to extract links from it and skip it.
                continue;
            }
            tempUrl = new URL(url.get(k).url);
            hostUrl = tempUrl.getProtocol() + "://" + tempUrl.getHost();
            getLinks.returnLinks(url.get(k).url, links);

            int count = links.size();
            int linksPerThread = count / Core.noThreads;
            /**
             * If the number of links is less than the number of threads
             * then start a thread for each link.
             */
            if (Core.noThreads == 1 && count == 0) {
                Core.maxThreads = true;
                return;
            }
            if (count == Core.noThreads) {
                Core.maxThreads = true;
            }
            if (count % Core.noThreads != 0) {
                /**
                 * If the number of links is not divisible by the number of threads
                 * then distribute the links using count/Core.noThreads for each thread except the last
                 * where the last thread gets assigned count/Core.noThreads + the remainder.
                 */
                int i;
                for (i = 0; i < Core.noThreads - 1; i++) {
                    Runnable urlCheck = new CheckUrl(links, IntStream.rangeClosed(i * linksPerThread, i * linksPerThread + linksPerThread - 1).boxed().collect(Collectors.toList()), hostUrl);
                    Thread t = new Thread(urlCheck);
                    t.start();
                    threads.add(t);
                }
                Runnable urlCheck = new CheckUrl(links, IntStream.rangeClosed(i * linksPerThread, i * linksPerThread + linksPerThread - 1 + count % Core.noThreads).boxed().collect(Collectors.toList()), hostUrl);
                Thread t = new Thread(urlCheck);
                t.start();
                threads.add(t);
            } else {
                /**
                 * If the number of links is divisible by the number of threads
                 * then distribute count/Core.noThreads for each thread.
                 */
                for (int i = 0; i < Core.noThreads; i++) {
                    Runnable urlCheck = new CheckUrl(links, IntStream.rangeClosed(i * linksPerThread, i * linksPerThread + linksPerThread - 1).boxed().collect(Collectors.toList()), hostUrl);
                    Thread t = new Thread(urlCheck);
                    t.start();
                    threads.add(t);
                }
            }

            /**
             * Wait for all the threads to finish before proceeding.
             */
            for (Thread thread: threads) {
                thread.join();
            }
        }
        if (depth == 0) {
            return;
        } else if (k != 0) {
            /**
             * Add the difference between the oldLinks list and the new list
             * to our links list.
             */
            List < Link > currLinks = new ArrayList < Link > (links);
            currLinks.removeAll(oldLinks);
            url.clear();
            for (Link link: currLinks) {
                if (link.valid) {
                    if (!link.url.startsWith("http")) {
                        link.url = hostUrl + link.url;
                    }
                    url.add(link);
                }
            }
            /**
             * Call checkUrl recursively with the new urls, and the depth-1.
             */
            checkUrl(url, depth - 1, threads, links);
        }
    }
}