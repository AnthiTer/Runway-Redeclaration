package uk.ac.soton.seg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import uk.ac.soton.seg.App.Themes;
import uk.ac.soton.seg.event.ImportXML;
import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Model;
import uk.ac.soton.seg.model.Runway;
import uk.ac.soton.seg.ui.AirportSettings;
import uk.ac.soton.seg.ui.CustomTitledPane;
import uk.ac.soton.seg.ui.RunwaySettings;
import uk.ac.soton.seg.util.LogManager;
import uk.ac.soton.seg.util.Logger;
import uk.ac.soton.seg.util.Util;

import javax.swing.*;

public class AirportEditDialogController implements Initializable {

    @FXML
    BorderPane bPaneParent;

    private static Logger log = LogManager.getLog(AirportEditDialogController.class.getName());


    private static final class AirportCustomTitledPane extends CustomTitledPane {
        private static Logger log = LogManager.getLog(AirportCustomTitledPane.class.getName());
        private static final class RunwayCustomTitledPane extends CustomTitledPane {
            private static Logger log = LogManager.getLog(RunwayCustomTitledPane.class.getName());
            private final Runway runway;
            private RunwaySettings child = new RunwaySettings();

            private RunwayCustomTitledPane(Runway runway) {
                super();
                this.runway = runway;
            }

            void smallRebuild() {
                this.setText(runway.prettyName());
                this.setSubThingType(null);
            }

            void rebuild() {
                smallRebuild();
                child.bearingProperty().set(runway.getBearing());
                child.remarksProperty().set(runway.getRemarks());
                child.designatorProperty().set(runway.getDesignator());
                child.toraProperty().set(runway.getTora());
                child.todaProperty().set(runway.getToda());
                child.asdaProperty().set(runway.getAsda());
                child.ldaProperty().set(runway.getLda());
                child.resetChangeMarkers();

                this.setChild(child);

                child.setOnSaveAction((e) -> {
                    var anyFailed = false;
                    var bearing = child.bearingProperty().get();
                    if (Util.validateBearing(bearing)) {
                        runway.setBearing(bearing);
                    } else {
                        anyFailed = true;
                    }
                    runway.setRemarks(child.remarksProperty().get());
                    
                    var designator = child.designatorProperty().get();
                    if (Util.validateDesignator(designator)) {
                        runway.setDesignator(designator);
                    } else {
                        anyFailed = true;
                    }
                    runway.setTora(child.toraProperty().get());
                    runway.setToda(child.todaProperty().get());
                    runway.setAsda(child.asdaProperty().get());
                    runway.setLda(child.ldaProperty().get());
                    
                    this.setText(runway.prettyName());
                    if (!anyFailed) {
                        child.resetChangeMarkers();
                        child.saveButton.setDisable(true);
                    } else {
                        log.error("Could not save runway parameters: some values invalid");
                        var alert = new Alert(AlertType.ERROR);
                        alert.setContentText("Could not save runway parameters: some values are invalid");
                        alert.showAndWait();
                    }
                });
                log.info("rebuilt");
            }

            public boolean hasUnsavedItems() {
                return child.hasUnsavedItems();
            }
        }

        private final Airport airport;
        private AirportSettings airportSettings = new AirportSettings();
        private Collection<RunwayCustomTitledPane> runwayPanes = new ArrayList<RunwayCustomTitledPane>();

        private AirportCustomTitledPane(Airport airport) {
            super();
            this.airport = airport;
        }

        void smallRebuild() {
            this.setText(airport.getPrettyName());
            this.setSubThingType("Runway");

        }
        void rebuild() {
            smallRebuild();
            Accordion runwayAccordion = new Accordion();
            airportSettings.identifierProperty().set(airport.getIdentifier());
            airportSettings.nameProperty().set(airport.getName());
            airportSettings.setOnSaveAction((e) -> {
                airport.setIdentifier(airportSettings.identifierProperty().get());
                airport.setName(airportSettings.nameProperty().get());
                airportSettings.resetChangeMarkers();
                this.setText(airport.getPrettyName());
            });
            var vBox = new VBox(airportSettings, runwayAccordion);
            vBox.setSpacing(10);
            this.setChild(vBox);

            for (Runway runway : airport.getRunways()) {
                var runwayPane = new RunwayCustomTitledPane(runway);
                runwayPane.setOnRemoveAction((e) -> {
                    var alert = new Alert(AlertType.CONFIRMATION, String.format("Remove runway %1$s from airport %2$s - %3$s?", runway.getDesignator(), airport.getIdentifier(), airport.getName()));
                    alert
                            .showAndWait()
                            .filter(ButtonType.OK::equals)
                            .ifPresent((response) -> {
                                airport.getRunways().remove(runway);
                            });
                });
                runwayPane.smallRebuild();
                ChangeListener<Boolean> rebuildListener = new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            runwayPane.rebuild();
                            runwayPane.expandedProperty().removeListener(this);
                        }
                    }
                };
                runwayPane.expandedProperty().addListener(rebuildListener);

                runwayPanes.add(runwayPane);
                runwayAccordion.getPanes().add(runwayPane);
            }
            log.info("rebuilt");
        }

        public boolean hasUnsavedItems() {
            return airportSettings
                    .hasUnsavedItems()
                    ||
                    runwayPanes
                            .stream()
                            .map(RunwayCustomTitledPane::hasUnsavedItems)
                            .anyMatch(x -> x);
        }
    }

    ObservableList<Airport> airports;

    @FXML
    Accordion mainAccordion;

    private List<AirportCustomTitledPane> allAirportPanes = new ArrayList<AirportCustomTitledPane>();

    private Accordion airportsAccordion;

    private TextField airportFilterBox;

    public AirportEditDialogController() {

    }

    public void setModel(ObservableList<Airport> airports) {
        this.airports = airports;
        rebuild();
        airports.addListener(new ListChangeListener<Airport>() {
            @Override
            public void onChanged(Change<? extends Airport> c) {
                rebuild();
            }
        });
    }

    @FXML
    private void importXmlPressed(ActionEvent e) {
        ImportXML impXML = App.xml;
        FileChooser fChooser = new FileChooser();
        fChooser.setTitle("Choose file to import");
        fChooser.getExtensionFilters().add(new ExtensionFilter("Airport files", "*.xml"));
        File f = fChooser.showOpenDialog(new Stage());
        if (f == null)
            return;
        Airport airport = impXML.importAirport(f);
        boolean checker = false;

        if(airport != null) {
            checker = Model.getModel().getAirports().add(airport);
        }

        if (checker) {
            Alert infoMessage = new Alert(Alert.AlertType.INFORMATION);
            infoMessage.setHeaderText("Importation Successful");
            infoMessage.setContentText("The " + airport.getName() + " airport from file " + f.getName() + " has been successfully imported.");
            infoMessage.showAndWait();

            log.info("Imported Airport : " + airport.getName());
            airport.getRunways()
                    .forEach(x -> log.info("\twith runway : " + x.toString()));
        }
        else {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Importation Unsuccessful");
            errorMessage.setContentText("Something went wrong while trying to import the airport.");
            errorMessage.showAndWait();

            log.error("Importation failed");
        }

    }

    @FXML
    private void exportXmlPressed(ActionEvent e) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open Airport File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("XML files", "*.xml"));
        File selectedFile = fileChooser.showSaveDialog(App.mainStage);
        if (selectedFile == null)
            return;
        // App.mainStage.display(selectedFile);
        log.info(selectedFile.toString());
        // try {
        //     XMLHandler.saveObjectAsXML(new Wrapper<Airport[]>(((Airport[]) airports.toArray())), selectedFile);
        // } catch (IOException ex) {
        //     //TODO: handle exception
        // }
    }

    @FXML
    private void goStartMenu(ActionEvent e) throws IOException {

        boolean unsavedChanges = this.allAirportPanes.stream().anyMatch(pane -> {
            return pane.hasUnsavedItems();
            // return (pane instanceof RunwaySettings) ? ((RunwaySettings) pane).hasUnsavedItems() : false;
            // return false;
        });
        // Alerts the user if any unsaved details present or loads menu screen
        if (unsavedChanges) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Exiting this screen will cause changes to be lost");
            alert.setHeaderText("Unsaved details");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                App.GoToView.startMenu();
            }
        } else {
            App.GoToView.startMenu();
        }
    }

    public void rebuild() {
        mainAccordion.getPanes().clear();
        var airportsPane = new CustomTitledPane();
        airportsAccordion = new Accordion();
        airportsPane.setText("Airports");
        airportsPane.removeDeleteButton();
        airportFilterBox = new TextField();
        airportFilterBox.setPromptText("Search");
        airportFilterBox.setOnKeyTyped((e) -> {
            reevalFilter();
        });

        ScrollPane scrollPane = new ScrollPane(new VBox(airportsAccordion));
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        var vBox = new VBox(airportFilterBox, scrollPane);
        vBox.setSpacing(10);
        airportsPane.setChild(vBox);
        mainAccordion.getPanes().setAll(airportsPane);

        airportsPane.setSubThingType("Airport");
        airportsPane.setOnAddAction((e) -> this.airports.add(new Airport()));
        airportsAccordion.getPanes().clear();
        allAirportPanes.clear();
        for (Airport airport : this.airports) {
            var airportPane = new AirportCustomTitledPane(airport);

            airportPane.smallRebuild();
            ChangeListener<Boolean> rebuildListener = new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        airportPane.rebuild();
                        airportPane.expandedProperty().removeListener(this);
                    }
                }
            };
            airportPane.expandedProperty().addListener(rebuildListener);
            airportPane.setOnAddAction((e) -> airport.addRunway(new Runway(null, 0, 0, 0, 0)));
            airportPane.setOnRemoveAction((e) -> {
                var alert = new Alert(AlertType.CONFIRMATION, String.format("Remove airport %1$s?", airport.getPrettyName()));
                alert
                        .showAndWait()
                        .filter(ButtonType.OK::equals)
                        .ifPresent((response) -> {
                            this.airports.remove(airport);
                        });
            });
            airport.getRunways().addListener(new ListChangeListener<Runway>() {
                @Override
                public void onChanged(Change<? extends Runway> c) {
                    airportPane.rebuild();
                }
            });
            allAirportPanes.add(airportPane);
        }
        airportsPane.setCollapsible(false);
        mainAccordion.setExpandedPane(airportsPane);
        reevalFilter();
    }


    private void reevalFilter() {
        airportsAccordion.getPanes().clear();
        var toAdd = allAirportPanes
                .stream()
                .filter((pane) -> {
                    return pane.getText().toLowerCase().contains(airportFilterBox.getText().toLowerCase());
                })
                .collect(Collectors.toList());
        airportsAccordion.getPanes().addAll(toAdd);
        airportsAccordion.getPanes().forEach((pane) -> pane.setVisible(true));
    }

    private void setTheme(Themes theme){
        bPaneParent.getStylesheets().clear();
        bPaneParent.getStylesheets().add(theme.stylesheetUrl());
        App.setTheme(theme);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //setTheme(App.getTheme());


        // setModel(Model.getModel().getAirports());
        // airportChooserBox.getCellFactory();
    }

}
