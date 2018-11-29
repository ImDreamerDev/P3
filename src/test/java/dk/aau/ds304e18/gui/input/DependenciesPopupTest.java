package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Task;
import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DependenciesPopupTest {

    @BeforeAll
    static void realInit() {
        DatabaseManager.isTests = true;
        Application.launch(StartUpTest.class);
    }

    @Test
    void openDependenciesPopupTest01() {
        BorderPane flowPane = ((BorderPane) StartUpTest.getRootPane().lookup("#inputFlowPane"));
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        DependenciesPopup test = new DependenciesPopup(StartUpTest.getRootPane(), ((ListView<Task>) inputVBox.getChildren().get(11)));
        test.openDependenciesPopup();
        assertTrue(StartUpTest.getRootPane().getChildrenUnmodifiable().get(3).isVisible());
    }
}

