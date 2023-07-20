import java.util.ArrayList;
import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;

public class Core{

	public static int noThreads = 0;
	public static final int MAX_NO_THREADS = 15; // minimum is THREADS_INCR * 2
	public static final int THREADS_INCR = 3; // minimum is 1
	public static int depth=0;
	public static String inputUrl;
	public static List<Link> links;
	public static int best1;
	public static long min1;
	public static boolean maxThreads = false;
	/**
	 * The function called from the Gui with 2 inputs, String initUrl and int initDepth.
	 * It uses the other classes and methods to finally fill the links list
	 * with the appropriate link objects as well as figures out the
	 * optimal threads count and the minimum execution time.
	 */
    public static void initiate(String initUrl, int initDepth) throws InterruptedException, MalformedURLException{
        inputUrl = initUrl;
        depth = initDepth;
        
        /**
         * Variables used to find out the optimum number of threads and minimum time.
         */
        min1 = 1;
        best1=0;
    	long min2 = 0, min3, start, time;
    	int best2=0;

    	links = new ArrayList<Link>();
    	List<Thread> threads = new ArrayList<>();
    	List<Link> temp = new ArrayList<>();
    	Link tempLink = new Link();
    	while(!maxThreads && (noThreads<=THREADS_INCR || (min1>min2 && noThreads<MAX_NO_THREADS))) {
    		min3=999999999;
    		best1 = best2;
    		for(int i=0; i<THREADS_INCR && !maxThreads; i++) {
    			noThreads++;
    			links.clear();
		        threads.clear();
		        URL u = new URL(inputUrl);

		        start = System.currentTimeMillis();
		        temp.clear();
		        tempLink.url = u.toString();
		        temp.add(tempLink);
		        IntegrityChecker.checkUrl(temp, depth, threads, links);
		        for(Thread thread: threads) {
		        	thread.join();
		        }
		        time = System.currentTimeMillis()-start;
		        
		        if(time<min3) {
		        	min3 = time;
		        	best2 = noThreads;
		        }
		        System.out.println("Threads: "+noThreads+"\nTime: "+String.format("%.03f",(time/1000.000)));
		        System.out.println("Number of links: " + links.size() + "\n");

    		}
    		min1 = min2;
    		min2 = min3;
    	}
    	if (min1>min2 || best1==0) {
    		min1 = min2;
    		best1 = best2;
    	}
    	
    	System.out.println("The optimal number of threads: " + best1 + "\nThe minimum execution time: " + String.format("%.03f",(min1/1000.000)) + "s\n\n");

    	noThreads = 0;
    	maxThreads = false;
    	
    }
}