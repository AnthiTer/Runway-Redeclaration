package uk.ac.soton.seg.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Model;
import uk.ac.soton.seg.model.Runway;

public class RunwayStringConverterTest {
    private RunwayStringConverter converter;
    private Airport airport;
    private Runway runway02;
    private Runway runway20;
    private Runway runwayWithRemarks;

    @BeforeEach
    void setup() {
        var model = Model.getModel();
        model.loadTestData();
        airport = model.getAirports().stream().filter(a -> a.getIdentifier().equals("EGHI")).findAny().get();
        converter = new RunwayStringConverter(airport);
        runway02 = airport.getRunwayByDesignator("02");
        runway20 = airport.getRunwayByDesignator("20");

        runwayWithRemarks = new Runway("02", 0, 0, 0, 0);
        runwayWithRemarks.setRemarks("test 5");
        airport.addRunway(runwayWithRemarks);
    }

    @Test
    void testToString() {
        assertAll(
                () -> assertEquals("02", converter.toString(runway02)),
                () -> assertEquals("02 test 5", converter.toString(runwayWithRemarks)),
                () -> assertEquals("", converter.toString(null)));

    }

    @Test
    void testFromString() {
        assertAll(
                () -> assertSame(runway20, converter.fromString("20")),
                () -> assertSame(runway02, converter.fromString("02")),
                () -> assertSame(runwayWithRemarks, converter.fromString("02 test 5")),
                () -> assertSame(null, converter.fromString("")));

    }
}
