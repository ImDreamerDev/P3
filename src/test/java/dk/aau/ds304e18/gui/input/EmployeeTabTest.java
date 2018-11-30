package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.models.Employee;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeTabTest extends ApplicationTest {
    private Parent rootPane;

    private BorderPane borderPane;
    private TabPane tabPane;
    private TableView<Employee> projectEmployeeTableView;
    private TableView<Employee> freeEmployeeTableView;

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
        freeEmployeeTableView = (TableView<Employee>) ((AnchorPane) tabPane.getTabs().get(1).getContent()).getChildren().get(0);
    }

    @Test
    void employeeTabConstructorTest01() {
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
        LocalObjStorage.getProjectList().addAll(DatabaseManager.getPMProjects(DatabaseManager.logIn("Project Manager111", "Password")));
        JavaFXMain.selectedProjectId = 13075;
        VBox inputVBox = (VBox) borderPane.getLeft();
        TextField nameTextField = (TextField) inputVBox.getChildren().get(1);
        nameTextField.setText("Tom");
        try {
            test.addEmployee(nameTextField);
        } catch (NullPointerException ignored) {
        }

        assertEquals(projectEmployeeTableView.getItems().size(), 1);
    }

    @Test
    void addEmployeeTest02() {
        EmployeeTab test = new EmployeeTab(rootPane);
        LocalObjStorage.getProjectList().addAll(DatabaseManager.getPMProjects(DatabaseManager.logIn("Project Manager111", "Password")));
        JavaFXMain.selectedProjectId = 13075;
        VBox inputVBox = (VBox) borderPane.getLeft();
        TextField nameTextField = (TextField) inputVBox.getChildren().get(1);
        nameTextField.setText("");
        try {
            test.addEmployee(nameTextField);
        } catch (NullPointerException ignored) {
        }

        assertEquals(projectEmployeeTableView.getItems().size(), 0);
    }

    @Test
    void assignEmployeeTest01() {
        EmployeeTab test = new EmployeeTab(rootPane);

        LocalObjStorage.getProjectList().addAll(DatabaseManager.getPMProjects(DatabaseManager.logIn("Project Manager111", "Password")));
        JavaFXMain.selectedProjectId = 13075;
        VBox inputVBox = (VBox) borderPane.getLeft();
        TextField nameTextField = (TextField) inputVBox.getChildren().get(1);
        nameTextField.setText("Hans");
        try {
            test.addEmployee(nameTextField);
        } catch (NullPointerException ignored) {
        }

        projectEmployeeTableView.getSelectionModel().select(0);
        try {
            test.unassignEmployee();
        } catch (Exception weDontCareAboutExceptionsLol) {
        }

        freeEmployeeTableView.getSelectionModel().select(0);
        try {
            test.assignEmployee();
        } catch (Exception weDontCareAboutExceptionsLol) {
        }

        assertEquals(3, projectEmployeeTableView.getItems().size());
    }

    @Test
    void unassignAssignEmployeeTest01() {
        EmployeeTab test = new EmployeeTab(rootPane);

        LocalObjStorage.getProjectList().addAll(DatabaseManager.getPMProjects(DatabaseManager.logIn("Project Manager111", "Password")));
        JavaFXMain.selectedProjectId = 13075;
        VBox inputVBox = (VBox) borderPane.getLeft();
        TextField nameTextField = (TextField) inputVBox.getChildren().get(1);
        nameTextField.setText("Hans");
        try {
            test.addEmployee(nameTextField);
        } catch (NullPointerException ignored) {
        }

        try {
            test.unassignEmployee();
        } catch (Exception weDontCareAboutExceptionsLol) {
        }

        try {
            test.assignEmployee();
        } catch (Exception weDontCareAboutExceptionsLol) {
        }

        assertEquals(2, projectEmployeeTableView.getItems().size());
    }

    @Test
    void unassignEmployeeTest01() {
        EmployeeTab test = new EmployeeTab(rootPane);

        LocalObjStorage.getProjectList().addAll(DatabaseManager.getPMProjects(DatabaseManager.logIn("Project Manager111", "Password")));
        JavaFXMain.selectedProjectId = 13075;
        VBox inputVBox = (VBox) borderPane.getLeft();
        TextField nameTextField = (TextField) inputVBox.getChildren().get(1);
        nameTextField.setText("Hans");
        try {
            test.addEmployee(nameTextField);
        } catch (NullPointerException ignored) {
        }

        projectEmployeeTableView.getSelectionModel().select(0);
        try {
            test.unassignEmployee();
        } catch (Exception weDontCareAboutExceptionsLol) {
        }

        assertEquals(1, projectEmployeeTableView.getItems().size());
    }
}
