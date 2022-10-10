package uk.ac.soton.seg.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.soton.seg.model.RunwayParameterProvider;

public class Util {
    private Util() {
    }

    public static int maxDeclaredDistance(RunwayParameterProvider params) {
        return Math.max(Math.max(Math.max(params.getTora(), params.getAsda()), params.getToda()), params.getLda());
    }

    public static void checkPositive(int value) throws IllegalArgumentException {
        if (value < 0) {
            throw new IllegalArgumentException("Distance must be positive");
        }
    }

    public static boolean validateBearing(double d) {
        return d >= 0d && d < 360d;
    }

    public static boolean validateDesignator(String designator) {
        Pattern p = Pattern.compile("^([0-9]+)([LCR])?$");
        Matcher m = p.matcher(designator);
        if (!m.matches()) return false;
        int deducedBearing;
        try {
            deducedBearing = Integer.parseInt(m.group(1));
        } catch (NumberFormatException e) {
            return false;
        }
        var letter = m.group(2);
        return (0 < deducedBearing && deducedBearing <= 36) || deducedBearing == 99;
        
    }

}
