package com.hashtag.generator;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagGenerator {
    private static final String TAG = "HashtagGen";
    
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "how", "to", "in", "on", "the", "a", "for", "with", "and", "is", "it", "of", "at", "by", "an",
        "be", "this", "that", "from", "or", "are", "as", "was", "were", "been", "have", "has", "had",
        "do", "does", "did", "will", "would", "could", "should", "can", "what", "why", "when", "where",
        "who", "which", "your", "my", "you", "me", "he", "she", "they", "we", "them", "us", "its", "about"
    ));

    public static String generateHashtags(String title) {
        try {
            Log.i(TAG, "Generating hashtags for: " + title);
            
            Set<String> hashtags = new LinkedHashSet<>();
            
            // 1. Extract words from title
            String clean = title.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
            String[] words = clean.split("\\s+");
            
            for (String word : words) {
                if (word != null && word.length() >= 3 && !STOP_WORDS.contains(word)) {
                    hashtags.add("#" + word);
                }
            }
            
            // 2. Fetch related hashtags from API
            try {
                List<String> apiTags = fetchRelatedHashtags(title);
                hashtags.addAll(apiTags);
            } catch (Exception e) {
                Log.e(TAG, "API fetch failed", e);
            }
            
            // 3. Add viral tags to reach exactly 10
            String[] viral = {"#viral", "#trending", "#youtube", "#shorts", "#fyp", "#explore", "#subscribe", "#content"};
            for (String v : viral) {
                if (hashtags.size() >= 10) break;
                hashtags.add(v);
            }
            
            // 4. Build exactly 10 hashtags
            List<String> list = new ArrayList<>(hashtags);
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < 10 && i < list.size(); i++) {
                result.append(list.get(i));
                if (i < 9 && i < list.size() - 1) result.append(" ");
            }
            
            String output = result.toString();
            Log.i(TAG, "Generated " + list.size() + " hashtags: " + output);
            
            return output;
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating hashtags", e);
            return "#viral #trending #youtube #shorts #fyp #explore #subscribe #content #video #instagram";
        }
    }
    
    private static List<String> fetchRelatedHashtags(String query) {
        List<String> tags = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String urlString = "https://clients1.google.com/complete/search?client=youtube&ds=yt&q=" + encodedQuery;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            
            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                in.close();
                
                // Extract suggestions
                Pattern pattern = Pattern.compile("\"([^\"]+)\"");
                Matcher matcher = pattern.matcher(content.toString());
                
                while (matcher.find() && tags.size() < 15) {
                    String suggestion = matcher.group(1).trim();
                    if (suggestion.length() > 3 && suggestion.length() < 30) {
                        // Extract individual words from suggestion
                        String[] words = suggestion.toLowerCase().split("\\s+");
                        for (String word : words) {
                            if (word.length() >= 3 && !STOP_WORDS.contains(word)) {
                                String tag = "#" + word;
                                if (!tags.contains(tag)) {
                                    tags.add(tag);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Fetch error", e);
        }
        return tags;
    }
}
