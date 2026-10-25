package tools;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SayHelloTest {
    @Test
    public void testMessage() {
        SayHello sayHello = new SayHello();
        assertEquals("Hello, world", sayHello.getMessage(), "The message should be equal to \"Hello, word\"");
        assertEquals("Good bye", sayHello.getOtherMessage(), "The other message should be equal to \"Good bye\"");
    }

    @Test
    public void testBothMessages() {
        SayHello sayHello = new SayHello();
        assertEquals("Hello, world\nGood bye", sayHello.getBothMessages(),
                "The message should be equal to \"Hello, word\"");
    }
}
