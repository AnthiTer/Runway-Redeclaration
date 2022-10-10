package uk.ac.soton.seg.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import uk.ac.soton.seg.XMLHandler;

public class ModelTest {
    Model model;

    @BeforeEach
    void getModelInstance() {
        model = Model.getModel();
        model.loadTestData();
    }

    @Test
    void checkIsSingleton() {
        assertAll(
                () -> assertInstanceOf(Model.class, model),
                () -> assertSame(model, Model.getModel()));
    }

    @Test
    void getAirports() {
        assertAll(
                () -> assertInstanceOf(ObservableList.class, model.getAirports()),
                () -> assertInstanceOf(Airport.class, model.getAirports().get(0)));
    }

    @Test
    void getObstacles() {
        assertAll(
                () -> assertInstanceOf(ObservableList.class, model.getObstacles()),
                () -> assertInstanceOf(Obstacle.class, model.getObstacles().get(0)));
    }

    @Test
    void testLoadPredefinedObstacles() throws IOException {
        model.getObstacles().clear();
        assertEquals(0, model.getObstacles().size());
        try (InputStream in = Model.class.getResourceAsStream("predefinedObs.xml");) {
            ArrayList<Obstacle> obs = (ArrayList<Obstacle>) XMLHandler.loadObstacles(in);
            Model.getModel().getObstacles().clear();
            Model.getModel().getObstacles().addAll(obs);
        }
        assertEquals(6, model.getObstacles().size());
        assertEquals(6, new HashSet<Obstacle>(model.getObstacles()).size());
    }
}
