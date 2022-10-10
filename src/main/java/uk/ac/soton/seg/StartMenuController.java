package uk.ac.soton.seg;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.util.Duration;
import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.ui.SettingPane;
import uk.ac.soton.seg.util.AirportStringConverter;
import uk.ac.soton.seg.util.LogManager;
import uk.ac.soton.seg.util.Logger;

public class StartMenuController implements Initializable {
    private static Logger log = LogManager.getLog(StartMenuController.class.getName());

    ObservableList<Airport> model;

    @FXML
    ComboBox<Airport> airportChooserBox;

    private boolean settingsIsAnimating;

    @FXML
    SettingPane settingsPane;

    public void setModel(ObservableList<Airport> model) {
        this.model = model;
        airportChooserBox.setItems(model);
        airportChooserBox.setConverter(new AirportStringConverter(model));
    }

    @FXML
    private void startCalculationView(ActionEvent a) throws IOException {
        App.GoToView.calculationView(airportChooserBox.getValue());
    }

    @FXML
    private void editAirports() throws IOException {
        App.GoToView.airportEditor();
    }

    @FXML
    private void editObstacles() throws IOException {
        App.GoToView.obstacleView();
    }

    @FXML
    private synchronized void showSettings() throws IOException {
        if (settingsIsAnimating)
            return;
        settingsIsAnimating = true;
        if (settingsPane.disableProperty().get()) {
            openSettings();
        } else {
            closeSettings();
        }
    }

    private synchronized void openSettings() throws IOException {
        TranslateTransition open = new TranslateTransition(Duration.millis(500), settingsPane);
        settingsPane.disableProperty().set(true);

        open.setFromY(500);
        open.setToY(0);
        open.setOnFinished((e) -> {
            settingsPane.disableProperty().set(false);
            settingsIsAnimating = false;
        });
        open.play();
    }

    private void closeSettings() {
        closeSettings(false);
    }

    private synchronized void closeSettings(boolean instant) {
        TranslateTransition close = new TranslateTransition(Duration.millis(instant ? 0 : 500), settingsPane);
        close.setFromY(0);
        close.setToY(500);
        close.setOnFinished((e) -> {
            settingsPane.disableProperty().set(true);
            settingsIsAnimating = false;
        });
        close.play();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeSettings();

        // setModel(Model.getModel().getAirports());
        // airportChooserBox.getCellFactory();
    }
}
