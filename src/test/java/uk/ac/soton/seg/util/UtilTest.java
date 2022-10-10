package uk.ac.soton.seg.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import uk.ac.soton.seg.model.Runway;

public class UtilTest {
    @Test
    void testIntegerFilter() {
        assertAll(
                () -> assertEquals(9000, Util.maxDeclaredDistance(new Runway("00", 9000, 1000, 500, 500))),
                () -> assertEquals(1234, Util.maxDeclaredDistance(new Runway("00", 500, 1234, 500, 500))),
                () -> assertEquals(4000, Util.maxDeclaredDistance(new Runway("00", 500, 1234, 4000, 500))),
                () -> assertEquals(1600, Util.maxDeclaredDistance(new Runway("00", 500, 500, 100, 1600))));
    }

    @Test
    void testCheckPositive() {
        assertAll(
                () -> assertDoesNotThrow(() -> Util.checkPositive(10)),
                () -> assertDoesNotThrow(() -> Util.checkPositive(2)),
                () -> assertDoesNotThrow(() -> Util.checkPositive(1)),
                () -> assertDoesNotThrow(() -> Util.checkPositive(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> Util.checkPositive(-1)),
                () -> assertThrows(IllegalArgumentException.class, () -> Util.checkPositive(-2)),
                () -> assertThrows(IllegalArgumentException.class, () -> Util.checkPositive(-10)));
    }

    @Test
    void testValidateBearing() {

        assertAll(
                () -> assertFalse(Util.validateBearing(-370)),
                () -> assertFalse(Util.validateBearing(-50)),
                () -> assertFalse(Util.validateBearing(-1)),
                () -> assertFalse(Util.validateBearing(-0.1)),

                () -> assertTrue(Util.validateBearing(0.1)),
                () -> assertTrue(Util.validateBearing(0)),
                () -> assertTrue(Util.validateBearing(300)),
                () -> assertTrue(Util.validateBearing(359.999)),

                () -> assertFalse(Util.validateBearing(360)),
                () -> assertFalse(Util.validateBearing(360.00001)),
                () -> assertFalse(Util.validateBearing(361)));

    }

    @Test
    void testValidateDesignator() {
        assertAll(
                () -> assertFalse(Util.validateDesignator("00")),
                () -> assertFalse(Util.validateDesignator("00L")),
                () -> assertFalse(Util.validateDesignator("00C")),
                () -> assertFalse(Util.validateDesignator("00R")),

                () -> assertTrue(Util.validateDesignator("01")),
                () -> assertTrue(Util.validateDesignator("01L")),
                () -> assertTrue(Util.validateDesignator("10C")),
                () -> assertTrue(Util.validateDesignator("10R")),

                () -> assertTrue(Util.validateDesignator("35L")),
                () -> assertTrue(Util.validateDesignator("36L")),

                () -> assertFalse(Util.validateDesignator("37")),
                () -> assertFalse(Util.validateDesignator("37L")),
                () -> assertFalse(Util.validateDesignator("60C")),
                () -> assertFalse(Util.validateDesignator("60R")),

                () -> assertFalse(Util.validateDesignator("asdfasdf")),
                () -> assertFalse(Util.validateDesignator("9999999999999")),
                () -> assertFalse(Util.validateDesignator("10F")),
                () -> assertFalse(Util.validateDesignator("00F")),
                () -> assertFalse(Util.validateDesignator("36LL")),
                () -> assertFalse(Util.validateDesignator("36 ")),
                () -> assertFalse(Util.validateDesignator("36CC")),
                () -> assertFalse(Util.validateDesignator("36FF")),

                // special case for testing non-existant runway etc
                () -> assertTrue(Util.validateDesignator("99C")),
                () -> assertTrue(Util.validateDesignator("99"))

        );

    }

}
