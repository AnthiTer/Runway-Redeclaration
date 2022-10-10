package uk.ac.soton.seg.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextFormatter;

public class IntegerInputFilterTest {

    IntegerInputFilter filter = new IntegerInputFilter();
    IntegerInputFilter allowNegativeFilter = new IntegerInputFilter(true);

    private TextFormatter.Change makeChange(String s) {
        TextFormatter.Change c = mock(TextFormatter.Change.class);
        when(c.getText()).thenReturn(s);
        return c;
    }

    @Test
    void testIntegerFilter() {
        assertAll(
                () -> assertNull(filter.apply(makeChange(" "))),
                () -> assertNull(filter.apply(makeChange("1 2"))),
                () -> assertNull(filter.apply(makeChange("-10"))),
                () -> assertNull(filter.apply(makeChange("a"))),
                () -> assertNull(filter.apply(makeChange("12a"))),
                () -> assertNull(filter.apply(makeChange("1a"))),
                () -> assertNotNull(filter.apply(makeChange("1"))),
                () -> assertNotNull(filter.apply(makeChange("1234"))),
                () -> assertNotNull(filter.apply(makeChange("4956"))),
                () -> assertNotNull(filter.apply(makeChange("1234536"))),
                () -> assertNotNull(filter.apply(makeChange(""))),
                () -> assertNotNull(filter.apply(makeChange("0"))));
    }
    
    @Test
    void testNegativeIntegerFilter() {
        assertAll(
                () -> assertNull(allowNegativeFilter.apply(makeChange(" "))),
                () -> assertNull(allowNegativeFilter.apply(makeChange("1 2"))),
                () -> assertNull(allowNegativeFilter.apply(makeChange("a"))),
                () -> assertNull(allowNegativeFilter.apply(makeChange("12a"))),
                () -> assertNull(allowNegativeFilter.apply(makeChange("1a"))),
                () -> assertNotNull(allowNegativeFilter.apply(makeChange("-10"))),
                () -> assertNotNull(allowNegativeFilter.apply(makeChange("-1"))),
                () -> assertNotNull(allowNegativeFilter.apply(makeChange("-1234"))),
                () -> assertNotNull(allowNegativeFilter.apply(makeChange("-4956"))),
                () -> assertNotNull(allowNegativeFilter.apply(makeChange("123-4536"))),
                () -> assertNotNull(allowNegativeFilter.apply(makeChange("-"))),
                () -> assertNotNull(allowNegativeFilter.apply(makeChange("-0"))));
    }

}
