package uk.ac.soton.seg.model;

public class Calc {
    private final Runway runway;
    private Obstacle obstacle;
    private final static int RESA_DISTANCE = 240;
    private final static int[] ALS_RATIO = { 1, 50 };

    public static enum FlightModes {
        /** Land over obstacle or take off away from obstacle */
        // TAKEOFF_AWAY,
        LAND_OVER,

        /** Land towards obstacle or take off towards obstacle */
        LAND_TOWARDS;
        // TAKEOFF_TOWARDS;

        @Override
        public String toString() {
            switch (this) {
                // case TAKEOFF_AWAY:
                case LAND_OVER:
                    return "Land over/take-off away";

                case LAND_TOWARDS:
                    // case TAKEOFF_TOWARDS:
                default:
                    return "Land/take-off towards";

            }
        }
    }

    public class CalculatedParameters implements RunwayParameterProvider {
        private final int tora;
        private final int toda;
        private final int asda;
        private final int lda;
        private String toraExplanation;
        private String todaExplanation;
        private String asdaExplanation;
        private String ldaExplanation;
        private final Runway runway;
        private final FlightModes mode;

        CalculatedParameters(int tora, int toda, int asda, int lda, Runway runway, FlightModes mode) {
            this.tora = Math.max(0, tora);
            this.toda = Math.max(0, toda);
            this.asda = Math.max(0, asda);
            this.lda = Math.max(0, lda);
            this.runway = runway;
            this.mode = mode;
        }

        public int getTora() {
            return tora;
        }

        public int getToda() {
            return toda;
        }

        public int getAsda() {
            return asda;
        }

        public int getLda() {
            return lda;
        }

        public String getToraExplanation() {
            return toraExplanation;
        }

        private void setToraExplanation(String val) {
            this.toraExplanation = val;
        }

        public String getTodaExplanation() {
            return todaExplanation;
        }

        public void setTodaExplanation(String val) {
            todaExplanation = val;
        }

        public String getAsdaExplanation() {
            return asdaExplanation;
        }

        public void setAsdaExplanation(String val) {
            asdaExplanation = val;
        }

        public String getLdaExplanation() {
            return ldaExplanation;
        }

        public void setLdaExplanation(String val) {
            ldaExplanation = val;
        }

        @Override
        public Runway getRunway() {
            return runway;
        }

        @Override
        public int getThresholdDisplacement() {
            return FlightModes.LAND_TOWARDS == mode ? runway.getThresholdDisplacement() : runway.getTora() - getLda();
        }
    }

    public Calc(Runway runway, Obstacle obstacle) {
        this.runway = runway;
        this.obstacle = obstacle;
        if (runway == null) {
            throw new NullPointerException("runway cannot be null");
        }
        if (obstacle == null) {
            throw new NullPointerException("obstacle cannot be null");
        }
    }

    /**
     * Runway end safety area (in here, represents the distance around an obstacle
     * that should be excluded from the runway distances)
     * 
     * @return distance in meters
     */
    private int getResaDistance() {
        // TODO correct RESA
        return RESA_DISTANCE;
    }

    private int getBlastProt() {
        // TODO correct blast protection value
        return runway.getBlastProt();
    }

    /**
     * Make a calculation
     * 
     * x and y start top left corner of obstacle (x axis is along the runway, y axis
     * perpendicular to it)
     * 
     * l (length) is measured along the x axis, w (width) is measured along the y
     * axis, and h (height) is measured along the z axis
     * lda is measured from x=0
     * 
     * @param type                      which mode of calcultion we are doing
     * @param obstacleThresholdDistance x coord of position of obstacle (distance
     *                                  from threshold)
     * @param y                         y coord of position of obstacle
     * @param l                         length of obstacle
     * @param w                         width of obstacle
     * @param h                         height of obstacle
     */
    public CalculatedParameters doCalculation(FlightModes type, int obstacleThresholdDistance, int y) {
        CalculatedParameters out;
        // out = new CalculatedParameters(vals[0], vals[1], vals[2], vals[3], runway);
        switch (type) {
            case LAND_OVER:
            // case TAKEOFF_AWAY:
                // land over
                var tocs = (obstacle.getHeight() * 50) - obstacle.getLength();
                var x = Math.max((Math.max(getResaDistance(), tocs) + runway.getStripEnd()), getBlastProt());
                var newLda = runway.getLda() - obstacleThresholdDistance - x;

                // take-off away
                var newTora = runway.getTora() - (runway.getThresholdDisplacement() + obstacleThresholdDistance
                        + (getResaDistance()) + runway.getStripEnd());
                var newAsda = newTora + runway.getStopway();
                var newToda = newTora + runway.getClearway();

                out = new CalculatedParameters(newTora, newToda, newAsda, newLda, runway, type);
                out.setToraExplanation(
                        String.format("TORA = %1$d - (%2$d + %3$d + %4$d + %5$d) = %6$d", runway.getTora(),
                                runway.getThresholdDisplacement(), obstacleThresholdDistance, getResaDistance(),
                                runway.getStripEnd(), newTora) +
                                "\nTORA = Original TORA - (runway threshold displacement + obstacle distance from threshold + RESA + runway strip end)");
                // "TORA = Original TORA - Blast Protection - Distance from Threshold -
                // Displaced Threshold" );
                out.setAsdaExplanation(
                        String.format("TODA = %1$d + %2$d = %3$d", newTora, runway.getStopway(), newAsda) +
                                "\nTODA = New TORA + STOPWAY");
                out.setTodaExplanation(
                        String.format("ASDA = %1$d + %2$d = %3$d", newTora, runway.getClearway(), newToda) +
                                "\nASDA = New TORA + CLEARWAY");
                out.setLdaExplanation(
                        String.format("LDA = %1$d - %2$d - %3$d = %4$d", runway.getLda(), obstacleThresholdDistance, x,
                                newLda) +
                                "\nLDA = Original LDA - obstacle distance from threshold - x" +
                                "\nwhere x is the largest of:" +
                                "\n    (RESA + Strip end)\t\t"
                                + String.format("(%1$d + %2$d = %3$d)", getResaDistance(), runway.getStripEnd(),
                                        getResaDistance() + runway.getStripEnd())
                                +
                                "\n    (TOCS + Strip end)\t\t"
                                + String.format("(%1$d + %2$d = %3$d)", tocs, runway.getStripEnd(),
                                        tocs + runway.getStripEnd())
                                +
                                "\n    Blast Protection Value\t" + String.format("(%1$d)", getBlastProt()));
                break;

            case LAND_TOWARDS:
            // case TAKEOFF_TOWARDS:
            default:
                // land towards
                var newLda1 = obstacleThresholdDistance - (runway.getStripEnd() + 240);

                // take-off towards
                var tocs1 = (obstacle.getHeight() * 50) - obstacle.getLength();
                var safeDistance1 = Math.max(getResaDistance(), tocs1) + runway.getStripEnd();
                var temp1 = obstacleThresholdDistance + runway.getThresholdDisplacement() - safeDistance1;
                var newTora1 = Math.min(runway.getTora(), temp1);
                var newToda1 = Math.min(runway.getToda(), temp1);
                var newAsda1 = Math.min(runway.getAsda(), temp1);

                out = new CalculatedParameters(newTora1, newToda1, newAsda1, newLda1, runway, type);

                out.setToraExplanation(
                        String.format("TORA = min( %1$d - %2$d - %3$d , %4$d)", obstacleThresholdDistance, tocs1,
                                runway.getStripEnd(), temp1)
                                + "\nTORA = min( original TORA, Distance from Threshold - Slope Calculation - Strip End )");
                out.setTodaExplanation(
                        String.format("TODA = min( %1$d, %2$d )", runway.getToda(), temp1)
                                + "\nTODA = min( original TODA, new TORA )");
                out.setAsdaExplanation(
                        String.format("ASDA = min( %1$d, %2$d )", runway.getAsda(), temp1)
                                + "\nASDA = min( original ASDA, New TORA )");
                out.setLdaExplanation(
                        String.format("LDA = %1$d - %2$d - %3$d", obstacleThresholdDistance, tocs1,
                                runway.getStripEnd())
                                + "\nLDA = Distance from Threshold - TOCS - Strip End");
                break;
        }
        return out;

    }

    /**
     * Get the horizontal distance of ALS (approach/landing surface) from vertical
     * height (of obstacle). Used for calculating 'exclusion' area around obstacle
     * that is unusable
     * 
     * @param h height of ALS (usually of obstacle)
     * @return horizontal length of ALS in meters
     */
    public static int calcAlsDistance(int height) {
        return height * ALS_RATIO[1] / ALS_RATIO[0];
    }
}
