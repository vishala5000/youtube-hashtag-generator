package com.hashtag.generator;

import org.junit.Test;
import static org.junit.Assert.*;

public class HashtagGeneratorTest {

    @Test
    public void testPastaInput_OutputIsPerfect() {
        String result = HashtagGenerator.generateHashtags("how to cook pasta");
        System.out.println("Pasta Output: " + result);
        
        assertNotNull("Result should not be null", result);
        String lower = result.toLowerCase();
        
        assertTrue("MUST contain #cook", lower.contains("#cook"));
        assertTrue("MUST contain #pasta", lower.contains("#pasta"));
        
        assertFalse("MUST NOT contain garbage #milk", lower.contains("#milk"));
        assertFalse("MUST NOT contain garbage #cheese", lower.contains("#cheese"));
        assertFalse("MUST NOT contain garbage #home", lower.contains("#home"));
        assertFalse("MUST NOT contain garbage #cooker", lower.contains("#cooker"));
        assertFalse("MUST NOT contain garbage #fryer", lower.contains("#fryer"));
    }

    @Test
    public void testHindiWorshipInput_OutputIsPerfect() {
        String result = HashtagGenerator.generateHashtags("Christian hindi worship songs 2026");
        System.out.println("Hindi Worship Output: " + result);
        
        assertNotNull("Result should not be null", result);
        String lower = result.toLowerCase();
        
        assertTrue("MUST contain #christian", lower.contains("#christian"));
        assertTrue("MUST contain #hindi", lower.contains("#hindi"));
        assertTrue("MUST contain #worship", lower.contains("#worship"));
        
        assertFalse("MUST NOT contain garbage #tamil", lower.contains("#tamil"));
        assertFalse("MUST NOT contain garbage #malayalam", lower.contains("#malayalam"));
        assertFalse("MUST NOT contain garbage #telugu", lower.contains("#telugu"));
        assertFalse("MUST NOT contain garbage #odia", lower.contains("#odia"));
    }

    @Test
    public void testPhotoEditingInput_OutputIsPerfect() {
        String result = HashtagGenerator.generateHashtags("photo editing prompt");
        System.out.println("Photo Editing Output: " + result);
        
        assertNotNull("Result should not be null", result);
        String lower = result.toLowerCase();
        
        assertTrue("MUST contain #photo", lower.contains("#photo"));
        assertTrue("MUST contain #editing", lower.contains("#editing"));
        assertTrue("MUST contain #prompt", lower.contains("#prompt"));
        
        assertFalse("MUST NOT contain garbage #copy", lower.contains("#copy"));
        assertFalse("MUST NOT contain garbage #couple", lower.contains("#couple"));
        assertFalse("MUST NOT contain garbage #gemini", lower.contains("#gemini"));
    }
    
    @Test
    public void testInstagramAccountInput_OutputIsPerfect() {
        String result = HashtagGenerator.generateHashtags("how to instagram account");
        System.out.println("Instagram Output: " + result);
        
        assertNotNull("Result should not be null", result);
        String lower = result.toLowerCase();
        
        assertTrue("MUST contain #instagram", lower.contains("#instagram"));
        assertTrue("MUST contain #account", lower.contains("#account"));
        
        assertFalse("MUST NOT contain garbage #private", lower.contains("#private"));
        assertFalse("MUST NOT contain garbage #change", lower.contains("#change"));
    }
}
