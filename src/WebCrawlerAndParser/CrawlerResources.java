package WebCrawlerAndParser;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;

public class CrawlerResources {

    public static LinkedList<String> linksToVisit;
    public static HashSet<String> linksVisited;
    public static Long parsedFileNumber;

    public static void deserializer() { // deserialize "linksToVisit", "linksVisited", "parsedFileNumber" __KAI

        linksToVisit = new LinkedList<>();
        linksVisited = new HashSet<>();
        parsedFileNumber = 0L;

        //adding deserializer__NC
        FileInputStream fileIn = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileIn = new FileInputStream(System.getProperty("user.dir")+"/assets/parsedFileNumber.txt");
            objectInputStream = new ObjectInputStream(fileIn);
            parsedFileNumber =  objectInputStream.readLong();

            if(parsedFileNumber > 0) {
                fileIn = new FileInputStream(System.getProperty("user.dir")+"/assets/LinksToVisit.ser");
                objectInputStream = new ObjectInputStream(fileIn);
                linksToVisit = ((LinkedList<String>) objectInputStream.readObject());

                fileIn = new FileInputStream(System.getProperty("user.dir")+"/assets/LinksVisited.ser");
                objectInputStream = new ObjectInputStream(fileIn);
                linksVisited = ((HashSet<String>) objectInputStream.readObject());

            }

        } catch (IOException i) {
            System.out.println(">> IO Exception 1 in CrawlResources Deserializer : " + i.getMessage());
        } catch (ClassNotFoundException c) {
            System.out.println(">> ClassNotFound Exception in CrawlResources Deserializer : " + c.getMessage());
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (fileIn != null) {
                    fileIn.close();
                }
            } catch (IOException e) {
                System.out.println(">> IO Exception 2 in CrawlResources Deserializer : " + e.getMessage());
            }
        }
        //end deserializer___NC
    }

    public static void serializeLinksToVisit() { // serialize "linksToVisit" __KAI
        //adding serializeLinksToVisit__NC
        FileOutputStream fileOut = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOut = new FileOutputStream(System.getProperty("user.dir")+"/assets/LinksToVisit.ser");
            objectOutputStream = new ObjectOutputStream(fileOut);
            objectOutputStream.writeObject(linksToVisit);

        } catch (IOException i) {
            System.out.println(i.getMessage());
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
        //end serializeLinksToVisit__NC
    }

    public static void serializeVisitedLinks() { // serialize "linksVisited" __KAI
        //adding serializeVisitedLinks__NC
        FileOutputStream fileOut = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOut = new FileOutputStream(System.getProperty("user.dir")+"/assets/LinksVisited.ser");
            objectOutputStream = new ObjectOutputStream(fileOut);
            objectOutputStream.writeObject(linksVisited);

        } catch (IOException i) {
            System.out.println(i.getMessage());
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
        //end serializeVisitedLinks__NC
    }

    public static void serializeParsedFileNumber() { // serialize "parsedFileNumber" __KAI
        //adding serializeParsedFileNumber__NC
        FileOutputStream fileOut = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOut = new FileOutputStream(System.getProperty("user.dir")+"/assets/parsedFileNumber.txt");
            objectOutputStream = new ObjectOutputStream(fileOut);
            objectOutputStream.writeLong(parsedFileNumber);

        } catch (IOException i) {
            System.out.println(i.getMessage());
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
    //end serializeParsedFileNumber__NC
}
