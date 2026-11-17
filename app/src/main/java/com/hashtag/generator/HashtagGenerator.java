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
        "who", "which", "your", "my", "you", "me", "he", "she", "they", "we", "them", "us", "its", "about",
        "best", "top", "new", "old", "free", "paid", "easy", "fast", "quick", "simple", "video", "videos",
        "use", "using", "make", "making", "get", "getting", "give", "giving", "take", "taking"
    ));

    public static String generateHashtags(String title) {
        try {
            Log.i(TAG, "Generating hashtags for: " + title);
            
            Set<String> hashtags = new LinkedHashSet<>();
            
            // 1. Extract ALL meaningful words from title (including languages!)
            String clean = title.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
            String[] titleWords = clean.split("\\s+");
            Set<String> titleWordSet = new LinkedHashSet<>();
            
            for (String word : titleWords) {
                if (word != null && word.length() >= 3 && !STOP_WORDS.contains(word)) {
                    hashtags.add("#" + word);
                    titleWordSet.add(word);
                }
            }
            
            // 2. Fetch suggestions and extract relevant words
            try {
                List<String> suggestions = fetchSuggestions(title);
                Map<String, Integer> wordFrequency = new HashMap<>();
                
                for (String suggestion : suggestions) {
                    String[] words = suggestion.toLowerCase().split("\\s+");
                    Set<String> uniqueWords = new HashSet<>();
                    
                    for (String word : words) {
                        if (word.length() >= 3 && !STOP_WORDS.contains(word) && !titleWordSet.contains(word)) {
                            uniqueWords.add(word);
                        }
                    }
                    
                    for (String word : uniqueWords) {
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                    }
                }
                
                List<Map.Entry<String, Integer>> relatedWords = new ArrayList<>(wordFrequency.entrySet());
                relatedWords.sort((a, b) -> b.getValue() - a.getValue());
                
                int relatedToAdd = Math.min(4, relatedWords.size());
                for (int i = 0; i < relatedToAdd; i++) {
                    if (hashtags.size() >= 7) break;
                    hashtags.add("#" + relatedWords.get(i).getKey());
                }
                
                if (hashtags.size() < 9 && !relatedWords.isEmpty() && !titleWordSet.isEmpty()) {
                    String mainWord = titleWordSet.iterator().next();
                    String relatedWord = relatedWords.get(0).getKey();
                    String compound = "#" + mainWord + relatedWord;
                    
                    if (compound.length() <= 25) {
                        hashtags.add(compound);
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "API fetch failed", e);
            }
            
            // 6. Add ONLY 2-3 viral tags
            String[] viral = {"#viral", "#trending", "#youtube", "#shorts", "#fyp"};
            int viralCount = 0;
            for (String v : viral) {
                if (hashtags.size() >= 10 || viralCount >= 3) break;
                hashtags.add(v);
                viralCount++;
            }
            
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
            return "#viral #trending #youtube #shorts #fyp #explore #subscribe #content #video #trending";
        }
    }
    
    private static List<String> fetchSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
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
                
                Pattern pattern = Pattern.compile("\"([^\"]+)\"");
                Matcher matcher = pattern.matcher(content.toString());
                
                while (matcher.find() && suggestions.size() < 20) {
                    String suggestion = matcher.group(1).trim();
                    if (suggestion.length() > 3 && suggestion.length() < 50) {
                        suggestions.add(suggestion);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Fetch error", e);
        }
        return suggestions;
    }
}
