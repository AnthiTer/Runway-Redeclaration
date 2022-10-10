package uk.ac.soton.seg.model;

/**
 * Represents something that has runway parameter values (TORA, TODA, ASDA and
 * LDA). Could be representing a Runway, or the result of a calculation
 * (Calc.CalculatedParameters)
 */
public interface RunwayParameterProvider {
    /**
     * @return take-off run available (in meters)
     */
    public int getTora();

    /**
     * @return take-off distance available (in meters)
     */
    public int getToda();

    /**
     * @return accelerate-stop distance available (in meters)
     */
    public int getAsda();

    /**
     * @return Landing distance available (in meters)
     */
    public int getLda();

    /**
     * @return the runway that these distances are associated with (note that the
     *         original declared distances of the runway could be different)
     */
    public Runway getRunway();

    /**
     * @return get the distance the threshold is displaced by (in meters)
     */
    public int getThresholdDisplacement();
}
