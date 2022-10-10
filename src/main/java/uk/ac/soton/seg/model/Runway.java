package uk.ac.soton.seg.model;

import java.util.ArrayList;

import uk.ac.soton.seg.event.RunwayListener;
import uk.ac.soton.seg.util.Util;

/**
 * Class representing a runway that has no obstructions
 */
public class Runway implements RunwayParameterProvider {
    private String designator;
    private int defaultTORA;
    private int defaultTODA;
    private int defaultASDA;
    private int defaultLDA;
    private int resa = 240;
    private int stripEnd = 60;
    private int blastProt = 300;

    /**
     * ALS value: default value from spec.
     */
    private Integer[] als = new Integer[] { 1, 50 };

    /**
     * TOCS value: default value, same as als
     */
    private Integer[] tocs = new Integer[] { 1, 50 };

    private Double bearing = 0d;

    private Integer customDisplacementThreshold = null;

    private ArrayList<RunwayListener> listeners = new ArrayList<RunwayListener>();;
    private String remarks;

    /**
     * Create a Runway with no values.
     */
    public Runway() {
    }

    /**
     * Create a Runway with given designator and default declared distances
     * 
     * @param designator  runway designator
     * @param defaultTORA Take-off Run Available
     * @param defaultTODA Take-off Distance Available
     * @param defaultASDA Accelerate-Stop Distance Available
     * @param defaultLDA  Landing Distance Available
     */
    public Runway(String designator, int defaultTORA, int defaultTODA, int defaultASDA, int defaultLDA) {
        this.designator = designator;
        setTora(defaultTORA);
        setToda(defaultTODA);
        setAsda(defaultASDA);
        setLda(defaultLDA);
    }

    /**
     * @return the runway's designator
     */
    public String getDesignator() {
        return designator;
    }

    /**
     * Set the runway designator.
     * 
     * @param value designator (eg. "02" or "05R")
     */
    public void setDesignator(String value) {
        this.designator = value;
    }

    /**
     * @return the bearing of the the runway's direction in degrees
     */
    public Double getBearing() {
        return bearing;
    }

    /**
     * Set the bearing of the the runway's direction.
     * 
     * @param bearing bearing in degrees
     */
    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }

    @Override
    public int getTora() {
        return defaultTORA;
    }

    @Override
    public int getToda() {
        return defaultTODA;
    }

    @Override
    public int getAsda() {
        return defaultASDA;
    }

    @Override
    public int getLda() {
        return defaultLDA;
    }

    @Override
    public Runway getRunway() {
        return this;
    }

    /**
     * Set a custom threshold displacement, useful for instances where the default
     * (displacement = TORA - LDA) is not used. Note that allowed values must be
     * within 2m either way of this default value
     * 
     * @param val the custom displacement threshold
     */
    public void setThresholdDisplacement(int val) {
        var old = (getTora() - getLda());
        if (Math.abs(old - val) < 2 || getTora() == 0 || getLda() == 0) {
            this.customDisplacementThreshold = val;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int getThresholdDisplacement() {
        return (customDisplacementThreshold == null) ? getTora() - getLda() : customDisplacementThreshold;
    }

    public int getStopway() {
        return getAsda() - getTora();
    }

    public int getClearway() {
        return getToda() - getTora();
    }

    public int getRESA() {
        return resa;
    }

    public int getStripEnd() {
        return stripEnd;
    }

    public int getBlastProt() {
        return blastProt;
    }
    // public Integer[] getALS() { return als; }
    // public Integer[] getTOCS() { return tocs; }

    public void setTora(int value) {
        Util.checkPositive(value);
        this.defaultTORA = value;
    }

    public void setToda(int value) {
        Util.checkPositive(value);
        this.defaultTODA = value;
    }

    public void setAsda(int value) {
        Util.checkPositive(value);
        this.defaultASDA = value;
    }

    public void setLda(int value) {
        Util.checkPositive(value);
        this.defaultLDA = value;
    }

    public void setResa(int resa) {
        Util.checkPositive(resa);
        this.resa = resa;
    }

    public void setStripEnd(int stripEnd) {
        Util.checkPositive(stripEnd);
        this.stripEnd = stripEnd;
    }

    public void setBlastProt(int blastProt) {
        Util.checkPositive(blastProt);
        this.blastProt = blastProt;
    }
    // public void setALS(Integer[] als) { this.als = als; updateListeners();}
    // public void setTOCS(Integer[] tocs) { this.tocs = tocs; updateListeners();}

    /**
     * Set the remarks for the runway (typically notes or information about the
     * runway)
     * 
     * @param remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return Get remarks for the runway (typically notes or information about the
     *         runway)
     */
    public String getRemarks() {
        return remarks == null ? "" : remarks;
    }

    /**
     * @return a string representing the runway in the format: "{designator}:
     *         {remarks}"
     */
    public String prettyName() {
        return getDesignator() + ": " + this.getRemarks();
    }
}
