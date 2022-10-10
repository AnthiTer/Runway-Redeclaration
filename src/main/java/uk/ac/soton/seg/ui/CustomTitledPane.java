package uk.ac.soton.seg.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class CustomTitledPane extends TitledPane {

    @FXML
    private TextField filterTextField;
    @FXML
    private ButtonBar addThingButtonParent;
    @FXML
    private Button addThingButton;
    @FXML
    private Button deleteButton;

    @FXML
    private BorderPane contentArea;

    public CustomTitledPane() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(CustomTitledPane.class.getResource("CustomTitledPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setChild(new Text());
        setAnimated(true);
    }

    public void setChild(Node value) {
        contentArea.setCenter(value);
        // contentArea.getChildren().setAll(value);
        // ((VBox) getContent()).getChildren().set(1,value);
        // setContent(new VBox(buttons, value));
    }

    public void setSubThingType(String thingTypeName) {
        if (addThingButtonParent.getButtons().size() == 0 || addThingButton == null) {
            return; // the ship has already sailed
        }
        if (thingTypeName == null) {
            addThingButtonParent.getButtons().remove(0);
        } else {
            addThingButton.setText("Add " + thingTypeName);
        }
    }

    public EventHandler<ActionEvent> getOnAddAction() {
        return addThingButton.getOnAction();
    }

    public void setOnAddAction(EventHandler<ActionEvent> val) {
        addThingButton.setOnAction(val);
    }

    public EventHandler<ActionEvent> getOnRemoveAction() {
        return deleteButton.getOnAction();
    }

    public void setOnRemoveAction(EventHandler<ActionEvent> value) {
        deleteButton.setOnAction(value);
    }

    public EventHandler<? super KeyEvent> getOnFilterChanged() {
        return filterTextField.getOnKeyTyped();
    }

    public void setOnFilterChanged(EventHandler<KeyEvent> value) {
        filterTextField.setOnKeyTyped(value);
    }

    public void removeDeleteButton() {
        setGraphic(null);
    }
}
