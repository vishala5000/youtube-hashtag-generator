package com.hashtag.generator;

import org.junit.Test;
import static org.junit.Assert.*;

public class HashtagGeneratorTest {

    @Test
    public void testPastaInput_OutputIsPerfect() {
        // INPUT
        String result = HashtagGenerator.generateHashtags("how to cook pasta");
        
        // OUTPUT VERIFICATION
        assertNotNull("Result should not be null", result);
        String[] tags = result.split(" ");
        assertTrue("Should have between 5 and 10 tags", tags.length >= 5 && tags.length <= 10);
        
        // MUST contain core topics
        assertTrue("MUST contain #cook", result.contains("#cook"));
        assertTrue("MUST contain #pasta", result.contains("#pasta"));
        
        // MUST NOT contain known garbage from previous bugs
        assertFalse("MUST NOT contain garbage #milk", result.toLowerCase().contains("#milk"));
        assertFalse("MUST NOT contain garbage #cheese", result.toLowerCase().contains("#cheese"));
        assertFalse("MUST NOT contain garbage #home", result.toLowerCase().contains("#home"));
        assertFalse("MUST NOT contain garbage #cooker", result.toLowerCase().contains("#cooker"));
    }

    @Test
    public void testHindiWorshipInput_OutputIsPerfect() {
        // INPUT
        String result = HashtagGenerator.generateHashtags("Christian hindi worship songs 2026");
        
        // OUTPUT VERIFICATION
        assertNotNull("Result should not be null", result);
        
        // MUST contain core topics (including 'hindi' because user typed it)
        assertTrue("MUST contain #christian", result.contains("#christian"));
        assertTrue("MUST contain #hindi", result.contains("#hindi"));
        assertTrue("MUST contain #worship", result.contains("#worship"));
        
        // MUST NOT contain random regional languages not in the title
        assertFalse("MUST NOT contain garbage #tamil", result.toLowerCase().contains("#tamil"));
        assertFalse("MUST NOT contain garbage #malayalam", result.toLowerCase().contains("#malayalam"));
        assertFalse("MUST NOT contain garbage #telugu", result.toLowerCase().contains("#telugu"));
    }

    @Test
    public void testPhotoEditingInput_OutputIsPerfect() {
        // INPUT
        String result = HashtagGenerator.generateHashtags("photo editing prompt");
        
        // OUTPUT VERIFICATION
        assertNotNull("Result should not be null", result);
        
        assertTrue("MUST contain #photo", result.contains("#photo"));
        assertTrue("MUST contain #editing", result.contains("#editing"));
        assertTrue("MUST contain #prompt", result.contains("#prompt"));
        
        assertFalse("MUST NOT contain garbage #copy", result.toLowerCase().contains("#copy"));
        assertFalse("MUST NOT contain garbage #couple", result.toLowerCase().contains("#couple"));
    }
}
