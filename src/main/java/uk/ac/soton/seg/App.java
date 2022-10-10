package uk.ac.soton.seg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Model;
import uk.ac.soton.seg.model.Obstacle;
import uk.ac.soton.seg.util.LogManager;
import uk.ac.soton.seg.util.Logger;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Logger log = LogManager.getLog(App.class.getName());

    public static enum Themes {
        LIGHT("Light", "runwayappLight.css"),
        DARK("Dark", "runwayappDark.css"),
        COLOUR("Colour", "runwayappColour.css");

        private String name;
        private String stylesheet;

        Themes(String name, String stylesheet) {
            this.name = name;
            this.stylesheet = stylesheet;
        }

        @Override
        public String toString() {
            return name;
        }

        public String stylesheetUrl() {
            return App.class.getResource(stylesheet).toExternalForm();
        }
    }

    private static Scene scene;
    public static Stage mainStage;

    public static final XMLHandler xml = new XMLHandler();

    private static Themes globalTheme = Themes.LIGHT;

    public App() {
        super();
        LogManager mainLog = LogManager.getLog();
        mainLog.getNotifs()
                .addListener((Change<? extends String> c) -> {
                    while (c.next())
                        c.getAddedSubList().stream().forEach(System.out::println);
                });
        mainLog.getErrors()
                .addListener((Change<? extends String> c) -> {
                    while (c.next())
                        c.getAddedSubList().stream().forEach(System.out::println);
                });

    }

    @Override
    public void start(Stage stage) throws IOException {
        // scene = new Scene(loadFXML("primary"), 640, 480);
        log.info("Starting app...");
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("StartMenu" + ".fxml"));
        Parent loaded = fxmlLoader.load();
        scene = new Scene(loaded, 1200, 600);
        mainStage = stage;
        stage.setTitle("Runway Redeclaration Tool");
        stage.setScene(scene);
        stage.show();
        ((StartMenuController) fxmlLoader.getController()).setModel(Model.getModel().getAirports());
        setTheme(globalTheme);

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static class GoToView {
        public static void startMenu() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("StartMenu" + ".fxml"));
            Parent loader = fxmlLoader.load();
            scene.setRoot(loader);
            ((StartMenuController) fxmlLoader.getController()).setModel(Model.getModel().getAirports());
        };

        public static void calculationView(Airport airport) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main" + ".fxml"));
            Parent loader = fxmlLoader.load();
            scene.setRoot(loader);

            ((MainController) fxmlLoader.getController()).setModel(airport, Model.getModel().getObstacles());
        };

        public static void airportEditor() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("AirportEditDialog" + ".fxml"));
            Parent loader = fxmlLoader.load();
            scene.setRoot(loader);
            ((AirportEditDialogController) fxmlLoader.getController()).setModel(Model.getModel().getAirports());
        };

        public static void obstacleView() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ObstacleView" + ".fxml"));
            Parent loader = fxmlLoader.load();
            scene.setRoot(loader);
        };

    }

    public static Themes getTheme() {
        return globalTheme;
    }

    public static Scene getScene() {
        return scene;
    }

    public static void setTheme(Themes theme) {
        log.info(String.format("Setting theme to: %s", theme));
        getScene().getStylesheets().clear();
        getScene().getStylesheets().add(theme.stylesheetUrl());
        globalTheme = theme;
    }

    public static void main(String[] args) {
        // Model.getModel().loadFromFile();
        // Model.getModel().saveToFile();

        // getObstacles().addAll(
        //         // new Obstacle("Bus", 4, 10, 10),
        //         new Obstacle("Plane", 10, 55, 80),
        //         new Obstacle("Car", 2, 2, 3),
        //         new Obstacle("Shuttle bus", 4, 3, 7),
        //         new Obstacle("Fire Truck", 4, 3, 6),
        //         new Obstacle("Shipping Crate", 3, 3, 6),
        //         new Obstacle("Felled tree", 2, 10, 10));
        // TODO remove when loading obstacles from xml works

        try (InputStream in = Model.class.getResourceAsStream("predefinedObs.xml");) {
            if (in != null) {
                ArrayList<Obstacle> obs = (ArrayList<Obstacle>) XMLHandler.loadObstacles(in);
                Model.getModel().getObstacles().clear();
                Model.getModel().getObstacles().addAll(obs);
            } else {
                log.error("Error loading predefined obstacles");
            }
        } catch (IOException e) {   
        }

        var xmlHandler = new XMLHandler();
        var airports = Model.getModel().getAirports();

        Stream
                .of("EGKK - LONDON GATWICK.xml", "EGLC - LONDON CITY.xml", "EGLL - LONDON HEATHROW.xml", "EGHI - SOUTHAMPTON.xml")
                .map(App.class::getResourceAsStream)
                .map(xmlHandler::importAirport)
                .forEach(airports::add);
        launch();
    }

}