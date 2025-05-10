package dev.siroshun.configapi.core.comment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleCommentTest {

    @Test
    void testCreate() {
        SimpleComment comment1 = SimpleComment.create("");
        assertEquals("", comment1.content());
        assertEquals("", comment1.type());

        SimpleComment comment2 = SimpleComment.create("test");
        assertEquals("test", comment2.content());
        assertEquals("", comment2.type());

        SimpleComment comment3 = SimpleComment.create("test", "type");
        assertEquals("test", comment3.content());
        assertEquals("type", comment3.type());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullForCreate() {
        assertThrows(NullPointerException.class, () -> SimpleComment.create(null));
        assertThrows(NullPointerException.class, () -> SimpleComment.create(null, "type"));
        assertThrows(NullPointerException.class, () -> SimpleComment.create("content", null));
    }
}
