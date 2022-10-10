module uk.ac.soton.seg {
    requires transitive javafx.graphics;
    requires transitive java.desktop;
    requires transitive javafx.controls;

    requires javafx.fxml;
    requires javafx.swing;
    requires org.jsoup;

    opens uk.ac.soton.seg to javafx.fxml;
    opens uk.ac.soton.seg.ui to javafx.fxml;
    opens uk.ac.soton.seg.model;
    opens uk.ac.soton.seg.util;

    exports uk.ac.soton.seg;
    exports uk.ac.soton.seg.ui;
    exports uk.ac.soton.seg.model;
    exports uk.ac.soton.seg.util;
    exports uk.ac.soton.seg.event;
}