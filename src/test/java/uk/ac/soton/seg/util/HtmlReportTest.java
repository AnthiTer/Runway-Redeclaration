package uk.ac.soton.seg.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Calc;
import uk.ac.soton.seg.model.Calc.CalculatedParameters;
import uk.ac.soton.seg.model.Obstacle;
import uk.ac.soton.seg.model.Runway;

@TestMethodOrder(OrderAnnotation.class)
public class HtmlReportTest {
    private Airport airport;
    private CalculatedParameters result;
    private File f = new File("report-test.html");

    @BeforeEach
    void setup() {
        airport = new Airport("EGLL", "HEATHROW");
        var obstacle = new Obstacle("test obstacle", 12, 0, 0);
        var runway09L = new Runway("09L", 3902, 3902, 3902, 3595);
        airport.getRunways().add(runway09L);
        runway09L.setThresholdDisplacement(306);

        var calculator09L = new Calc(runway09L, obstacle);

        result = calculator09L.doCalculation(Calc.FlightModes.LAND_OVER, -50, -1);
    }

    @Test
    @Order(1)
    void testWrite() throws IOException {
        var htmlReport = new HtmlReport(airport, null, null, result, Calc.FlightModes.LAND_OVER);

        assertDoesNotThrow(() -> htmlReport.write(f));
        assertDoesNotThrow(() -> Jsoup.parse(f, null));

    }

    @Test
    @Order(2)
    void testWrittenReport() throws IOException {
        var doc = Jsoup.parse(f, null);
        assertAll(
                () -> assertNotNull(doc.title()),
                () -> assertNotEquals("", doc.title()));

        assertAll(
                () -> assertEquals(1, doc.getElementsByTag("h1").size()),
                () -> assertEquals(3, doc.getElementsByTag("h2").size()),
                () -> assertEquals(6, doc.getElementsByTag("h3").size()));
    }

}
