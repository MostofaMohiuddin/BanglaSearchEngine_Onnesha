package WebCrawlerAndParser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class CrawlerThread extends Thread {

    public static final int MAX_DEPTH = 100;
    private String visitingLink;

    public CrawlerThread() {
        this.start();
    }

    @Override
    public void run() {

        int currentDepth = 0;

        do {
            synchronized (CrawlerResources.linksToVisit) {
                while(CrawlerResources.linksToVisit.isEmpty()){
                    synchronized (CrawlerResources.linksToVisit) {
                        try {
                            System.out.println("--->>>  In wait() of "+this.getName());
                            CrawlerResources.linksToVisit.wait();
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }

                visitingLink = getVisitingLink();
            }

            if(visitingLink == null){
                break;
            }

            if(visitingLink.contains(".pdf") || visitingLink.contains(".jpg") || visitingLink.contains(".png")) {
                System.out.println("Cannot read \" " + visitingLink + " \".\n" +
                        "Skipping Link...");
                continue;
            }

            try{

                synchronized (CrawlerResources.linksVisited) {
                    System.out.println(this.getName()+">");
                    System.out.println("Visiting: "+ visitingLink);
                }

                boolean isCrawled = crawlThisLink(visitingLink);
                if(isCrawled){
                    synchronized (CrawlerResources.linksVisited) {
                        CrawlerResources.linksVisited.add(visitingLink);
                        System.out.println(this.getName()+">");
                        System.out.println("Visited: "+(CrawlerResources.parsedFileNumber)+": "+visitingLink);
                        currentDepth++;
                    }
                }else{
                    System.out.println("Connection failed in: "+this.getName());
                }

            }catch(Exception e){
                System.out.println("In catch of do-while loop of "+ this.getName() +". Exiting...\n" +
                        e.getMessage());
                return;
            }

            if((currentDepth % 100) == 0 && currentDepth > 0){
                System.out.println(this.getName()+" : " + currentDepth);

                synchronized (CrawlerResources.linksToVisit) {
                    CrawlerResources.serializeLinksToVisit();
                }

                synchronized (CrawlerResources.linksVisited) {
                    CrawlerResources.serializeVisitedLinks();
                    CrawlerResources.serializeParsedFileNumber();
                }

                System.gc();

                System.out.println("Serialized in Thread: "+this.getName());

                if(currentDepth == MAX_DEPTH) {
                    return;
                }

            }

        } while (!CrawlerResources.linksToVisit.isEmpty());
    }

    private boolean crawlThisLink(String visitingLink) {

        URL url;
        URLConnection urlConnection;
        BufferedReader urlReader;

        //Establishing connection __KAI
        try {
            url = new URL(visitingLink);
            urlConnection = url.openConnection();
            urlConnection.connect();
        } catch (MalformedURLException e) {
            System.out.println(">> Malformed url exception! Connection not found. Exiting...");
            return false;
        } catch (IOException e) {
            System.out.println(">> IO exception! Connection not found. Exiting...");
            return false;
        }

        //Getting buffered reader __KAI
        try {
            urlReader = new BufferedReader( new InputStreamReader(
                            urlConnection.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            System.out.println(">> Buffer Reading Error!");
            return false;
        }

        //collects links from the visitingLink and saves its text in a file __KAI
        if (urlReader != null) {
            LinkedList<String> words;
            LinkedList<String> links;

            StringBuffer htmlReader = new StringBuffer();
            String htmlFile, tempLine;

            try {
                while ((tempLine = urlReader.readLine())!=null){
                    htmlReader.append(tempLine + "\n");
                }
            } catch (IOException e) {
                System.out.println(">> Error reading HTML file.");
            }

            htmlFile = htmlReader.toString();

            words = CustomParser.getWords(visitingLink, htmlFile);

            if(words.size() >= 5){
                links = CustomParser.getLinks(url, htmlFile);
                synchronized (CrawlerResources.linksToVisit) {
                    CrawlerResources.linksToVisit.addAll(links);
                    CrawlerResources.linksToVisit.notifyAll();
                }
                synchronized (CrawlerResources.parsedFileNumber) {
                    serializeTextsOfHTML(words);
                }
            }
        }

        return true;
    }

    //returns an new link and removes it from linksToVisit __KAI
    private String getVisitingLink() {

        String tempLink = CrawlerResources.linksToVisit.removeFirst();

        while (CrawlerResources.linksVisited.contains(tempLink) && !CrawlerResources.linksToVisit.isEmpty()) {
            tempLink = CrawlerResources.linksToVisit.removeFirst();
            if(tempLink.contains("wikipedia") && !tempLink.contains("bn.")) {
                //skipping links of other languages
                CrawlerResources.linksVisited.add(tempLink);
            }
            if(tempLink.contains("#")) {
                //skipping links with subtext and self-reference
                CrawlerResources.linksVisited.add(tempLink);
            }
        }

        if(CrawlerResources.linksVisited.contains(tempLink)){
            return null;
        }

        return tempLink;

    }

    // serialize "TextsOfHTML" __KAI
    //File format :
    //  -> link of the HTML file
    //  -> Collected Bengali words from it...

    public static void serializeTextsOfHTML(LinkedList<String> formattedTexts) {

        FileOutputStream fileOut = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            CrawlerResources.parsedFileNumber++;
            fileOut = new FileOutputStream(System.getProperty("user.dir") +
                    "/assets/ParsedFiles/file_" + CrawlerResources.parsedFileNumber + ".ser");
            objectOutputStream = new ObjectOutputStream(fileOut);
            objectOutputStream.writeObject(formattedTexts);

        } catch (IOException i) {
            System.out.println(">> Error writing text of HTML");
        }finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
