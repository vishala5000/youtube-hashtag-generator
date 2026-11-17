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
    
    // Words to NEVER use as hashtags
    private static final Set<String> BLACKLIST = new HashSet<>(Arrays.asList(
        // Stop words
        "how", "to", "in", "on", "the", "a", "for", "with", "and", "is", "it", "of", "at", "by", "an",
        "be", "this", "that", "from", "or", "are", "as", "was", "were", "been", "have", "has", "had",
        "do", "does", "did", "will", "would", "could", "should", "can", "what", "why", "when", "where",
        "who", "which", "your", "my", "you", "me", "he", "she", "they", "we", "them", "us", "its", "about",
        "best", "top", "new", "old", "free", "paid", "easy", "fast", "quick", "simple", "video", "videos",
        // Languages (irrelevant)
        "tamil", "telugu", "malayalam", "kannada", "hindi", "english", "spanish", "french", "german",
        // Random common words that appear in many suggestions
        "home", "house", "room", "kitchen", "bedroom", "bathroom", "office", "school", "college",
        "milk", "water", "cheese", "butter", "oil", "salt", "sugar", "rice", "bread", "egg", "eggs",
        "phone", "mobile", "laptop", "computer", "tablet", "tv", "television", "radio",
        "car", "bike", "bus", "train", "plane", "flight", "travel", "trip",
        "boy", "boys", "girl", "girls", "man", "men", "woman", "women", "kid", "kids", "child", "children",
        "copy", "paste", "cut", "edit", "download", "upload", "share", "save", "delete", "remove",
        "website", "app", "application", "software", "program", "tool", "tools",
        "make", "making", "use", "using", "get", "getting", "give", "giving", "take", "taking",
        "come", "coming", "go", "going", "see", "seeing", "look", "looking", "watch", "watching",
        "work", "working", "play", "playing", "run", "running", "walk", "walking", "talk", "talking",
        "like", "love", "hate", "want", "need", "know", "think", "feel", "say", "tell", "ask",
        "good", "bad", "great", "nice", "cool", "awesome", "amazing", "beautiful", "ugly",
        "big", "small", "large", "tiny", "huge", "long", "short", "high", "low", "hot", "cold",
        "first", "second", "third", "last", "next", "previous", "before", "after",
        "yes", "no", "maybe", "always", "never", "sometimes", "often", "rarely"
    ));

    public static String generateHashtags(String title) {
        try {
            Log.i(TAG, "Generating hashtags for: " + title);
            
            Set<String> hashtags = new LinkedHashSet<>();
            
            // 1. Extract meaningful words from title
            String clean = title.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
            String[] titleWords = clean.split("\\s+");
            Set<String> titleWordSet = new LinkedHashSet<>();
            
            for (String word : titleWords) {
                if (word != null && word.length() >= 3 && !BLACKLIST.contains(word)) {
                    hashtags.add("#" + word);
                    titleWordSet.add(word);
                }
            }
            
            // 2. Fetch suggestions and count word frequency
            try {
                List<String> suggestions = fetchSuggestions(title);
                Map<String, Integer> wordFrequency = new HashMap<>();
                
                // Count how many times each word appears across ALL suggestions
                for (String suggestion : suggestions) {
                    String[] words = suggestion.toLowerCase().split("\\s+");
                    Set<String> uniqueWords = new HashSet<>();
                    
                    for (String word : words) {
                        if (word.length() >= 3 && !BLACKLIST.contains(word) && !titleWordSet.contains(word)) {
                            uniqueWords.add(word);
                        }
                    }
                    
                    // Count each unique word once per suggestion
                    for (String word : uniqueWords) {
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                    }
                }
                
                // 3. Only keep words that appear in at least 2 suggestions (high relevance)
                List<Map.Entry<String, Integer>> relevantWords = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
                    if (entry.getValue() >= 2) { // Must appear in 2+ suggestions
                        relevantWords.add(entry);
                    }
                }
                
                // Sort by frequency
                relevantWords.sort((a, b) -> b.getValue() - a.getValue());
                
                // 4. Add top relevant words (max 3)
                for (int i = 0; i < Math.min(3, relevantWords.size()); i++) {
                    if (hashtags.size() >= 8) break;
                    hashtags.add("#" + relevantWords.get(i).getKey());
                }
                
                // 5. Create smart 2-word compounds (title word + most relevant word)
                if (hashtags.size() < 10 && !relevantWords.isEmpty() && !titleWordSet.isEmpty()) {
                    String mainTitleWord = titleWordSet.iterator().next();
                    String relatedWord = relevantWords.get(0).getKey();
                    String compound = "#" + mainTitleWord + relatedWord;
                    
                    // Only add if not too long
                    if (compound.length() <= 25) {
                        hashtags.add(compound);
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "API fetch failed", e);
            }
            
            // 6. Only add viral tags if we still don't have 10
            String[] viral = {"#viral", "#trending", "#youtube", "#shorts", "#fyp", "#explore", "#subscribe"};
            for (String v : viral) {
                if (hashtags.size() >= 10) break;
                hashtags.add(v);
            }
            
            // 7. Build exactly 10 hashtags
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
