package WebCrawlerAndParser;

import java.util.Scanner;

public class CrawlerMain {

    public static final String SEED_BANGLA_WIKI = "https://bn.wikipedia.org/wiki/" +
            "%E0%A6%AA%E0%A7%8D%E0%A6%B0%E0%A6%A7%E0%A6%BE%E0%A6%A8_%E0%A6%AA%E0%A6%BE%E0%A6%A4%E0%A6%BE";

    public static final String SEED_PROTHOM_ALO = "http://www.prothomalo.com/todays-paper/2018-01-01";

    public static final String SEED_KALER_KONTHO = "http://www.ekalerkantho.com/home/page/2018-01-01/1";


    public static final int THREAD_NUMBER = 8;

    public static void main(String[] args) {

        System.out.println("Starting crawler.\nPlease wait loading files...\n");

        //Loads all resources __KAI
        CrawlerResources.deserializer();

        //Checks if crawler is out of links __KAI
        if(CrawlerResources.linksToVisit.isEmpty() && CrawlerResources.linksVisited.contains(SEED_BANGLA_WIKI)){
            System.out.println("No more links available to crawl.\n");

            Scanner scan = new Scanner(System.in);
            String newSeed;

            do{
                System.out.println("Please provide another seed. \n" +
                        "or type \"Exit\" to exit crawler.");
                newSeed = scan.nextLine();
                if (newSeed.equalsIgnoreCase("Exit")) {
                    System.out.println("Exiting...");
                    return;
                } else if (CrawlerResources.linksVisited.contains(newSeed)) {
                    System.out.println("\nInvalid seed.\n" +
                            "This link has been crawled");
                }
                else {
                    System.out.println(newSeed + " - seed received.\n" +
                            "Starting crawler.\n");

                    CrawlerResources.linksToVisit.add(newSeed);
                }
            } while(CrawlerResources.linksVisited.contains(newSeed));

        } else {

            if(CrawlerResources.linksToVisit.isEmpty()) {
                CrawlerResources.linksToVisit.add(SEED_BANGLA_WIKI);
                CrawlerResources.linksToVisit.add(SEED_PROTHOM_ALO);
                CrawlerResources.linksToVisit.add(SEED_KALER_KONTHO);
            }
        }

        // Initializing and joining all threads __KAI
        CrawlerThread[] t = new CrawlerThread[THREAD_NUMBER];

        for(int i=0; i<THREAD_NUMBER; i++){
            t[i] = new CrawlerThread();
        }

        for(int i=0; i<THREAD_NUMBER; i++){

            try {
                t[i].join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

        }


        System.out.println("Crawling ended.\nExiting...");

    }
}
