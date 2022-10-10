package uk.ac.soton.seg.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AirportTests {

    @Test
    void consAirport() {
        var a = new Airport("EGLL", "HEATHROW");
        assertEquals("EGLL", a.getIdentifier());
        assertEquals("HEATHROW", a.getName());
    }

    @Test
    void consBlankAirport() {
        var a = new Airport();
        assertNull(a.getName());
        assertNull(a.getIdentifier());

        a.setIdentifier("EGLL");
        a.setName("HEATHROW");

        assertEquals("EGLL", a.getIdentifier());
        assertEquals("HEATHROW", a.getName());
    }

    @Nested
    class SotonAirportTests {
        private Airport sotonAirport;
        private Runway sotonAirportRunway02;

        @BeforeEach
        void setupSoton() {
            // var airports = new Airports();
            this.sotonAirport = new Airport("EGHI", "Southampton");
            // var sotonAirportRunway = new Runway("02", defaultTORA, defaultStopway,
            // defaultClearway);
            sotonAirportRunway02 = new Runway("02", 1723, 1831, 1723, 1650);
            sotonAirport.addRunway(sotonAirportRunway02);
            // airports.addAirport(sotonAirport);
        }

        @Test
        @DisplayName("runway array can be retrieved")
        void testGetRunways() {
            assertEquals(sotonAirportRunway02, sotonAirport.getRunways().get(0), "retrieved runway index 0");
        }

        @Test
        @DisplayName("original runway declarations")
        void testOriginalDeclarations() {
            Runway runway = sotonAirport.getRunways().get(0);

            // https://nats-uk.ead-it.com/cms-nats/opencms/en/Publications/AIP/
            // https://www.aurora.nats.co.uk/htmlAIP/Publications/2022-02-24-AIRAC/html/index-en-GB.html
            // https://www.aurora.nats.co.uk/htmlAIP/Publications/2022-02-24-AIRAC/html/eAIP/EG-AD-2.EGHI-en-GB.html#EGHI-AD-2.13
            assertAll("SOTON declaration parameters (No obstruction)",
                    () -> assertEquals(1723, runway.getTora(), "TORA value wrong"),
                    () -> assertEquals(1831, runway.getToda(), "TODA value wrong"),
                    () -> assertEquals(1723, runway.getAsda(), "ASDA value wrong"),
                    () -> assertEquals(1650, runway.getLda(), "LDA value wrong"));
        }

        @Test
        void testPrettyName() {
            assertEquals("EGHI - Southampton", sotonAirport.getPrettyName());
        }

        @Test
        void testGetRunwayByDesignator() {
            assertAll(
                    () -> assertSame(sotonAirportRunway02, sotonAirport.getRunwayByDesignator("02")),
                    () -> assertNull(sotonAirport.getRunwayByDesignator("05")));
        }

    }
}
