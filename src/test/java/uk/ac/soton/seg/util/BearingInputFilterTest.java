package uk.ac.soton.seg.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextFormatter;

public class BearingInputFilterTest {

    BearingInputFilter filter = new BearingInputFilter();

    private TextFormatter.Change makeChange(String s) {
        TextFormatter.Change c = mock(TextFormatter.Change.class);
        when(c.getText()).thenReturn(s);
        return c;
    }

    @Test
    void testIntegerFilter() {
        assertAll(
                () -> assertNull(filter.apply(makeChange(" "))),
                () -> assertNull(filter.apply(makeChange("a"))),
                () -> assertNull(filter.apply(makeChange(","))),
                () -> assertNull(filter.apply(makeChange("-"))),
                () -> assertNull(filter.apply(makeChange("-1"))),
                () -> assertNull(filter.apply(makeChange("+"))),
                () -> assertNotNull(filter.apply(makeChange("12.5"))),
                () -> assertNotNull(filter.apply(makeChange("123"))),
                () -> assertNotNull(filter.apply(makeChange("123.3."))),
                () -> assertNotNull(filter.apply(makeChange("....."))),
                () -> assertNotNull(filter.apply(makeChange("1234567890."))),
                () -> assertNotNull(filter.apply(makeChange(""))));
    }

}