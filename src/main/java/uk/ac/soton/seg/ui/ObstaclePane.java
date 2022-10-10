package uk.ac.soton.seg.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.ac.soton.seg.model.Obstacle;

/**
 * Custom pane representing an obstacle
 */
public class ObstaclePane extends TitledPane {
    Obstacle obstacle;
    TextField newObName;
    TextField newObHeight;
    TextField newObLength;
    TextField newObWidth;
    Button remove;
    Button edit;
    Button save;
    Button clear;

    public ObstaclePane(){
        this.setText("Enter Details: ");
        this.setAlignment(Pos.CENTER);
        newObName = new TextField();
        newObHeight = new TextField();
        newObLength = new TextField();
        newObWidth = new TextField();
        Label name = new Label("Name");
        Label height = new Label("Height (m)");
        Label width = new Label("Width (m)");
        Label length = new Label("Length (m) ");
        AnchorPane anchorPane = new AnchorPane();
        this.setContent(anchorPane);
        anchorPane.resize(this.getWidth(),this.getHeight());
        save = new Button("Save");
        clear = new Button("Clear");



        VBox left = new VBox();
        VBox right = new VBox();
        GridPane gpleft = new GridPane();
        GridPane gpright = new GridPane();

        gpleft.setHgap(20);
        gpleft.setVgap(10);
        gpright.setHgap(20);
        gpright.setVgap(10);

        gpleft.add(name, 0,0);
        gpleft.add(width,0,1);
        gpleft.add(newObName,1,0);
        gpleft.add(newObWidth, 1,1);

        gpright.add(height, 0,0);
        gpright.add(length,0,1);
        gpright.add(newObHeight,1,0);
        gpright.add(newObLength, 1,1);

        left.setSpacing(10);
        right.setSpacing(10);
        left.setFillWidth(true);
        right.setFillWidth(true);

        left.setPadding(new Insets(20));
        right.setPadding(new Insets(20));
        left.getChildren().addAll(gpleft,save);
        right.getChildren().addAll(gpright,clear);

        left.setAlignment(Pos.CENTER);
        right.setAlignment(Pos.CENTER);

        anchorPane.getChildren().addAll(left,right);
        AnchorPane.setLeftAnchor(left,80.0);
        AnchorPane.setRightAnchor(right,80.0);
    }


    public ObstaclePane(Obstacle obs){
        this.obstacle = obs;
        save = new Button("Save");

        clear = new Button("Clear");

        remove = new Button("Delete");
        edit = new Button("Edit");

        HBox hBox = new HBox();
        AnchorPane anchorPane1 = new AnchorPane();
        hBox.setMaxWidth(anchorPane1.getWidth());
        hBox.setAlignment(Pos.CENTER);

        hBox.setSpacing(900);

        HBox hBox2 = new HBox();
        hBox2.setMaxWidth(anchorPane1.getWidth());
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setSpacing(900);

        this.setAlignment(Pos.CENTER);

        //Set up all labels and textfields
        newObName = new TextField(obs.getName());
        newObHeight = new TextField(((Integer) obs.getHeight()).toString());
        newObLength = new TextField(((Integer) obs.getLength()).toString());
        newObWidth = new TextField(((Integer) obs.getWidth()).toString());
        Label name = new Label("Name");
        Label height = new Label("Height (m)");
        Label width = new Label("Width (m)");
        Label length = new Label("Length (m)");
        newObName.setAlignment(Pos.CENTER);
        newObHeight.setAlignment(Pos.CENTER);
        newObLength.setAlignment(Pos.CENTER);
        newObWidth.setAlignment(Pos.CENTER);

        //Add titled pane and set width
        this.setContent(anchorPane1);
        anchorPane1.resize(this.getWidth(),this.getHeight());
        this.setExpanded(true);
        this.setText(obs.getName());

        VBox left = new VBox();
        VBox right = new VBox();
        GridPane gpleft = new GridPane();
        gpleft.setPadding(new Insets(20));
        GridPane gpright = new GridPane();
        gpright.setPadding(new Insets(20));

        gpleft.setHgap(20);
        gpleft.setVgap(10);
        gpright.setHgap(20);
        gpright.setVgap(10);

        gpleft.add(name, 0,0);
        gpleft.add(width,0,1);
        gpleft.add(newObName,1,0);
        gpleft.add(newObWidth, 1,1);

        gpright.add(height, 0,0);
        gpright.add(length,0,1);
        gpright.add(newObHeight,1,0);
        gpright.add(newObLength, 1,1);

        left.setSpacing(10);
        right.setSpacing(10);
        left.setFillWidth(true);
        right.setFillWidth(true);

        left.setPadding(new Insets(20,0,0,0));
        right.setPadding(new Insets(20,0,0,0));
        left.getChildren().addAll(remove,gpleft,save);
        right.getChildren().addAll(edit,gpright,clear);
        left.setAlignment(Pos.CENTER);
        right.setAlignment(Pos.CENTER);

        anchorPane1.getChildren().addAll(left,right);
        AnchorPane.setLeftAnchor(left,80.0);
        AnchorPane.setRightAnchor(right,80.0);
        AnchorPane.setTopAnchor(hBox,0.0);
        AnchorPane.setBottomAnchor(hBox2,0.0);

        newObName.setEditable(false);
        newObLength.setEditable(false);
        newObWidth.setEditable(false);
        newObHeight.setEditable(false);

    }

    public Obstacle getObstacle(){
        return obstacle;
    }

    public Button getRemove(){
        return remove;
    }

    public Button getClear() {
        return clear;
    }

    public Button getSave() {
        return save;
    }

    public Button getEdit() {
        return edit;
    }

    public TextField getNewObHeight() {
        return newObHeight;
    }

    public TextField getNewObLength() {
        return newObLength;
    }

    public TextField getNewObName() {
        return newObName;
    }

    public TextField getNewObWidth() {
        return newObWidth;
    }

    /***
     * Sets new height of obstacle
     * @param newObHeight obstacle height
     */
    public void setNewObHeight(String newObHeight) {
        if(newObHeight.isBlank()) throw new NullPointerException();

        if(newObHeight.contains("-")) throw new NumberFormatException();
        if(Integer.parseInt(newObHeight) <1 ||Integer.parseInt(newObHeight)>1000) throw new IllegalArgumentException();

        this.newObHeight.setText(newObHeight);
    }

    /**
     * Sets new length of obstacle
     * @param newObLength obstacle length
     */
    public void setNewObLength(String newObLength) {
        if(newObLength.isBlank()) throw new NullPointerException();

        if(newObLength.contains("-")) throw new NumberFormatException();
        if(Integer.parseInt(newObLength) <1 ||Integer.parseInt(newObLength)>1000) throw new IllegalArgumentException();

        this.newObLength.setText(newObLength);
    }

    /**
     *Sets name of obstacle
     * @param newObName obstacle name
     */
    public void setNewObName(String newObName) {
        if(newObName.isBlank()) throw new NullPointerException();

        this.newObName.setText(newObName);
    }

    /**
     * Sets width of obstacle
     * @param newObWidth obstacle width
     */
    public void setNewObWidth(String newObWidth) {
        //Throws respective errors if entered string is not in the given range
        if(newObWidth.isBlank()) throw new NullPointerException();

        if(newObWidth.contains("-")) throw new NumberFormatException();
        if(Integer.parseInt(newObWidth) <1 ||Integer.parseInt(newObWidth)>1000) throw new IllegalArgumentException();


        this.newObWidth.setText(newObWidth);
    }
}
