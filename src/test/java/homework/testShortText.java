package homework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testShortText {

    @Test
    public void shortText(){
        String str = "LearnQA is the best";

        assertTrue(str.length() > 15, "Text length must be greater than 15 characters");
    }

}