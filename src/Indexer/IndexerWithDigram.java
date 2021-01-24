package Indexer;

import java.util.Hashtable;
import java.util.LinkedList;

import static Indexer.IndexerMain.Dictionary;
import static Indexer.IndexerMain.banglaBivokti;
import static Indexer.IndexerMain.banglaStopWords;

public class IndexerWithDigram {

    public static void indexSingleWords (LinkedList<String> wordList, String pageURL) {

        for ( String word : wordList ) {

            String tempWord;
            Boolean isIndexed = false;

            if (banglaStopWords.contains(word)) {
                continue;
            }

            if (Dictionary.containsKey(word)) {
                Hashtable<String, Integer> previousEntry = Dictionary.get(word);
                if (previousEntry.containsKey(pageURL)) {
                    previousEntry.put(pageURL, (previousEntry.get(pageURL)+1));
                } else {
                    previousEntry.put(pageURL, 1);
                }
                synchronized (Dictionary) {
                    Dictionary.put(word, previousEntry);
                }
                continue;
            }


            for (String bivokti : banglaBivokti) {
                if (word.endsWith(bivokti)) {
                    tempWord = word.substring(0, word.lastIndexOf(bivokti));
                    if (Dictionary.containsKey(tempWord)) {
                        Hashtable<String, Integer> previousEntry = Dictionary.get(tempWord);
                        if (previousEntry.containsKey(pageURL)) {
                           previousEntry.put(pageURL, (previousEntry.get(pageURL)+1));
                        } else {
                            previousEntry.put(pageURL, 1);
                        }
                        synchronized (Dictionary) {
                            Dictionary.put(tempWord, previousEntry);
                        }
                        isIndexed = true;
                        break;
                    }
                }
            }

            if(!isIndexed) {
                Hashtable<String, Integer> newEntry = new Hashtable<>();
                newEntry.put(pageURL, 1);
                synchronized (Dictionary) {
                    Dictionary.put(word, newEntry);
                }
            }

        }

    }

    public static void indexDoubleWords (LinkedList<String> wordList, String pageURL) {

        for ( int i=0; i < (wordList.size()-1); i++ ) {

            if (banglaStopWords.contains(wordList.get(i))) {
                continue;
            }

            if (wordList.get(i).equals(wordList.get(i+1))) {
                continue;
            }

            String word = (wordList.get(i) + " " + wordList.get(i+1));

            if (Dictionary.containsKey(word)) {
                Hashtable<String, Integer> previousEntry = Dictionary.get(word);
                if (previousEntry.containsKey(pageURL)) {
                    previousEntry.put(pageURL, (previousEntry.get(pageURL)+1));
                } else {
                    previousEntry.put(pageURL, 1);
                }
                synchronized (Dictionary) {
                    Dictionary.put(word, previousEntry);
                }

            } else {
                Hashtable<String, Integer> newEntry = new Hashtable<>();
                newEntry.put(pageURL, 1);
                synchronized (Dictionary) {
                    Dictionary.put(word, newEntry);
                }
            }
        }
    }
}
