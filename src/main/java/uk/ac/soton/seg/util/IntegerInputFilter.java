package uk.ac.soton.seg.util;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

/**
 * Filter for unformatted positive integers.
 */
public class IntegerInputFilter implements UnaryOperator<TextFormatter.Change> {
    private final Pattern p;
    private final Pattern pattern1 = Pattern.compile("^[0-9]*$");
    private final Pattern pattern2 = Pattern.compile("^[\\-0-9]*$");

    public IntegerInputFilter() {this(false);}
    public IntegerInputFilter(boolean allowNegative) {
        p = allowNegative ? pattern2 : pattern1;
    }

    @Override
    public TextFormatter.Change apply(Change change) {
        return p.matcher(change.getText()).matches() ? change : null;
    }
}