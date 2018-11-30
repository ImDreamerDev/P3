package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.gui.output.OutputTab;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTabTest extends ApplicationTest {
    private Parent rootPane;

    private BorderPane borderPane;
    private TabPane tabPane;
    private TableView<Employee> projectEmployeeTableView;

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
        borderPane = (BorderPane) rootPane.lookup("#employeesBorderpane");
        tabPane = (TabPane) borderPane.getCenter();
        projectEmployeeTableView = (TableView<Employee>) ((AnchorPane) tabPane.getTabs().get(0).getContent()).getChildren().get(0);

    }

    @Test
    void employeeTabConstructorTest01(){
        EmployeeTab test = new EmployeeTab(rootPane);

        assertNotNull(test);
    }

    @Test
    void employeeTabDrawEmployeeTest01() {
        EmployeeTab test = new EmployeeTab(rootPane);

        projectEmployeeTableView.getItems().add(new Employee("Hans", null));

        test.drawEmployees();

        assertEquals(projectEmployeeTableView.getItems(), new ArrayList<>());
    }

    @Test
    void addEmployeeTest01() {
        EmployeeTab test = new EmployeeTab(rootPane);

        JavaFXMain.selectedProjectId = 48;
        JavaFXMain.outputTab = new OutputTab(rootPane);

        test.addEmployee(new TextField("Test"));
        test.drawEmployees();

        assertEquals(projectEmployeeTableView.getItems(), new ArrayList<>());
    }
}
