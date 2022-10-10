package uk.ac.soton.seg;

/**
 * Launcher class used because JavaFX won't let us make an uber jar where the
 * main method is in a class extending Application for some reason.
 * Only used when launching from a shaded (uber) jar
 * 
 * @see https://openjfx.io/openjfx-docs
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}