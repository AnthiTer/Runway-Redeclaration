package uk.ac.soton.seg.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import uk.ac.soton.seg.model.Calc.FlightModes;

public class CalcTest {
    @Nested
    class HeathrowCalcTest {
        // https://secure.ecs.soton.ac.uk/noteswiki/images/Heathrow_Scenarios_New.pdf

        private Runway runway09R;
        private Runway runway27L;
        private Runway runway09L;
        private Runway runway27R;

        @BeforeEach
        void setup() {
            this.runway09R = new Runway("09R", 3660, 3660, 3660, 3353);
            this.runway27L = new Runway("27L", 3660, 3660, 3660, 3660);

            this.runway09L = new Runway("09L", 3902, 3902, 3902, 3595);
            this.runway09L.setThresholdDisplacement(306);
            this.runway27R = new Runway("27R", 3884, 3962, 3884, 3884);
        }

        @Test
        void testNullObstacle() {
            var obstacle = new Obstacle("test", 12, 0, 0);
            assertAll(
                    () -> assertThrows(NullPointerException.class, () -> new Calc(runway09L, null)),
                    () -> assertThrows(NullPointerException.class, () -> new Calc(null, obstacle)),
                    () -> assertThrows(NullPointerException.class, () -> new Calc(null, obstacle)));

        }

        /**
         * 12m tall obstacle, on the centreline, 50m before the 09L threshold, i.e. to
         * the west of the threshold. The same obstacle is 3646m from the 27R threshold.
         * 
         * In scenario 1 we also test for presence of explanations and the getRunway
         * method on the CalculatedParameters. Testing for
         * correctness of explanations is not in scope of these unit tests.
         */
        @Nested
        @DisplayName("Scenario 1")
        class Scenario1 {

            private Obstacle obstacle;
            private Calc calculator09L;
            private Calc calculator27R;

            @BeforeEach
            void setup() {
                obstacle = new Obstacle("test", 12, 0, 0);
                calculator09L = new Calc(runway09L, obstacle);
                calculator27R = new Calc(runway27R, obstacle);
            }

            @Test
            @DisplayName("landOverObs")
            void testLandOverObs() {
                var result = calculator09L.doCalculation(Calc.FlightModes.LAND_OVER, -50, -1);
                assertAll(
                        () -> assertEquals(3346, result.getTora()),
                        () -> assertEquals(3346, result.getToda()),
                        () -> assertEquals(3346, result.getAsda()),
                        () -> assertEquals(2985, result.getLda()));
            }

            @Test
            @DisplayName("landTowardsObs")
            void testLandTowardsObs() {
                var result = calculator27R.doCalculation(Calc.FlightModes.LAND_TOWARDS, 3646, 0);
                assertAll(
                        () -> assertEquals(2986, result.getTora()),
                        () -> assertEquals(2986, result.getToda()),
                        () -> assertEquals(2986, result.getAsda()),
                        () -> assertEquals(3346, result.getLda()));
            }

            @Test
            void CheckExplanationsPresent() {
                var result = calculator27R.doCalculation(Calc.FlightModes.LAND_TOWARDS, 3646, 0);
                assertAll(
                        () -> assertNotNull(result.getToraExplanation()),
                        () -> assertNotNull(result.getTodaExplanation()),
                        () -> assertNotNull(result.getAsdaExplanation()),
                        () -> assertNotNull(result.getLdaExplanation()));
            }

            @Test
            void checkGetRunwayFromResultTowards() {
                var result = calculator27R.doCalculation(Calc.FlightModes.LAND_TOWARDS, 3646, 0);
                assertAll(
                        () -> assertInstanceOf(Runway.class, result.getRunway()),
                        () -> assertSame(runway27R, result.getRunway()),
                        () -> assertEquals(0,
                                // = result.getRunway().getThresholdDisplacement()
                                result.getThresholdDisplacement()));
            }

            @Test
            void checkGetRunwayFromResultAway() {
                var result = calculator27R.doCalculation(Calc.FlightModes.LAND_OVER, -50, -1);
                assertAll(
                        () -> assertInstanceOf(Runway.class, result.getRunway()),
                        () -> assertSame(runway27R, result.getRunway()),
                        () -> assertEquals(610,
                                // = result.getRunway().getTora() - result.getLda()
                                result.getThresholdDisplacement()));
            }
        }

        /**
         * 25m tall obstacle, 20m south of the centerline, 500m from the 27L threshold
         * and 2853m from 09R threshold.
         */
        @Nested
        @DisplayName("Scenario 2")
        class Scenario2 {

            private Obstacle obstacle;
            private Calc calculator09R;
            private Calc calculator27L;

            @BeforeEach
            void setup() {
                obstacle = new Obstacle("25 meter tall obstacle", 25, 0, 0);
                calculator09R = new Calc(runway09R, obstacle);
                calculator27L = new Calc(runway27L, obstacle);
            }

            @Test
            @DisplayName("landOverObs")
            void testLandOverObs() {
                var result = calculator27L.doCalculation(Calc.FlightModes.LAND_OVER, 500, -20);
                assertAll(
                        () -> assertEquals(2860, result.getTora()),
                        () -> assertEquals(2860, result.getToda()),
                        () -> assertEquals(2860, result.getAsda()),
                        () -> assertEquals(1850, result.getLda()));
            }

            @Test
            @DisplayName("landTowardsObs")
            void testLandTowardsObs() {
                var result = calculator09R.doCalculation(Calc.FlightModes.LAND_TOWARDS, 2853, 20);
                assertAll(
                        () -> assertEquals(1850, result.getTora()),
                        () -> assertEquals(1850, result.getToda()),
                        () -> assertEquals(1850, result.getAsda()),
                        () -> assertEquals(2553, result.getLda()));
            }
        }

        /**
         * 15m tall obstacle, 60m north of centreline, 150m from 09R threshold and 3203m
         * from 27L threshold.
         */
        @Nested
        @DisplayName("Scenario 3")
        class Scenario3 {

            private Obstacle obstacle;
            private Calc calculator09R;
            private Calc calculator27L;

            @BeforeEach
            void setup() {
                obstacle = new Obstacle("15 meter tall obstacle", 15, 0, 0);
                calculator09R = new Calc(runway09R, obstacle);
                calculator27L = new Calc(runway27L, obstacle);
            }

            @Test
            @DisplayName("landOverObs")
            void testLandOverObs() {
                var result = calculator09R.doCalculation(Calc.FlightModes.LAND_OVER, 150, -60);
                assertAll(
                        () -> assertEquals(2903, result.getTora()),
                        () -> assertEquals(2903, result.getToda()),
                        () -> assertEquals(2903, result.getAsda()),
                        () -> assertEquals(2393, result.getLda()));
            }

            @Test
            @DisplayName("landTowardsObs")
            void testLandTowardsObs() {
                var result = calculator27L.doCalculation(Calc.FlightModes.LAND_TOWARDS, 3203, 60);
                assertAll(
                        () -> assertEquals(2393, result.getTora()),
                        () -> assertEquals(2393, result.getToda()),
                        () -> assertEquals(2393, result.getAsda()),
                        () -> assertEquals(2903, result.getLda()));
            }
        }

        /**
         * 20m tall obstacle, 20m right of centreline (to the north), 50m from 27R
         * threshold and 3546m from 09L threshold.
         */
        @Nested
        @DisplayName("Scenario 4")
        class Scenario4 {

            private Obstacle obstacle;
            private Calc calculator27R;
            private Calc calculator09L;

            @BeforeEach
            void setup() {
                obstacle = new Obstacle("20 meter tall obstacle", 20, 0, 0);
                calculator27R = new Calc(runway27R, obstacle);
                calculator09L = new Calc(runway09L, obstacle);
            }

            @Test
            @DisplayName("landOverObs")
            void testLandOverObs() {
                var result = calculator27R.doCalculation(Calc.FlightModes.LAND_OVER, 50, 20);
                assertAll(
                        () -> assertEquals(3534, result.getTora()),
                        () -> assertEquals(3612, result.getToda()),
                        () -> assertEquals(3534, result.getAsda()),
                        () -> assertEquals(2774, result.getLda()));
            }

            @Test
            @DisplayName("landTowardsObs")
            void testLandTowardsObs() {
                var result = calculator09L.doCalculation(Calc.FlightModes.LAND_TOWARDS, 3546, -20);
                assertAll(
                        () -> assertEquals(2792, result.getTora()),
                        () -> assertEquals(2792, result.getToda()),
                        () -> assertEquals(2792, result.getAsda()),
                        () -> assertEquals(3246, result.getLda()));
            }
        }

    }

    @Nested
    class CalcUtilityTests {
        @Test
        void testAlsDistanceCalculation() {
            assertAll(
                    () -> assertEquals(50, Calc.calcAlsDistance(1)),
                    () -> assertEquals(500, Calc.calcAlsDistance(10)));
        }

        @Test
        void testFlightModeToString() {
            assertAll(
                    () -> assertEquals("Land over/take-off away", FlightModes.LAND_OVER.toString()),
                    () -> assertEquals("Land/take-off towards", FlightModes.LAND_TOWARDS.toString()));
        }
    }
}
