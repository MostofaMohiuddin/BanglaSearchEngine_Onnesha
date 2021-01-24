package WebCrawlerAndParser;

import java.net.URL;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomParser {

    static String regex = "[আইঈউঊঋঌএঐওঔকঁংঃঅখগঘঙচছজঝঞটঠডঢণতথদধনপফবভমযরলশষসহ়ঽািীুূৃৄেৈোৌ্ৎৗড়ঢ়য়ৠ০১২৩৪৫৬৭৮৯]*";

    static Pattern pattern=Pattern.compile(regex);

    //First adds the link of the HTML file and then adds the Bengali words found in it and returns the list __KAI
    public static LinkedList<String> getWords(String htmlFileLink, String htmlFile) {

        LinkedList<String> tempListOfWords = new LinkedList<>();

        Matcher matcher=pattern.matcher(htmlFile);

        tempListOfWords.add(htmlFileLink);

        while (matcher.find())
        {
            if(matcher.group().length() != 0)
            {
                tempListOfWords.add(matcher.group());
            }
        }

        return tempListOfWords;
    }

    //returns all links found in the given HTML file  __KAI
    public static LinkedList<String> getLinks(URL url, String htmlFile) {

        int init = 0, end;
        LinkedList<String> tempListOfLinks = new LinkedList<>();

        init = htmlFile.indexOf("<a href=\"", init);
        end = htmlFile.indexOf("\"", init+(8+1));

        while(init!=-1 && end!=-1){
            String tempLink = htmlFile.substring(init+9, end);

            String authority = url.getAuthority();
            String protocol = url.getProtocol();
            if (!(tempLink.contains(authority))) {
                if (!tempLink.contains("//")){
                    tempLink = protocol+"://" + authority + (tempLink.startsWith("/") ? (tempLink) : ("/"+tempLink) );
                }
                else if (!tempLink.contains("http:") && !tempLink.contains("https:")) {
                    tempLink = protocol+":" + tempLink;
                }
            } else if (!tempLink.contains("http:") && !tempLink.contains("https:") && tempLink.contains("//")) {
                tempLink = protocol+":" + tempLink;
            }

            if(tempLink.contains("wikipedia")){
                if(tempLink.contains("bn.")){
                    tempListOfLinks.add(tempLink);
                }
            } else {
                tempListOfLinks.add(tempLink);
            }

            init = end + 2;
            init = htmlFile.indexOf("<a href=\"", init);
            end = htmlFile.indexOf("\"", init+(8+1));
        }

        return tempListOfLinks;
    }

}
