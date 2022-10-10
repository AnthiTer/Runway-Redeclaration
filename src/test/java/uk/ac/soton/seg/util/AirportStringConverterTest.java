package uk.ac.soton.seg.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Model;

public class AirportStringConverterTest {
    private AirportStringConverter converter;
    private Airport airport;

    @BeforeEach
    void setup() {
        var model = Model.getModel();
        model.loadTestData();
        airport = model.getAirports().stream().filter(a -> a.getIdentifier().equals("EGHI")).findAny().get();
        converter = new AirportStringConverter(model.getAirports());
    }

    @Test
    void testToString() {
        assertAll(
                () -> assertEquals("EGHI - Southampton", converter.toString(airport)),
                () -> assertEquals("", converter.toString(null)));

    }

    @Test
    void testFromString() {
        assertAll(
                () -> assertSame(airport, converter.fromString("EGHI")),
                () -> assertSame(airport, converter.fromString("EGHI - Southampton")),
                () -> assertSame(airport, converter.fromString("EGHI - asdf")),
                () -> assertSame(null, converter.fromString("")));

    }
}
