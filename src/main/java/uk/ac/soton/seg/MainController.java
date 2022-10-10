package uk.ac.soton.seg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import uk.ac.soton.seg.App.Themes;
import uk.ac.soton.seg.event.ExportXML;
import uk.ac.soton.seg.event.ImportXML;
import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Calc;
import uk.ac.soton.seg.model.Calc.CalculatedParameters;
import uk.ac.soton.seg.model.Calc.FlightModes;
import uk.ac.soton.seg.model.Model;
import uk.ac.soton.seg.model.Obstacle;
import uk.ac.soton.seg.model.Runway;
import uk.ac.soton.seg.model.RunwayParameterProvider;
import uk.ac.soton.seg.ui.SideCanvas;
import uk.ac.soton.seg.ui.TopCanvas;
import uk.ac.soton.seg.util.*;

public class MainController implements Initializable {
    private static Logger log = LogManager.getLog(MainController.class.getName());

    private Airport airport;
    private Runway selectedRunway;
    private Obstacle selectedObstacle;
    private FlightModes selectedFlightMode;
    private RunwayParameterProvider currentParameters;

    /**
     * true if the obstacle is to be placed on the right of the runway
     */
    private boolean directionIsRight;

    // Obstacle values for its position on selected runway
    private int obstacleDistCentre;
    private int obstacleDistThres;


    /**
     * Length of drawn runway in javafx units 
     */
    private static final double RUNWAY_DEFAULT_LENGTH = 350d;

    @FXML
    private BorderPane bPaneParent;

    // MenuBar buttons

    @FXML
    private CheckMenuItem rotateRunwayCheck;

    @FXML
    private CheckMenuItem showAeroplaneCheck;

    @FXML
    private CheckMenuItem showCGAreaCheck;

    @FXML
    private CheckMenuItem showDistMarkersCheck;

    @FXML
    private RadioMenuItem topViewYScaleHalfx;

    @FXML
    private RadioMenuItem topViewYScale1x;

    @FXML
    private RadioMenuItem topViewYScale2x;

    @FXML
    private RadioMenuItem topViewYScale5x;

    // Left sidebar items

    @FXML
    private Text currentAirport;

    @FXML
    private ComboBox<Runway> runwaySelectionBox;

    @FXML
    private ComboBox<Obstacle> obstacleSelectionBox;

    @FXML
    private ToggleGroup directionToggle;

    @FXML
    private RadioButton directionToggleButton1;

    @FXML
    private RadioButton directionToggleButton2;

    /** Text field for distance the obstacle is from the centre of the runway */
    @FXML
    private TextField distCentre;

    /** Text field for distance the obstacle is from the runway threshold */
    @FXML
    private TextField distThres;

    /** Combo box for which mode of takeoff/landing we are using */
    @FXML
    private ComboBox<Calc.FlightModes> flightModeBox;

    // right sidebar tabs
    // original/results
    @FXML
    private Text originalParamsText;

    @FXML
    private Text calculationOutText;

    // breakdown

    @FXML
    private Text breakdownFlightType;

    @FXML
    private Text breakdownExplanationTora;

    @FXML
    private Text breakdownExplanationToda;

    @FXML
    private Text breakdownExplanationAsda;

    @FXML
    private Text breakdownExplanationLda;

    
    // Canvas tabs and Canvases
    @FXML
    private Tab topTab;

    @FXML
    private TopCanvas topCanvas;

    @FXML
    private Tab sideTab;

    @FXML
    private SideCanvas sideCanvas;

    @FXML
    private Tab simultaneousTab;

    private VBox simVBox;

    @FXML
    private TopCanvas topCanvasSim;

    @FXML
    private SideCanvas sideCanvasSim;
    
    // Notifications/info/error panes at bottom
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox errorvbox;
    
    @FXML
    private ScrollPane notifPane;

    @FXML
    private VBox notifvbox;

    public MainController() {
    }

    public void setModel(Airport airport, ObservableList<Obstacle> obstacles) {
        this.airport = airport;
        currentAirport.setText(airport.getPrettyName());

        runwaySelectionBox.setItems(this.airport.getRunways());
        runwaySelectionBox.setConverter(new RunwayStringConverter(airport));

        obstacleSelectionBox.setItems(obstacles);
        obstacleSelectionBox.setConverter(new ObstacleStringConverter(obstacles));

        var distCentreFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, new IntegerInputFilter());
        distCentre.setTextFormatter(distCentreFormatter);
        distCentreFormatter.valueProperty().addListener((o, oldValue, newValue) -> {if (newValue != null) {setObstacleDistanceCentre(newValue);}});
        // distCentreFormatter.valueProperty().bindBidirectional(ldaProperty().asObject());

        var distThresFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, new IntegerInputFilter(true));
        distThres.setTextFormatter(distThresFormatter);
        distThresFormatter.valueProperty().addListener((o, oldValue, newValue) -> {if (newValue != null) {setObstacleDistanceThres(newValue);}});

        flightModeBox.getItems().setAll(Calc.FlightModes.values());
        flightModeBox.setOnAction((e) -> {
            sideCanvas.setState(flightModeBox.getValue());
            topCanvas.setState(flightModeBox.getValue());
            if (selectedRunway != null)
                updateSideCanvas(selectedRunway);
        });

    }

    @FXML
    private void exportCalcsToTxt(ActionEvent e) {
        var fileChooser = new FileChooser();
        // TODO refactor all FileChooser stuff into methods in Util
        fileChooser.setTitle("Export Calculations");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Text files", "*.txt"));
        // String dateTime =
        // DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
        String dateTime = DateTimeFormatter.ofPattern("uuuu-MM-dd-HHmmss").format(LocalDateTime.now());
        fileChooser.setInitialFileName(String.format("calculations-%1s.txt", dateTime));
        File selectedFile = fileChooser.showSaveDialog(App.mainStage);
        try {
            try (FileWriter writer = new FileWriter(selectedFile)) {
                writer.write("# Runway Calculations\n");
                writer.write("> " + DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.now()));
                writer.write("\n\n> Landing mode: ");
                writer.write(breakdownFlightType.getText());
                writer.write("\n\n> ");
                writer.write(calculationOutText.getText().replaceAll("\n", "\n>\n> "));

                writer.write("\n\n## TORA\n```\n");
                writer.write(breakdownExplanationTora.getText());
                writer.write("\n```\n## TODA\n```\n");
                writer.write(breakdownExplanationToda.getText());
                writer.write("\n```\n## ASDA\n```\n");
                writer.write(breakdownExplanationAsda.getText());
                writer.write("\n```\n## LDA\n```\n");
                writer.write(breakdownExplanationLda.getText());
                writer.write("\n```\n");
            }
        } catch (IOException ex) {
            log.error("error saving: " + ex.getMessage());
        }
    }

    @FXML
    private void exportCanvasImages(ActionEvent e) {

        var fileChooser = new FileChooser();
        fileChooser.setTitle("Export Calculations");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG files", "*.png"));

        String dateTime = DateTimeFormatter.ofPattern("uuuu-MM-dd-HHmmss").format(LocalDateTime.now());
        fileChooser.setInitialFileName(String.format("snapshot-%1s.png", dateTime));
        File selectedFile = fileChooser.showSaveDialog(App.mainStage);
        if (selectedFile == null)
            return;

        WritableImage snapshot;
        if (topTab.isSelected()) {
            snapshot = topCanvas.snapshot(null, null);
        } else {
            snapshot = sideCanvas.snapshot(null, null);
        }

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "PNG", selectedFile);
        } catch (IOException ex) {
            log.error("error saving: " + ex.getMessage());
        }
    }

    @FXML
    private void exportReport(ActionEvent e) {

        var fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("HTML files", "*.html"));

        String dateTime = DateTimeFormatter.ofPattern("uuuu-MM-dd-HHmmss").format(LocalDateTime.now());
        fileChooser.setInitialFileName(String.format("report-%1s.html", dateTime));
        File selectedFile = fileChooser.showSaveDialog(App.mainStage);
        if (selectedFile == null)
            return;

        var topView = SwingFXUtils.fromFXImage(topCanvas.snapshot(null, null),null);
        var sideView = SwingFXUtils.fromFXImage(sideCanvas.snapshot(null, null), null);

        HtmlReport report;
        if (currentParameters instanceof CalculatedParameters) {
            var tempParams = (CalculatedParameters) currentParameters;
            report = new HtmlReport(airport, topView, sideView, tempParams, selectedFlightMode);
        } else {
            report = new HtmlReport(airport, topView, sideView, null, null);

        }
        try {
            report.write(selectedFile);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @FXML
    private void setRunwayRotation(ActionEvent e) {
        redraw();
    }

    @FXML
    private void setShowAeroplane(ActionEvent e) {
        redraw();
    }

    @FXML
    private void setCGArea(ActionEvent e) {
        redraw();
    }

    @FXML
    private void setDistMarkers(ActionEvent e) {
        redraw();
    }

    @FXML
    private void redrawFromEvent(ActionEvent e) { 
        redraw();
    }
    @FXML
    private void setRunway(ActionEvent a) throws IOException {
        this.selectedRunway = runwaySelectionBox.getValue();
        this.currentParameters = runwaySelectionBox.getValue();
        if (selectedRunway != null) {
            updateTopCanvas(selectedRunway);
            updateSideCanvas(selectedRunway);
        }
    }

    @FXML
    private void setObstacle(ActionEvent e) {
        selectedObstacle = obstacleSelectionBox.getValue();
        updateTopCanvas(selectedRunway, selectedObstacle, obstacleDistCentre, obstacleDistThres);
        updateSideCanvas(selectedRunway, selectedObstacle, obstacleDistCentre, obstacleDistThres);
    }

    @FXML
    private void setDirection(ActionEvent e) {
        this.directionIsRight = (((RadioButton) directionToggle.getSelectedToggle()).getText().equals("R"));
        log.info("directionIsRight: " + this.directionIsRight);
        log.info(((RadioButton) directionToggle.getSelectedToggle()).getText());
        if (selectedObstacle != null || selectedRunway != null) {
            updateTopCanvas(selectedRunway, selectedObstacle, obstacleDistCentre, obstacleDistThres);
            updateSideCanvas(selectedRunway, selectedObstacle, obstacleDistCentre, obstacleDistThres);
        }
    }

    private void setObstacleDistanceCentre(int newValue) {
        obstacleDistCentre = newValue;
        if (selectedObstacle != null || selectedRunway != null) {
            updateTopCanvas(selectedRunway, selectedObstacle, obstacleDistCentre, obstacleDistThres);
            updateSideCanvas(selectedRunway, selectedObstacle, obstacleDistCentre, obstacleDistThres);
        }
    }

    private void setObstacleDistanceThres(int newValue) {
        obstacleDistThres = newValue;
        if (selectedObstacle != null || selectedRunway != null) {
            updateTopCanvas(selectedRunway, selectedObstacle, obstacleDistCentre, obstacleDistThres);
            updateSideCanvas(selectedRunway, selectedObstacle, obstacleDistCentre, obstacleDistThres);
        }
    }

    /**
     * Redraw top canvas to a given runway
     *
     * @param parameterProvider provides the runway parameters we will draw
     */
    private void updateTopCanvas(RunwayParameterProvider parameterProvider) {
        Runway runway = parameterProvider.getRunway();
        if (runway != null) {
            var newBearing = rotateRunwayCheck.isSelected() ? runway.getBearing() : 90;
            // log.info("Setting bearing to: " + newBearing);
            topCanvas.setBearing(newBearing);
            topCanvas.setShowAeroplane(showAeroplaneCheck.isSelected());
            topCanvas.setDrawGradedArea(showCGAreaCheck.isSelected());
            topCanvas.setDistMarkers(showDistMarkersCheck.isSelected());
            topCanvas.setName(runway.getDesignator());

            double xScale = RUNWAY_DEFAULT_LENGTH / (double) Util.maxDeclaredDistance(runway);

            topCanvas.setTODAlength(parameterProvider.getToda() * xScale);
            topCanvas.setTORAlength(parameterProvider.getTora() * xScale);
            topCanvas.setASDAlength(parameterProvider.getAsda() * xScale);
            topCanvas.setLDAlength(parameterProvider.getLda() * xScale);
            topCanvas.setRESAlength(runway.getRESA() * xScale);
            topCanvas.setDisplacedThreshold(parameterProvider.getThresholdDisplacement() * xScale);
            topCanvas.setBlastLength(runway.getBlastProt() * xScale);
            topCanvas.setStripEndLength(runway.getStripEnd() * xScale);
            topCanvas.setStopwayLength(runway.getStopway() * xScale);
            topCanvas.setClearwayLength(runway.getClearway() * xScale);

            topCanvas.setRunwayLength(RUNWAY_DEFAULT_LENGTH);
            double yScale = getUserWidthScale() * xScale;
            topCanvas.setRunwayHeight(50d * yScale);
            topCanvas.setVisible(true);
        }
    }

    private void updateTopCanvas(RunwayParameterProvider parameterProvider, Obstacle obstacle) {
        Runway runway = parameterProvider.getRunway();
        double xScale = RUNWAY_DEFAULT_LENGTH / (double) Util.maxDeclaredDistance(runway);
        double yScale = getUserWidthScale() * xScale;
        // Top view of an obstacle doesn't show the height
        topCanvas.setObstacleWidth(selectedObstacle.getLength() * xScale);
        topCanvas.setObstacleHeight(selectedObstacle.getWidth() * yScale);
        updateTopCanvas(parameterProvider);
    }

    private double getUserWidthScale() {
        if (topViewYScaleHalfx.isSelected()) return 0.5d;
        if (topViewYScale1x.isSelected()) return 1d;
        if (topViewYScale2x.isSelected()) return 2d;
        if (topViewYScale5x.isSelected()) return 5d;
        return 1d;
    }

    private void updateTopCanvas(RunwayParameterProvider parameterProvider, Obstacle obstacle, int obstacleDist, int obstacleThres) {
        if (parameterProvider != null) {
            Runway runway = parameterProvider.getRunway();
            double xScale = RUNWAY_DEFAULT_LENGTH / (double) Util.maxDeclaredDistance(runway);
            topCanvas.setObstacleDistance((directionIsRight ? 1 : -1) * (int) (obstacleDist * xScale));
            topCanvas.setObstacleThres((int) (obstacleThres * xScale));
            updateTopCanvas(parameterProvider, obstacle);
        }
    }

    /**
     * Redraw side canvas to a given runway
     *
     * @param parameterProvider Runway to draw
     */
    private void updateSideCanvas(RunwayParameterProvider parameterProvider) {
        Runway runway = parameterProvider.getRunway();
        if (runway != null) {
            sideCanvas.setName(runway.getDesignator());
            sideCanvas.setDistMarkers(showDistMarkersCheck.isSelected());
            sideCanvas.setShowAeroplane(showAeroplaneCheck.isSelected());

            double xScale = RUNWAY_DEFAULT_LENGTH / (double) Util.maxDeclaredDistance(runway);

            sideCanvas.setTODAlength(parameterProvider.getToda() * xScale);
            sideCanvas.setTORAlength(parameterProvider.getTora() * xScale);
            sideCanvas.setASDAlength(parameterProvider.getAsda() * xScale);
            sideCanvas.setLDAlength(parameterProvider.getLda() * xScale);
            sideCanvas.setRESAlength(runway.getRESA() * xScale);
            sideCanvas.setBlastLength(runway.getBlastProt() * xScale);
            sideCanvas.setStripEndLength(runway.getStripEnd() * xScale);
            sideCanvas.setDisplacedThreshold(parameterProvider.getThresholdDisplacement() * xScale);
            sideCanvas.setBlastLength(runway.getBlastProt() * xScale);
            sideCanvas.setStopwayLength(runway.getStopway() * xScale);
            sideCanvas.setClearwayLength(runway.getClearway() * xScale);
            sideCanvas.setRunwayLength(RUNWAY_DEFAULT_LENGTH);
            // sideCanvas.setVisible(true);
        }
    }

    private void updateSideCanvas(RunwayParameterProvider parameterProvider, Obstacle obstacle) {
        Runway runway = parameterProvider.getRunway();

        double xScale = RUNWAY_DEFAULT_LENGTH / (double) Util.maxDeclaredDistance(runway);
        // side view of an obstacle doesn't show width
        sideCanvas.setObstacleHeight(40);
        sideCanvas.setObstacleWidth(selectedObstacle.getLength() * xScale);
        updateSideCanvas(parameterProvider);
    }

    private void updateSideCanvas(RunwayParameterProvider parameterProvider, Obstacle obstacle, int obstacleDist, int obstacleThres) {
        if (parameterProvider != null) {
            Runway runway = parameterProvider.getRunway();

            double xScale = RUNWAY_DEFAULT_LENGTH / (double) Util.maxDeclaredDistance(runway);
            sideCanvas.setObstacleDistance((int) (obstacleDist * xScale));
            sideCanvas.setObstacleThres((int) (obstacleThres * xScale));
            updateSideCanvas(parameterProvider, obstacle);
        }
    }

    /**
     * Redraw the top and side views, based on current controller state
     */
    private void redraw() {
        if (currentParameters == null)
            return;
        if (selectedObstacle == null) {
            updateTopCanvas(currentParameters);
            updateSideCanvas(currentParameters);
        } else {
            updateTopCanvas(currentParameters, selectedObstacle, obstacleDistCentre, obstacleDistThres);
            updateSideCanvas(currentParameters, selectedObstacle, obstacleDistCentre, obstacleDistThres);
        }
    }

    @FXML
    private void doCalculation(ActionEvent e) {
        log.info("Attempting new calculation");
        selectedFlightMode = flightModeBox.getValue();
        var calcFailed = false;
        if (selectedRunway == null) {
            log.error("User error: no runway selected for calculation");
            calcFailed = true;

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Missing data");

            error.setContentText("No runway selected for calculation");
            error.showAndWait();
        }
        else if (selectedObstacle == null) {
            log.error("User error: no obstacle selected for calculation");
            calcFailed = true;
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Missing data");

            error.setContentText("No obstacle selected for calculation");
            error.showAndWait();
        }
        else if (selectedFlightMode == null) {
            log.error("User error: no flight mode selected for calculation");
            calcFailed = true;

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Missing data");

            error.setContentText("No flight mode selected for calculation");
            error.showAndWait();
        }
        else if (calcFailed) {
            log.error("Calculation failed");
            return;
        }
        // TODO output error messages to UI log
        log.info("Selected flight mode: " + selectedFlightMode);

        Calc calc = new Calc(selectedRunway, selectedObstacle);

        var result = calc.doCalculation(selectedFlightMode, obstacleDistThres, obstacleDistCentre);

        if (result.getTora() < 100 && result.getToda() < 100 && result.getAsda() < 100 && result.getLda() < 100) {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Invalid calculations");
            errorMessage.setContentText("Some or all parameters are zero so the runway isn't usable in this mode");
            errorMessage.showAndWait();

            log.error("Invalid calculations");
        }

        var originalParamsString = String.format("TORA: %d\nTODA: %d\nASDA: %d\nLDA: %d", selectedRunway.getTora(),
                selectedRunway.getToda(), selectedRunway.getAsda(),
                selectedRunway.getLda());
        var newParamsString = String.format("TORA: %d\nTODA: %d\nASDA: %d\nLDA: %d", result.getTora(), result.getToda(), result.getAsda(),
        result.getLda());

        log.info("INFO: calculation results: \n" + newParamsString); // TODO UI log here

        originalParamsText.setText(originalParamsString);
        calculationOutText.setText(newParamsString);

        breakdownFlightType.setText(selectedFlightMode.toString());

        breakdownExplanationTora.setText(result.getToraExplanation());
        breakdownExplanationToda.setText(result.getTodaExplanation());
        breakdownExplanationAsda.setText(result.getAsdaExplanation());
        breakdownExplanationLda.setText(result.getLdaExplanation());

        sideCanvas.setVisible(true);
        sideCanvas.setState(selectedFlightMode);
        topCanvas.setVisible(true);

        this.currentParameters = result;
        updateTopCanvas(result);
        updateSideCanvas(result);
    }

    @FXML
    private void goStartMenu(ActionEvent e) throws IOException {
        App.GoToView.startMenu();
    }

    @FXML
    private void goObstacles(ActionEvent e) throws IOException {
        App.GoToView.obstacleView();
    }

    @FXML
    private void goAirports(ActionEvent e) throws IOException {
        App.GoToView.airportEditor();
    }

    @FXML
    private void clearAllFields(ActionEvent a) {
        runwaySelectionBox.setValue(null);
        obstacleSelectionBox.setValue(null);
        directionToggle.selectToggle(null);
        distCentre.clear();
        distThres.clear();
        flightModeBox.setValue(null);
        topCanvas.setVisible(false);
        sideCanvas.setVisible(false);
        breakdownFlightType.setText(null);
        originalParamsText.setText(null);
        calculationOutText.setText(null);
        breakdownExplanationTora.setText(null);
        breakdownExplanationToda.setText(null);
        breakdownExplanationAsda.setText(null);
        breakdownExplanationLda.setText(null);
    }

    @FXML
    public void onSimTabSelect(Event e) throws IOException {
        if (simultaneousTab.isSelected()) {
            Separator separator = new Separator(Orientation.HORIZONTAL);
            simVBox = new VBox(topCanvas,separator,sideCanvas);
            topCanvas.widthProperty().bind(simultaneousTab.getTabPane().widthProperty());
            topCanvas.heightProperty().bind(simultaneousTab.getTabPane().heightProperty().divide(2));
            sideCanvas.widthProperty().bind(simultaneousTab.getTabPane().widthProperty());
            sideCanvas.heightProperty().bind(simultaneousTab.getTabPane().heightProperty().divide(2));
            simultaneousTab.setContent(simVBox);
            UIHelper.scale(topCanvas,1);
            UIHelper.scale(sideCanvas,0.6);
        } else {
            simVBox = null;
            simultaneousTab.setContent(null);
            var vbox1 = new VBox(topCanvas);
            var vbox2 = new VBox(sideCanvas);
            topCanvas.widthProperty().bind(topTab.getTabPane().widthProperty());
            topCanvas.heightProperty().bind(topTab.getTabPane().heightProperty());
            sideCanvas.widthProperty().bind(sideTab.getTabPane().widthProperty());
            sideCanvas.heightProperty().bind(sideTab.getTabPane().heightProperty());
            log.info(String.valueOf(topTab.getContent()));
            topTab.setContent(vbox1);
            sideTab.setContent(vbox2);
            UIHelper.scale(topCanvas,1);
            UIHelper.scale(sideCanvas,1);
        }
        redraw();
        UIHelper.zoom(topCanvas,1,0,0);
        UIHelper.zoom(sideCanvas,1,0,0);
    }

    @FXML
    public void onNormalTabSelect(Event e) throws IOException {
        if (topTab.getTabPane() != null) {
            topCanvas.widthProperty().bind(topTab.getTabPane().widthProperty());
            topCanvas.heightProperty().bind(topTab.getTabPane().heightProperty());
            sideCanvas.widthProperty().bind(sideTab.getTabPane().widthProperty());
            sideCanvas.heightProperty().bind(sideTab.getTabPane().heightProperty());
            topTab.setContent(topCanvas);
            sideTab.setContent(sideCanvas);
        }
        redraw();
    }

    /***
     * Displays initial notifications list
     */
    public void setNotifs(){

        log.info("setting notifications");

        LogManager.getLog().getNotifs()
                .addListener((Change<? extends String> c) -> {
                    while (c.next())
                        c.getAddedSubList()
                        .stream()
                        .map(s -> LogManager.STACK_TRACE_LOG_PATTERN.matcher(s).replaceFirst("")) // remove class and method from ui
                        .map((s) -> new Text(s))
                        .forEach(notifvbox.getChildren()::add);                    
                });
        notifvbox.getChildren().addListener((Change<? extends Node> c) -> {
            notifPane.applyCss();
            notifPane.layout();
            notifPane.setVvalue(notifPane.getVmax());
        });
                //         .addListener(
                // (javafx.collections.ListChangeListener.Change<? extends String> change) -> {
                //     if (change.next()) {
                //         updateNotifs();
                //     }
                // });

        // notifvbox.getChildren().clear();
        // updateNotifs();

    }

    /***
     * Updates error list display
     */
    public void updateErrors(){
        errorvbox.getChildren().clear();

        for(String err: LogManager.getLog().getErrors()){

            Text errorText = new Text(err );

            errorvbox.getChildren().add(errorText);
            errorvbox.setAlignment(Pos.TOP_LEFT);
            errorvbox.setSpacing(10);

        }

    }

    /***
     * Displays initial error list
     */
    public void setErrors(){
        LogManager.getLog().getErrors().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> change) {
                if (change.next()) {
                    updateErrors();
                }
            }});

        for(String err: LogManager.getLog().getErrors()){
             // remove class and method from ui

            Text errorText = new Text(LogManager.STACK_TRACE_LOG_PATTERN.matcher(err).replaceFirst(""));

            errorvbox.getChildren().add(errorText);
            errorvbox.setAlignment(Pos.TOP_CENTER);
            errorvbox.setSpacing(20);

        }

    }

    private void setTheme(Themes theme){
        bPaneParent.getStylesheets().clear();
        bPaneParent.getStylesheets().add(theme.stylesheetUrl());
        App.setTheme(theme);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // runwaySelectionBox.setCellFactory();

        // Function<Runway, String> transform = (Runway runway) -> {runway.getName();};

        // setModel(Model.getModel().getAirports().getAirportByICAO(ICAOLocationIndicator));
        // runwaySelectionBox.setItems(model.getRunwaysProperty());
        // flightModeBox.getItems().setAll(Calc.FlightModes.values());

        // Bind canvas size to their tab containers
        topCanvas.widthProperty().bind(topTab.getTabPane().widthProperty());
        topCanvas.heightProperty().bind(topTab.getTabPane().heightProperty());
        sideCanvas.widthProperty().bind(sideTab.getTabPane().widthProperty());
        sideCanvas.heightProperty().bind(sideTab.getTabPane().heightProperty());

        setNotifs();
        setErrors();
    }


    public void expAir(ActionEvent actionEvent) {
        ExportXML expXML = App.xml;
        FileChooser fChooser = new FileChooser();
        fChooser.setTitle("Export File");
        fChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML format(*.xml)","*.xml"));
        File f = fChooser.showSaveDialog(App.mainStage);
        if (f == null)
            return;

        if(!f.getName().contains(".xml"))
            f = new File(f.getAbsolutePath()+".xml");

        boolean checker = expXML.exportAirport(airport,f);
        //where do I get the info for the airport and its runways?

        if (checker) {
            Alert infoMessage = new Alert(Alert.AlertType.INFORMATION);
            infoMessage.setHeaderText("Exportation Successful");
            infoMessage.setContentText("The airport have been successfully exported to an XML file.");
            infoMessage.showAndWait();

            log.info("Airport was exported successfully");
        }
        else {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Exportation Unsuccessful");
            errorMessage.setContentText("Something went wrong while trying to export the airport to an XML file.");
            errorMessage.showAndWait();

            log.error("Exporting went wrong");
        }
    }

    public void expObs(ActionEvent actionEvent) {
        ExportXML expXML = App.xml;
        FileChooser fChooser = new FileChooser();
        fChooser.setTitle("Export File");
        fChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML format(*.xml)","*.xml"));
        File f = fChooser.showSaveDialog(App.mainStage);
        if (f == null)
            return;

        if(!f.getName().contains(".xml"))
            f = new File(f.getAbsolutePath()+".xml");

        boolean checker = expXML.exportObstacle(selectedObstacle,f);
        //where do I get the info for the obstacle?

        if (checker) {
            Alert infoMessage = new Alert(Alert.AlertType.INFORMATION);
            infoMessage.setHeaderText("Exportation Successful");
            infoMessage.setContentText("The obstacle have been successfully exported to an XML file.");
            infoMessage.showAndWait();

            log.info("Obstacle was exported successfully");
        }
        else {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Exportation Unsuccessful");
            errorMessage.setContentText("Something went wrong while trying to export the obstacle to an XML file.");
            errorMessage.showAndWait();

            log.error("Exporting went wrong");
        }

    }

    public void impAir(ActionEvent actionEvent) {
        ImportXML impXML = App.xml;
        FileChooser fChooser = new FileChooser();
        fChooser.getExtensionFilters().add(new ExtensionFilter("XML files", "*.xml"));
        fChooser.setTitle("Choose file to import");
        File f = fChooser.showOpenDialog(App.mainStage);
        if (f == null)
            return;
        Airport airport = impXML.importAirport(f);
        boolean checker = Model.getModel().getAirports().add(airport);

        if (checker) {
            Alert infoMessage = new Alert(Alert.AlertType.INFORMATION);
            infoMessage.setHeaderText("Importation Successful");
            infoMessage.setContentText("The " + airport.getName() + " airport from file " + f.getName() + " has been successfully imported.");
            infoMessage.showAndWait();

            log.info("Imported Airport : " + airport.getName());
            airport.getRunways()
                    .forEach(x -> log.info("With runway : " + x.toString()));
        } else {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Importation Unsuccessful");
            errorMessage.setContentText("Something went wrong while trying to import the airport.");
            errorMessage.showAndWait();

            log.error("Importation failed");
        }
    }

    public void impObs(ActionEvent actionEvent) {
        ImportXML impXML = App.xml;
        FileChooser fChooser = new FileChooser();
        fChooser.getExtensionFilters().add(new ExtensionFilter("XML files", "*.xml"));
        fChooser.setTitle("Choose file to import");
        File f = fChooser.showOpenDialog(App.mainStage);
        if (f == null)
            return;
        List<Obstacle> obslist = impXML.importObstacle(f);
        boolean checker = Model.getModel().getObstacles().addAll(obslist);

        if (checker) {
            Alert infoMessage = new Alert(Alert.AlertType.INFORMATION);
            infoMessage.setHeaderText("Importation Successful");
            infoMessage.setContentText("The obstacle(s) have been successfully imported.");
            infoMessage.showAndWait();

            log.info("Obstacle(s) imported successfully");
        }
        else {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Importation Unsuccessful");
            errorMessage.setContentText("Something went wrong while trying to import the obstacle(s).");
            errorMessage.showAndWait();

            log.error("Importation failed");
        }
    }
}
