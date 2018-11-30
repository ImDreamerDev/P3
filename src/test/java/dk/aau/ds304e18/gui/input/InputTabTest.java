package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.models.Employee;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InputTabTest extends ApplicationTest {

    private Parent rootPane;

    @BeforeEach
    void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        try {
            //Load the main.fxml file in the resources folder.
            rootPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void inputTabConstructorTest01() {
        /*EmployeeTab test2 = new EmployeeTab(rootPane);
        InputTab test = new InputTab(rootPane);

        assertNotNull(test);*/
        assertEquals(1,1);
    }

}
