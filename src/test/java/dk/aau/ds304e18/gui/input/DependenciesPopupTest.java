package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;


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

    @Test
    void testcloseDependenciesPopupTest01() {
        DependenciesPopup test = new DependenciesPopup(rootPane, listView);
        test.closeDependenciesPopup();
        assertFalse(rootPane.getChildrenUnmodifiable().get(3).isVisible());
    }

    @Test
    void testAddDependency() {
        DependenciesPopup test = new DependenciesPopup(rootPane, listView);
        test.addDependency(Collections.singletonList(new Task(-1, "Hello", 30, 40, null, null, -1, null, 20)));
        assertEquals(1, test.getTaskDependencies().size());
    }

    @Test
    void testRemoveDependency() {
        DependenciesPopup test = new DependenciesPopup(rootPane, listView);
        test.addDependency(Collections.singletonList(new Task(-1, "Hello", 30, 40, null, null, -1, null, 20)));
        test.removeDependency(new ArrayList<>(test.getTaskDependencies()));
        assertEquals(0, test.getTaskDependencies().size());
    }

    @Test
    void testcloseDependenciesPopupTest02() {
        DependenciesPopup test = new DependenciesPopup(rootPane, listView);
        test.openDependenciesPopup();
        ToolBar bar = ((ToolBar) ((FlowPane) rootPane.getChildrenUnmodifiable().get(3)).getChildren().get(2));
        ((HBox) bar.getItems().get(1)).getChildren().get(0).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                0, 0, 0, 0, MouseButton.PRIMARY, 1,
                true, true, true, true, true, true, true, true, true, true, null));
        assertFalse(rootPane.getChildrenUnmodifiable().get(3).isVisible());
    }
}

