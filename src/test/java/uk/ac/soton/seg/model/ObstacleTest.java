package uk.ac.soton.seg.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ObstacleTest {
    Obstacle obstacle;

    @BeforeEach
    void setup() {
        obstacle = new Obstacle("Bus", 1, 2, 3);
    }

    @Test
    @DisplayName("check we can create an obstacle with given dimensions and name")
    void testCreation() {
        assertInstanceOf(Obstacle.class, new Obstacle("Bus 2", 10, 10, 10));
    }

    @Test
    void testGetName() {
        assertEquals("Bus", obstacle.getName());
    }

    @Test
    void testSetName() {
        obstacle.setName("Car");
        assertEquals("Car", obstacle.getName());
    }

    @Test
    void TestGetHeight() {
        assertEquals(1, obstacle.getHeight());
    }

    @Test
    void TestSetHeight() {
        obstacle.setHeight(10);
        assertEquals(10, obstacle.getHeight());
    }

    @Test
    void TestGetWidth() {
        assertEquals(2, obstacle.getWidth());
    }

    @Test
    void TestSetWidth() {
        obstacle.setWidth(20);
        assertEquals(20, obstacle.getWidth());
    }

    @Test
    void TestGetLength() {
        assertEquals(3, obstacle.getLength());
    }

    @Test
    void TestSetLength() {
        obstacle.setLength(30);
        assertEquals(30, obstacle.getLength());
    }

    @Test
    void testSettingValuesTooHigh() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> obstacle.setHeight(10000)),
                () -> assertThrows(IllegalArgumentException.class, () -> obstacle.setWidth(10000)),
                () -> assertThrows(IllegalArgumentException.class, () -> obstacle.setLength(10000)));
    }

    @Test
    void testSettingValuesTooLow() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> obstacle.setHeight(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> obstacle.setWidth(-50)),
                () -> assertThrows(IllegalArgumentException.class, () -> obstacle.setLength(-1)));
    }
}
