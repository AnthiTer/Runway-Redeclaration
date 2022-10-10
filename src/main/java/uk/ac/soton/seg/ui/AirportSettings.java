package uk.ac.soton.seg.ui;

import java.io.IOException;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class AirportSettings extends BorderPane {
    private StringProperty identifier = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();

    @FXML
    private TextField identifierTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private Button saveButton;

    public AirportSettings() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(RunwaySettings.class.getResource("AirportSettings.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        identifierTextField.textProperty().bindBidirectional(identifierProperty());
        nameTextField.textProperty().bindBidirectional(nameProperty());

        // when a value is changed, highlight the box until it is saved
        Stream.of(identifierTextField, nameTextField)
                .forEach((TextField f) -> {
                    f.setOnKeyTyped((e) -> {
                        f.setEffect(new InnerShadow(10, Color.ORANGE));
                        saveButton.setDisable(false);
                    });
                });
        Platform.runLater(() -> saveButton.setDisable(true));
    }

    public void resetChangeMarkers() {
        Stream.of(identifierTextField, nameTextField)
                .forEach((TextField f) -> {
                    f.setEffect(null);
                });

    }

    public final StringProperty identifierProperty() {
        return identifier;
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<EventHandler<ActionEvent>> onSaveActionProperty() {
        return saveButton.onActionProperty();
    }

    public EventHandler<ActionEvent> getOnSaveAction() {
        return saveButton.getOnAction();
    }

    public void setOnSaveAction(EventHandler<ActionEvent> value) {
        saveButton.setOnAction((e) -> {
            saveButton.setDisable(true);
            value.handle(e);
        });
    }

    public boolean hasUnsavedItems() {
        return !saveButton.disabledProperty().get();
    }
}
