package uk.ac.soton.seg;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import uk.ac.soton.seg.event.ImportXML;
import uk.ac.soton.seg.model.Model;
import uk.ac.soton.seg.model.Obstacle;
import uk.ac.soton.seg.ui.ObstaclePane;
import uk.ac.soton.seg.util.LogManager;
import uk.ac.soton.seg.util.Logger;

public class ObstacleViewController implements Initializable {
    private static Logger log = LogManager.getLog(ObstacleViewController.class.getName());



    final ObservableList<Obstacle> obstacles = Model.getModel().getObstacles();
    FilteredList<Obstacle> filteredObs = new FilteredList<>(FXCollections.observableList(obstacles));
    Boolean emptyAdd =false;
    Boolean unsavedChanges =false;
    Boolean obsRemoved = false;
    Boolean newlyAddedObs = false;

    @FXML
    TextField searchObstacle;

    @FXML
    ScrollPane scrollPane;


    @FXML
    Accordion accordion = new Accordion();
    @FXML
    VBox vbox;

    @FXML
    VBox vBoxParent;

    @FXML
    BorderPane borderpane;


    /**
     * Updates displayed obstacles according to searched characters
     * @param a the characters typed in the search bar
     */
    @FXML
    private void displayObstacles (KeyEvent a){
        log.info("Filtering obstacles");
        if(obstacles.size()>1) {
            accordion.getPanes().clear();

            searchObstacle.textProperty().addListener((observable, oldValue, newValue) ->
                    filteredObs.setPredicate(obstaclePredicate(newValue))
            );
            for (Obstacle ob : filteredObs) {
                createObs(ob);
            }
        }

    }

    /**
     * Conveys if searched obstacle exists
     * @param searchedObs typed keys
     * @return obstacles that match the search
     */
    private Predicate<Obstacle> obstaclePredicate(String searchedObs){
        return obstacle -> {
            //If search bar is empty, all obstacles are displayed
            if ( searchedObs.isEmpty() || searchedObs == null ){
                return true;
            }
            return ifObsExists(searchedObs,obstacle);
        };
    }

    /**
     * Checks if searched pattern exists anywhere in list of obstacles
     * @param obstacle the obstacle checked
     * @param searchedObs the searched keys
     * @return true if searched obstacle exists
     */
    private boolean ifObsExists(String searchedObs, Obstacle obstacle){
        //sanitise inputs
        String lookUp = searchedObs.replaceAll("\\s+","");
        return (obstacle.getName().toLowerCase().contains(lookUp.toLowerCase()));
    }


    /**
     * Presents drop-down to enter details of a new obstacle
     * @param a clicked button
     */
    @FXML
    private void addObstacle(ActionEvent a) throws IOException {

        //If no unsaved edited/newly added obstacles present, allow user to add new obstacle
        //Else alert user of unsaved details
        if(!emptyAdd && !unsavedChanges){
            ObstaclePane newOb = new ObstaclePane();
            newOb.resize(accordion.getWidth(), accordion.getHeight());
            accordion.getPanes().add(newOb);
            newOb.setExpanded(true);

            //Signify there is currently an obstacle with unsaved details
            emptyAdd = true;
            unsavedChanges = true;

            newOb.getSave().setOnAction(a1 -> saveNewInfo(newOb));
            newOb.getClear().setOnAction(a1 -> clearFields(newOb));
        }else{
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Unsaved details");
            errorMessage.setContentText("Please save all values for the current obstacle before adding another one");
            errorMessage.showAndWait();
        }

    }

    /**
     * Clears all textfields
     * @param obstaclePane the pane holding the selected obstacle
     */
    private void clearFields(ObstaclePane obstaclePane){
        obstaclePane.getNewObWidth().clear();
        obstaclePane.getNewObLength().clear();
        obstaclePane.getNewObHeight().clear();
    }

    /**
     * Saves newly added obstacle and adds into obstacles list
     * @param obstaclePane pane holding newly added obstacle
     */
    @FXML
    private void saveNewInfo(ObstaclePane obstaclePane)  {
        //Try saving entered values, else alert the user of error
        try {
            //create Obstacle with entered details

            int height = Integer.parseInt(obstaclePane.getNewObHeight().getText());
            int width = Integer.parseInt(obstaclePane.getNewObWidth().getText());
            int length = Integer.parseInt(obstaclePane.getNewObLength().getText());

            //Check obstacle name does not already exist
            if (checkName(obstaclePane.getNewObName().getText())) {
                throw new Exception();
            } else {
                var obstacle = new Obstacle();

                obstacle.setName(obstaclePane.getNewObName().getText());
                obstacle.setLength(length);
                obstacle.setWidth(width);
                obstacle.setHeight(height);

                //Update no more unsaved obstacles
                emptyAdd = false;
                unsavedChanges = false;

                accordion.getPanes().remove(obstaclePane);
                createObs(obstacle);

                Alert saveMessage = new Alert(Alert.AlertType.INFORMATION);
                saveMessage.setHeaderText("Obstacle Added");

                saveMessage.setContentText("New obstacle " + obstaclePane.getNewObName().getText() + " has been saved");
                saveMessage.showAndWait();
            }
        }

        catch (NullPointerException e){

                Alert errorMessage = new Alert(Alert.AlertType.ERROR);
                errorMessage.setHeaderText("Incomplete details");
                errorMessage.setContentText("Please enter all values for the name, height, width, and length");
                errorMessage.showAndWait();

        }catch (NumberFormatException e){

            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Incorrect input format");
            errorMessage.setContentText("Please enter positive integer values for the height, width, and length");
            errorMessage.showAndWait();

        }catch (IllegalArgumentException e){

            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Invalid dimensions");
            errorMessage.setContentText("Please check and re-enter dimension values");
            errorMessage.showAndWait();
        }catch (Exception e){
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Obstacle name already exists");
            errorMessage.setContentText("Please choose another name");
            errorMessage.showAndWait();
        }
    }

    /***
     * Check if obstacle name already exists
     * @param obstacleName
     * @return true if obstacle name already exists
     */
    private boolean checkName(String obstacleName) {
        for(Obstacle obs: obstacles){
            if(!obs.getName().equals(obstacleName)){
                continue;
            }else{
                return true;
            }
        }
        return false;

    }

    /**
     * Saves existing obstacles edited details
     * @param obstaclePane pane holding edited obstacle
     */
    private void saveEditedInfo(ObstaclePane obstaclePane)  {
        //Try saving editing details, else alert the user of error

        try{
            obstaclePane.setText(obstaclePane.getNewObName().getText());
            obstaclePane.setNewObName(obstaclePane.getNewObName().getText());
            obstaclePane.setNewObHeight(obstaclePane.getNewObHeight().getText());
            obstaclePane.setNewObWidth(obstaclePane.getNewObWidth().getText());
            obstaclePane.setNewObLength(obstaclePane.getNewObLength().getText());

            obstaclePane.getObstacle().setName(obstaclePane.getNewObName().getText());
            obstaclePane.getObstacle().setHeight(Integer.parseInt(obstaclePane.getNewObHeight().getText()));
            obstaclePane.getObstacle().setWidth(Integer.parseInt(obstaclePane.getNewObWidth().getText()));
            obstaclePane.getObstacle().setLength(Integer.parseInt(obstaclePane.getNewObLength().getText()));

            setToEdit(obstaclePane,false);

            Alert saveMessage = new Alert(Alert.AlertType.INFORMATION);
            saveMessage.setHeaderText("Obstacle saved");
            saveMessage.setContentText(obstaclePane.getNewObName().getText()+ " has been updated");
            saveMessage.showAndWait();

            log.info("Obstacle '" + obstaclePane.getNewObName().getText()+ "' has been updated");
            unsavedChanges = false;

        }catch (NumberFormatException e){
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Incorrect input format");
            errorMessage.setContentText("Please enter positive integer values for the height, width, and length");
            errorMessage.showAndWait();

        }catch (NullPointerException e){

            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Incomplete details");
            errorMessage.setContentText("Please enter all values for the name, height, width, and length");
            errorMessage.showAndWait();

        }catch (IllegalArgumentException e){

            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setHeaderText("Invalid dimensions");
            errorMessage.setContentText("Please check and re-enter dimension values");
            errorMessage.showAndWait();
        }

    }

    /***
     * Enables the required buttons according to whether the user is currently viewing or editing the obstacle
     * @param obstaclePane pane holding the selected obstacle
     * @param bool refers to if the pane should be set to edit (true) or view (false)
     */
    private void setToEdit(ObstaclePane obstaclePane,boolean bool){
        obstaclePane.getNewObName().setEditable(bool);
        obstaclePane.getNewObName().setFocusTraversable(true);
        obstaclePane.getNewObHeight().setEditable(bool);
        obstaclePane.getNewObWidth().setEditable(bool);
        obstaclePane.getNewObLength().setEditable(bool);

        obstaclePane.getRemove().setDisable(bool);
        obstaclePane.getEdit().setDisable(bool);
        obstaclePane.getClear().setDisable(!bool);
        obstaclePane.getSave().setDisable(!bool);
        unsavedChanges = bool;

    }

    /***
     * Takes user back to the menu screen
     * @param a click of button
     * @throws IOException throws exception if error loading menu screen
     */
    @FXML
    private void startMenu (ActionEvent a) throws IOException {
        //Alerts the user if any unsaved details present or loads menu screen
        if(unsavedChanges){
            Alert errorMessage = new Alert(Alert.AlertType.CONFIRMATION,"Exiting this screen will cause changes to be lost", ButtonType.OK,ButtonType.CANCEL);

            errorMessage.setHeaderText("Unsaved details");
            errorMessage.showAndWait();
            if(errorMessage.getResult() == ButtonType.CANCEL ){

            }else if(errorMessage.getResult() == ButtonType.OK){
                App.GoToView.startMenu();

            }
        }else{
        App.GoToView.startMenu();}
    }


    /**
     * Creates pane to display passed obstacle
     * @param obs Obstacle object to create pane for
     */
    private void createObs(Obstacle obs){
        log.info("creating new obstacle: "+ obs.getName());

        if( !obstacles.contains(obs)) {
            obstacles.add(obs);
            log.info("" + obs.getName() + " was added");
        }
        ObstaclePane obstaclePane = new ObstaclePane(obs);
        obstaclePane.getObstacle().setName(obstaclePane.getNewObName().getText());
        obstaclePane.getObstacle().setHeight(Integer.parseInt(obstaclePane.getNewObHeight().getText()));
        obstaclePane.getObstacle().setWidth(Integer.parseInt(obstaclePane.getNewObWidth().getText()));
        obstaclePane.getObstacle().setLength(Integer.parseInt(obstaclePane.getNewObLength().getText()));
        setToEdit(obstaclePane,false);


        accordion.getPanes().add(obstaclePane);
        obstaclePane.getRemove().setOnAction(a1 -> removeObs(obstaclePane));
        obstaclePane.getEdit().setOnAction(a1 -> editObs(obstaclePane));
        obstaclePane.getSave().setOnAction(a1 -> saveEditedInfo(obstaclePane));
        obstaclePane.getClear().setOnAction(a1 -> clearFields(obstaclePane));

        //If a new obstacle is added, logs it to notifications in main calculation window
        if(newlyAddedObs)
            log.info(""+ "Added obstacle '" + obs.getName()+"'");

    }

    /***
     * Sets pane holding selected obstacle to be editable
     * @param toEdit
     */
    private void editObs(ObstaclePane toEdit){
        log.info("editing: "+ toEdit.getText());
        setToEdit(toEdit,true);

    }

    /***
     * Removes obstacle from the display and list
     * @param toRemove pane holding selected obstacle to remove
     */
    private void removeObs(ObstaclePane toRemove){
        log.info("removing: "+ toRemove.getText());

        log.info("Removed obstacle '" + toRemove.getText()+ "'");
        obsRemoved =true;
        accordion.getPanes().remove(toRemove);

        if(obstacles.contains(toRemove.getObstacle()))
        obstacles.remove(toRemove.getObstacle());


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(Obstacle obs: obstacles){
            createObs(obs);
        }
        newlyAddedObs = true;

        searchObstacle.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.TAB)) {
                    event.consume();
                }
            }
        });

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
