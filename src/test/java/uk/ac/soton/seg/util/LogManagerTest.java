package uk.ac.soton.seg.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class LogManagerTest {
    private static Logger log = LogManager.getLog(LogManagerTest.class.getName());
    private static Logger log2 = LogManager.getLog(null);


    LogManager mainLog = LogManager.getLog();

    @Test
    void testGetLists() {
        log.info("info test 1");
        log.info("info test 2");
        log.info("info test 3");
        log.error("error test 1");
        log.error("error test 2");
        log.error("error test 3");
        log2.info("test log2");
        Pattern p = Pattern.compile("^[0-9]{2}:[0-9]{2}:[0-9]{2} : test log2$");
        Matcher m = p.matcher(mainLog.getNotifs().get(mainLog.getNotifs().size()-1));
        assertAll(
                () -> assertEquals(3, mainLog.getErrors().size()),
                () -> assertEquals(7, mainLog.getNotifs().size()),
                () -> assertTrue(m.matches())
        );
    }

}
