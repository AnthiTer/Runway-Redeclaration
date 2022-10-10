package uk.ac.soton.seg.ui;

import java.io.IOException;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import uk.ac.soton.seg.util.BearingInputFilter;
import uk.ac.soton.seg.util.DesignatorInputFilter;
import uk.ac.soton.seg.util.IntegerInputFilter;

public class RunwaySettings extends BorderPane {
    private static final InnerShadow UNSAVED_FIELD_EFFECT = new InnerShadow(10, Color.ORANGE);
    private DoubleProperty bearing = new SimpleDoubleProperty();
    private StringProperty remarks = new SimpleStringProperty();
    private StringProperty designator = new SimpleStringProperty();
    private IntegerProperty dTORA = new SimpleIntegerProperty();
    private IntegerProperty dTODA = new SimpleIntegerProperty();
    private IntegerProperty dASDA = new SimpleIntegerProperty();
    private IntegerProperty dLDA = new SimpleIntegerProperty();

    @FXML
    private TextField bearingTextField;

    @FXML
    private TextField remarksTextField;

    @FXML
    private TextField designatorTextField;

    @FXML
    private TextField toraTextField;

    @FXML
    private TextField todaTextField;

    @FXML
    private TextField asdaTextField;

    @FXML
    private TextField ldaTextField;

    @FXML
    public Button saveButton;

    private TextFormatter<Integer> toraFormatter;
    private ObjectProperty<Double> bearingPropertyasObject;
    private ObjectProperty<Integer> todaPropertyAsObject;
    private ObjectProperty<Integer> toraPropertyAsObject;
    private ObjectProperty<Integer> asdaPropertyAsObject;
    private ObjectProperty<Integer> ldaPropertyAsObject;

    public RunwaySettings() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(RunwaySettings.class.getResource("RunwaySettings.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        TextFormatter<Double> bearingFormatter = new TextFormatter<Double>(new DoubleStringConverter(), 0d,
                new BearingInputFilter());
        bearingTextField.setTextFormatter(bearingFormatter);
        bearingPropertyasObject = bearingProperty().asObject();
        bearingFormatter.valueProperty().bindBidirectional(bearingPropertyasObject);
        bearingFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                bearingTextField.setText("0.0");
                newValue = 0d;
            }
            if (oldValue != null && !oldValue.equals(newValue)) {
                bearingTextField.setEffect(UNSAVED_FIELD_EFFECT);
                showSaveButton();
            }
            bearingProperty().set(newValue);
        });

        // var distanceFormatter = new DecimalFormat();
        // distanceFormatter.setGroupingUsed(false);
        // distanceFormatter.setParseIntegerOnly(true);
        // TODO sort out errors when wrong number format

        TextFormatter<String> remarksFormatter = new TextFormatter<String>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }
            @Override
            public String fromString(String string) {
                return string;
            }
        });
        remarksTextField.setTextFormatter(remarksFormatter);
        remarksTextField.textProperty().bindBidirectional(remarksProperty());
        remarksProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null && !oldValue.equals(newValue)) {
                remarksTextField.setEffect(UNSAVED_FIELD_EFFECT);
                showSaveButton();
            }
        });

        TextFormatter<String> designatorFormatter = new TextFormatter<String>(new StringConverter<String>() {

            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }

        }, "", new DesignatorInputFilter());
        designatorTextField.setTextFormatter(designatorFormatter);
        designatorTextField.textProperty().bindBidirectional(designatorProperty());
        designatorProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null && !oldValue.equals(newValue)) {
                designatorTextField.setEffect(UNSAVED_FIELD_EFFECT);
                showSaveButton();
            }
        });

        toraFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, new IntegerInputFilter());
        toraTextField.setTextFormatter(toraFormatter);
        toraPropertyAsObject = toraProperty().asObject();
        toraFormatter.valueProperty().bindBidirectional(toraPropertyAsObject);
        toraFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                toraTextField.setText("0");
                newValue = 0;
            }
            if (oldValue != null && !oldValue.equals(newValue)) {
                toraTextField.setEffect(UNSAVED_FIELD_EFFECT);
                showSaveButton();
            }
            toraProperty().set(newValue);
            // bodge
            // TODO figure out why bidirectional bind isn't really working (see also other ".set(newValue)")
        });

        var todaFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, new IntegerInputFilter());
        todaTextField.setTextFormatter(todaFormatter);
        todaPropertyAsObject = todaProperty().asObject();
        todaFormatter.valueProperty().bindBidirectional(todaPropertyAsObject);
        todaFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                todaTextField.setText("0");
                newValue = 0;
            }
            if (oldValue != null && !oldValue.equals(newValue)) {
                todaTextField.setEffect(UNSAVED_FIELD_EFFECT);
                showSaveButton();
            }
            todaProperty().set(newValue);
        });

        var asdaFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, new IntegerInputFilter());
        asdaTextField.setTextFormatter(asdaFormatter);
        asdaPropertyAsObject = asdaProperty().asObject();
        asdaFormatter.valueProperty().bindBidirectional(asdaPropertyAsObject);
        asdaFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                asdaTextField.setText("0");
                newValue = 0;
            }
            if (oldValue != null && !oldValue.equals(newValue)) {
                asdaTextField.setEffect(UNSAVED_FIELD_EFFECT);
                showSaveButton();
            }
            asdaProperty().set(newValue);
        });

        var ldaFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, new IntegerInputFilter());
        ldaTextField.setTextFormatter(ldaFormatter);
        ldaPropertyAsObject = ldaProperty().asObject();
        ldaFormatter.valueProperty().bindBidirectional(ldaPropertyAsObject);
        ldaFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                ldaTextField.setText("0");
                newValue = 0;
            }
            if (oldValue != null && !oldValue.equals(newValue)) {
                ldaTextField.setEffect(UNSAVED_FIELD_EFFECT);
                showSaveButton();
            }
            ldaProperty().set(newValue);
        });
        Platform.runLater(() -> saveButton.setDisable(true));
    }

    private void showSaveButton() {
        saveButton.setDisable(false);
    }

    public void resetChangeMarkers() {
        Stream.of(bearingTextField, designatorTextField, toraTextField, todaTextField, asdaTextField, ldaTextField)
                .forEach((TextField f) -> {
                    f.setEffect(null);
                });

    }

    public final DoubleProperty bearingProperty() {
        return bearing;
    }

    public final StringProperty remarksProperty() {
        return remarks;
    }

    public final StringProperty designatorProperty() {
        return designator;
    }

    public final IntegerProperty toraProperty() {
        return dTORA;
    }

    public final IntegerProperty todaProperty() {
        return dTODA;
    }

    public final IntegerProperty asdaProperty() {
        return dASDA;
    }

    public final IntegerProperty ldaProperty() {
        return dLDA;
    }

    public ObjectProperty<EventHandler<ActionEvent>> onSaveActionProperty() {
        return saveButton.onActionProperty();
    }

    public EventHandler<ActionEvent> getOnSaveAction() {
        return saveButton.getOnAction();
    }

    public void setOnSaveAction(EventHandler<ActionEvent> value) {
        saveButton.setOnAction(value);
    }

    public boolean hasUnsavedItems() {
        return !saveButton.disabledProperty().get();
    }

}
