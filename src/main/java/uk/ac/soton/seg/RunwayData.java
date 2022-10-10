package uk.ac.soton.seg;

public class RunwayData {
    public String designator;

    public RunwayData(String designator, double trueBearing, String runwayDimensions, String runwaySurface,
            String coordinates, String highestElevation, String slope, String swyDimensions, String clearwayDimensions,
            String stripDimensions,
            String resaDimensions, String arrestingSystem, String ofz, String remarks) {
        this.designator = designator;
        this.trueBearing = trueBearing;
        this.runwayDimensions = runwayDimensions;
        this.runwaySurface = runwaySurface;
        this.coordinates = coordinates;
        this.highestElevation = highestElevation;
        this.slope = slope;
        this.swyDimensions = swyDimensions;
        this.stripDimensions = stripDimensions;
        this.resaDimensions = resaDimensions;
        this.arrestingSystem = arrestingSystem;
        this.ofz = ofz;
        this.remarks = remarks;
    }

    public double trueBearing;
    public String runwayDimensions;
    public String runwaySurface;
    public String coordinates;
    public String highestElevation;
    public String slope;
    public String swyDimensions;
    public String clearwayDimensions;
    public String stripDimensions;
    public String resaDimensions;
    public String arrestingSystem;
    public String ofz;
    public String remarks;
}
