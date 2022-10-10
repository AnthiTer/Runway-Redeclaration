package uk.ac.soton.seg.ui;

import java.io.IOException;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import uk.ac.soton.seg.App;
import uk.ac.soton.seg.App.Themes;

public class SettingPane extends HBox {

    public SettingPane() throws IOException {
        Button light = new Button("Light Mode");
        light.setOnAction(a1 -> setTheme(Themes.LIGHT));
        light.getStyleClass().add("menu-buttons");

        Button dark = new Button("Dark Mode");
        dark.setOnAction(a1 -> setTheme(Themes.DARK));
        dark.getStyleClass().add("menu-buttons");

        Button colour = new Button("Colour Mode");
        colour.getStyleClass().add("menu-buttons");
        colour.setOnAction(a1 -> setTheme(Themes.COLOUR));

        getChildren().addAll(light, dark, colour);
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setStyle("-fx-background-color: transparent");
    }

    private void setTheme(Themes theme) {
        App.setTheme(theme);
    }
}
