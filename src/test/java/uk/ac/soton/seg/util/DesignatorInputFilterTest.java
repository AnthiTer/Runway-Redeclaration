package uk.ac.soton.seg.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextFormatter;

public class DesignatorInputFilterTest {

    DesignatorInputFilter filter = new DesignatorInputFilter();

    private TextFormatter.Change makeChange(String s) {
        TextFormatter.Change c = mock(TextFormatter.Change.class);
        when(c.getText()).thenReturn(s);
        return c;
    }

    @Test
    void testIntegerFilter() {
        assertAll(
                () -> assertNull(filter.apply(makeChange(" "))),
                () -> assertNull(filter.apply(makeChange("0 9L"))),
                () -> assertNull(filter.apply(makeChange("a"))),
                () -> assertNull(filter.apply(makeChange("r"))),
                () -> assertNull(filter.apply(makeChange("12a"))),
                () -> assertNull(filter.apply(makeChange("1a"))),
                () -> assertNotNull(filter.apply(makeChange("0"))),
                () -> assertNotNull(filter.apply(makeChange("1LRC1"))),
                () -> assertNotNull(filter.apply(makeChange("09R"))),
                () -> assertNotNull(filter.apply(makeChange("32C"))),
                () -> assertNotNull(filter.apply(makeChange("L"))),
                () -> assertNotNull(filter.apply(makeChange(""))));
    }

}