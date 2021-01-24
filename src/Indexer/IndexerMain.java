package Indexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IndexerMain {

    public static int THREAD_NO = 5;

    public static Hashtable<String, Hashtable<String, Integer>> Dictionary = new Hashtable<>();
    public static LinkedList<String> banglaBivokti = new LinkedList<>();
    public static LinkedList<String> banglaStopWords = new LinkedList<>();
    public static Long indexedFileNumber = 0L;
    public static Long totalFileNumber = 0L;

    public static void main(String[] args) {

        System.out.println("Starting Indexer With Digram.\nPlease wait loading files...");
        deserializer();

        System.out.println("Starting from file no : " + indexedFileNumber);

        if((indexedFileNumber.equals(totalFileNumber)) || totalFileNumber.equals(0L)) {
            System.out.println("All available file has been indexed.\n" +
                    "Please crawl the web and parse more file.\n" +
                    "Exiting...");
            return;
        }

        Lock lockIndexer = new ReentrantLock();

        IndexerThread[] indexerThread = new IndexerThread[THREAD_NO];

        for (int i = 0; i < THREAD_NO; i++) {
            indexerThread[i] = new IndexerThread(lockIndexer);
        }

        for (int i = 0; i < THREAD_NO; i++) {
            try {
                indexerThread[i].join();
            } catch (InterruptedException e) {
                System.out.println(">> Error joining threads in Indexer Main : " + e.getMessage());
            }
        }
    }

    // deserialize "Indexed File Number", "Dictionary"& "Bivokti"
    private static void deserializer() {
        FileInputStream fileIn = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileIn = new FileInputStream(System.getProperty("user.dir")+"/assets/indexedFileNumber.txt");
            objectInputStream = new ObjectInputStream(fileIn);
            indexedFileNumber =  objectInputStream.readLong();

            fileIn = new FileInputStream(System.getProperty("user.dir")+"/assets/parsedFileNumber.txt");
            objectInputStream = new ObjectInputStream(fileIn);
            totalFileNumber =  objectInputStream.readLong();

            fileIn = new FileInputStream(System.getProperty("user.dir")+"/assets/Bivokti.txt");
            Scanner scanFile = new Scanner(fileIn);
            while (scanFile.hasNext()) {
                banglaBivokti.add(scanFile.nextLine());
            }
            scanFile.close();

            fileIn = new FileInputStream(System.getProperty("user.dir")+"/assets/BanglaStopWords.txt");
            scanFile = new Scanner(fileIn);
            while (scanFile.hasNext()) {
                banglaStopWords.add(scanFile.nextLine());
            }
            scanFile.close();

            if(indexedFileNumber > 0) {
                fileIn = new FileInputStream(System.getProperty("user.dir")+"/assets/Dictionary.ser");
                objectInputStream = new ObjectInputStream(fileIn);
                Dictionary = ((Hashtable<String, Hashtable<String, Integer>>) objectInputStream.readObject());
            }

        } catch (IOException i) {
            System.out.println(">> IO Exception 1 in CrawlResources Deserializer : " + i.getMessage());
        } catch (ClassNotFoundException c) {
            System.out.println(">> ClassNotFound Exception in Indexer Main Deserializer : " + c.getMessage());
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (fileIn != null) {
                    fileIn.close();
                }
            } catch (IOException e) {
                System.out.println(">> IO Exception 2 in Indexer Main Deserializer : " + e.getMessage());
            }
        }
    }

}
