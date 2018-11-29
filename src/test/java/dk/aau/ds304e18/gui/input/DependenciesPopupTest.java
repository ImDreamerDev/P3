package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Task;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class DependenciesPopupTest extends ApplicationTest {
    private Parent rootPane;
    private static ListView listView;

    @BeforeAll
    static void realInit() {
        DatabaseManager.isTests = true;
    }

    @BeforeEach
    void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        try {
            //Load the main.fxml file in the resources folder.
            rootPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BorderPane flowPane = ((BorderPane) rootPane.lookup("#inputFlowPane"));
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        listView = ((ListView<Task>) inputVBox.getChildren().get(11));
    }

    @Test
    void dependenciesPopupConstructorTest01() {
        DependenciesPopup test = new DependenciesPopup(rootPane, listView);
        assertEquals(test.getTaskDependencies(), new ArrayList<>());
    }

    @Test
    void openDependenciesPopupTest01() {
        DependenciesPopup test = new DependenciesPopup(rootPane, listView);
        test.openDependenciesPopup();
        assertTrue(rootPane.getChildrenUnmodifiable().get(3).isVisible());
    }
}

