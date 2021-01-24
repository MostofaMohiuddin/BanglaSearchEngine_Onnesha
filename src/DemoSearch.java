import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class DemoSearch {
    private static Hashtable<String, Hashtable<String, Integer>> Dictionary;
    private static LinkedList<String> banglaBivokti = new LinkedList<>();
    private static LinkedList<String> banglaStopWords = new LinkedList<>();
    public static void main(String[] args) {

        System.out.println("Bangla Search Engine\n" + "***** ONNESHA *****\n\n" +
                "Please wait file loading...\n\n");

        deserializer();
        Scanner scan = new Scanner(System.in);
        LinkedList<String> keywords = new LinkedList<>();
        String searchWords;
        Hashtable<String, Integer> searchResults = new Hashtable<>();


        do{
            System.out.print("Enter keyword : ");
            searchWords = scan.nextLine();

            Collections.addAll(keywords, searchWords.split(" "));

            for (int i = 0; i < keywords.size(); i++) {
                if(banglaStopWords.contains(keywords.get(i))) {
                    keywords.remove(i);
                }
            }

            for (int i = 0; i < (keywords.size()-1); i++) {
                String doubleWords = keywords.get(i) + " " + keywords.get(i+1);
                if(Dictionary.containsKey(doubleWords)) {
                    Hashtable<String, Integer> tempResult = Dictionary.get(doubleWords);
                    tempResult.forEach((link, freq) -> {
                        if(searchResults.containsKey(link)) {
                            searchResults.put(link, (searchResults.get(link) + freq + 75));
                        } else {
                            searchResults.put(link, freq + 30);
                        }
                    });
                }
            }


            for (int i = 0; i < (keywords.size()); i++) {

                String singleWords = keywords.get(i);

                if(Dictionary.containsKey(singleWords)) {
                    Hashtable<String, Integer> tempResult = Dictionary.get(singleWords);
                    tempResult.forEach((link, freq) -> {
                        if(searchResults.containsKey(link)) {
                            searchResults.put(link, (searchResults.get(link) + freq + 45));
                        } else {
                            searchResults.put(link, freq);
                        }
                    });

                    continue;
                }

                for ( String bivokti : banglaBivokti ) {
                    if( singleWords.endsWith(bivokti) ) {
                        singleWords = searchWords.substring(0, singleWords.lastIndexOf(bivokti));
                        if(Dictionary.containsKey(singleWords)) {
                            Hashtable<String, Integer> tempResult = Dictionary.get(singleWords);
                            tempResult.forEach((link, freq) -> {
                                if(searchResults.containsKey(link)) {
                                    searchResults.put(link, (searchResults.get(link) + freq + 45));
                                } else {
                                    searchResults.put(link, freq);
                                }
                            });

                            break;
                        }
                    }
                }

            }

            int resultNo = 1;

            if(searchResults.isEmpty()) {
                System.out.println("Not found\n");
            } else {
                LinkedList<Map.Entry<String,Integer>> sortedResult = sortByValues(searchResults);
                for (Map.Entry<String,Integer> entry : sortedResult) {
                    System.out.println( resultNo++ + ". Link : " + entry.getKey() );
//                            + "\n" + "weight : " + entry.getValue());  //add if need to see weight __KAI
                    if(resultNo > 10) break;
                }
            }

            System.out.println();
            searchResults.clear();
            keywords.clear();
        } while (!searchWords.equalsIgnoreCase("exit"));

    }

    private static LinkedList<Map.Entry<String,Integer>> sortByValues(Hashtable<String, Integer> results) {

        LinkedList<Map.Entry<String,Integer>> entries = new LinkedList<>(results.entrySet());

        Collections.sort(entries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        return entries;
    }


    private static void deserializer() {
        FileInputStream fileIn = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileIn = new FileInputStream(System.getProperty("user.dir")+"/assets/Dictionary.ser");
            objectInputStream = new ObjectInputStream(fileIn);
            Dictionary = (Hashtable<String, Hashtable<String, Integer>>) objectInputStream.readObject();

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
