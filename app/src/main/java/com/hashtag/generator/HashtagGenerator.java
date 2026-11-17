package com.hashtag.generator;

import java.util.*;

public class HashtagGenerator {
    
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "how", "to", "in", "on", "the", "a", "for", "with", "and", "is", "it", "of", "at", "by", "an",
        "be", "this", "that", "from", "or", "are", "as", "was", "were", "been", "have", "has", "had",
        "do", "does", "did", "will", "would", "could", "should", "can", "what", "why", "when", "where",
        "who", "which", "your", "my", "you", "me", "he", "she", "they", "we", "them", "us", "its", "about",
        "best", "top", "new", "old", "free", "paid", "easy", "fast", "quick", "simple", "video", "videos"
    ));

    public static String generateHashtags(String title) {
        String clean = title.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
        String[] words = clean.split("\\s+");
        
        Set<String> hashtags = new LinkedHashSet<>();
        
        // Extract meaningful single words from title
        for (String word : words) {
            if (word.length() >= 3 && !STOP_WORDS.contains(word)) {
                hashtags.add("#" + word);
            }
        }
        
        // Add universal viral tags
        String[] viral = {"#viral", "#trending", "#youtube", "#shorts", "#fyp"};
        for (String v : viral) {
            if (hashtags.size() < 10) {
                hashtags.add(v);
            }
        }
        
        // Build output (exactly 10)
        List<String> list = new ArrayList<>(hashtags);
        StringBuilder result = new StringBuilder();
        int limit = Math.min(10, list.size());
        for (int i = 0; i < limit; i++) {
            result.append(list.get(i));
            if (i < limit - 1) result.append(" ");
        }
        
        return result.toString();
    }
}
