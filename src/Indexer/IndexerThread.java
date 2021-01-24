package Indexer;

import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;

public class IndexerThread extends Thread {

    public static final int MAX_INDEX_LIMIT = 200;
    private Lock indexerLock;

    public IndexerThread(Lock lock) {
        this.indexerLock = lock;
        this.start();
    }

    @Override
    public void run() {

        LinkedList<String> wordList;
        String pageURL;
        int fileNo;
        FileInputStream fileIn = null;
        ObjectInputStream objectInputStream = null;
        int indexedFileNo = 0;

        while (IndexerMain.indexedFileNumber.intValue() < IndexerMain.totalFileNumber.intValue()) {

            indexerLock.lock();
            IndexerMain.indexedFileNumber++;
            fileNo = IndexerMain.indexedFileNumber.intValue();
            System.out.println(this.getName() + " : \n" +
                    "Indexing file no " + fileNo);
            indexerLock.unlock();


            try {
                fileIn = new FileInputStream(System.getProperty("user.dir")+
                        "/assets/ParsedFiles/file_" + fileNo + ".ser");
                objectInputStream = new ObjectInputStream(fileIn);
                wordList = (LinkedList<String>) objectInputStream.readObject();
                pageURL = wordList.removeFirst();
                IndexerWithDigram.indexSingleWords(wordList, pageURL);
                IndexerWithDigram.indexDoubleWords(wordList, pageURL);
                indexedFileNo++;
            } catch (Exception e) {
                System.out.println(">> Error in IndexerThread " + this.getName() +
                        " : " + e.getMessage());
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

            System.out.println(this.getName() + " : \n" +
                    "Completed indexing file no " + fileNo);


            if ((indexedFileNo % 100) == 0) {
                System.out.println(this.getName() + " : \n" +
                        "Completed indexing " + indexedFileNo + " files.\n");
                serializeDictionary();
                serializeIndexedFileNumber();
                if (indexedFileNo == MAX_INDEX_LIMIT) {
                    System.out.println(this.getName() + " : Exiting");
                    return;
                }
            }

            System.gc();

        }
    }

    private void serializeDictionary() {

        FileOutputStream fileOut = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            synchronized (IndexerMain.Dictionary) {
                fileOut = new FileOutputStream(System.getProperty("user.dir") + "/assets/Dictionary.ser");
                objectOutputStream = new ObjectOutputStream(fileOut);
                objectOutputStream.writeObject(IndexerMain.Dictionary);
            }
        } catch (IOException i) {
            System.out.println(">> Error writing Dictionary");
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

    private void serializeIndexedFileNumber() {

        FileOutputStream fileOut = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            synchronized (IndexerMain.indexedFileNumber) {
                fileOut = new FileOutputStream(System.getProperty("user.dir") + "/assets/indexedFileNumber.txt");
                objectOutputStream = new ObjectOutputStream(fileOut);
                objectOutputStream.writeLong(IndexerMain.indexedFileNumber);
            }
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

    private void printDictionary () {
        IndexerMain.Dictionary.forEach((word, links) -> {
            System.out.println(word);
            links.forEach((link, freq) -> System.out.println(link + " = " + freq));
            System.out.println();
        });
    }

}
