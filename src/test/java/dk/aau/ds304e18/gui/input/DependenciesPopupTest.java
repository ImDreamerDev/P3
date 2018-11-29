package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DependenciesPopupTest {

    private static Parent rootPane;
    private static VBox inputVBox;

    @BeforeAll
    static void realInit(){
        DatabaseManager.isTests = true;
    }

    void init() {
        BorderPane flowPane = ((BorderPane) rootPane.lookup("#inputFlowPane"));
        inputVBox = ((VBox) flowPane.getChildren().get(0));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        try {
            //Load the main.fxml file in the resources folder.
            rootPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void dependenciesPopupConstructorTest01() {
        DependenciesPopup test = new DependenciesPopup(rootPane, ((ListView<Task>) inputVBox.getChildren().get(11)));

        assertEquals(test.getTaskDependencies(), new ArrayList<>());
    }

    @Test
    void openDependenciesPopupTest01() {
        init();
        DependenciesPopup test = new DependenciesPopup(rootPane, ((ListView<Task>) inputVBox.getChildren().get(11)));

        test.openDependenciesPopup();

        assertTrue(rootPane.getChildrenUnmodifiable().get(3).isVisible());
    }

}
