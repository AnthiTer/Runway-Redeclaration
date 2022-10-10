package uk.ac.soton.seg.util;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

public class DesignatorInputFilter implements UnaryOperator<TextFormatter.Change> {
    private Pattern p = Pattern.compile("^[0-9LCR]*$");

    @Override
    public Change apply(Change change) {
        return p.matcher(change.getText()).matches() ? change : null;
    }
}
