package uk.ac.soton.seg.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class RunwayTest {
    private Airport sotonAirport;
    private Runway sotonAirportRunway02;
    private Runway sotonAirportRunway02a;

    @BeforeEach
    void setupSoton() {
        this.sotonAirport = new Airport("EGHI", "Southampton");
        sotonAirportRunway02 = new Runway("02", 1723, 1831, 1723, 1650);
        sotonAirportRunway02.setBearing(019.38);
        sotonAirport.addRunway(sotonAirportRunway02);

        sotonAirportRunway02a = new Runway();
        sotonAirportRunway02a.setDesignator("02");
        sotonAirportRunway02a.setTora(1723);
        sotonAirportRunway02a.setToda(1831);
        sotonAirportRunway02a.setAsda(1723);
        sotonAirportRunway02a.setLda(1650);

        // runways = () ->
    }

    static class RunwayProvider implements ArgumentsProvider {
        public RunwayProvider() {
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            var sotonAirportRunway02a = new Runway();
            sotonAirportRunway02a.setDesignator("02");
            sotonAirportRunway02a.setTora(1723);
            sotonAirportRunway02a.setToda(1831);
            sotonAirportRunway02a.setAsda(1723);
            sotonAirportRunway02a.setLda(1650);
            sotonAirportRunway02a.setBearing(019.38);
            sotonAirportRunway02a.setRemarks("test remarks");

            var sotonAirportRunway02 = new Runway("02", 1723, 1831, 1723, 1650);
            sotonAirportRunway02.setBearing(019.38);
            sotonAirportRunway02.setRemarks("test remarks");

            return Stream.of(Arguments.of(sotonAirportRunway02), Arguments.of(sotonAirportRunway02a));
        }

    };

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetTORA(Runway runway) {
        assertEquals(1723, runway.getTora());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetTODA(Runway runway) {
        assertEquals(1831, runway.getToda());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetASDA(Runway runway) {
        assertEquals(1723, runway.getAsda());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetLDA(Runway runway) {
        assertEquals(1650, runway.getLda());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetBearingDefault(Runway runway) {
        assertEquals(019.38, runway.getBearing());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetBearing(Runway runway) {
        runway.setBearing(60.01);
        assertEquals(60.01, runway.getBearing());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetDesignator(Runway runway) {
        assertAll(
                () -> assertEquals("02", runway.getDesignator()),
                () -> {
                    var testRunway = new Runway("02L", 1723, 1831, 1723, 1650);
                    assertEquals("02L", testRunway.getDesignator());
                });

    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void getThreshold(Runway runway) {
        assertEquals(73, runway.getThresholdDisplacement());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetStopway(Runway runway) {
        // Southampton has no stopway on either runway
        assertAll(
                () -> assertEquals(0, runway.getStopway()),
                () -> assertEquals(runway.getAsda() - runway.getTora(),
                        runway.getStopway()));
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetClearway(Runway runway) {
        assertAll(
                () -> assertEquals(108, runway.getClearway()),
                () -> assertEquals(runway.getToda() - runway.getTora(),
                        runway.getClearway()));
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testGetRunway(Runway runway) {
        assertSame(runway, runway.getRunway());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testPrettyName(Runway runway) {
        assertEquals("02: test remarks", runway.prettyName());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testResa(Runway runway) {
        assertEquals(240, runway.getRESA());
        runway.setResa(400);
        assertEquals(400, runway.getRESA());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testStripEnd(Runway runway) {
        assertEquals(60, runway.getStripEnd());
        var newValue = 400;
        runway.setStripEnd(newValue);
        assertEquals(newValue, runway.getStripEnd());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testBlastProt(Runway runway) {
        assertEquals(300, runway.getBlastProt());
        var newValue = 350;
        runway.setBlastProt(newValue);
        assertEquals(newValue, runway.getBlastProt());
    }

    @ParameterizedTest
    @ArgumentsSource(RunwayProvider.class)
    void testEnforcedCustomThresholdDisplacementRange(Runway runway) {
        var old = (runway.getTora() - runway.getLda());
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> runway.setThresholdDisplacement(old + 10)),
                () -> assertDoesNotThrow(() -> runway.setThresholdDisplacement(old + 1)));

        assertThrows(IllegalArgumentException.class, () -> runway.setThresholdDisplacement(300));
        var tempTora = runway.getTora();
        runway.setTora(0);
        assertDoesNotThrow(() -> runway.setThresholdDisplacement(300));
        runway.setLda(0);
        assertDoesNotThrow(() -> runway.setThresholdDisplacement(300));
        runway.setTora(tempTora);
        assertDoesNotThrow(() -> runway.setThresholdDisplacement(300));
    }
}
