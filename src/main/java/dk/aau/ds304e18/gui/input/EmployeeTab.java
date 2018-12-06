package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Task;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The tab where employee(s) can be created or assigned/unassigned.
 */
public class EmployeeTab {

    /**
     * The table view of all the employees assigned to the project.
     */
    private final TableView<Employee> projectEmployeeTableView;

    /**
     * The table view of all the free employees.
     */
    private final TableView<Employee> freeEmployeeTableView;

    /**
     * The border pane containing the employee tab.
     */
    private BorderPane borderPane;

    /**
     * Create and sets up the Employee tab.
     *
     * @param rootPane The root pane of the GUI.
     */
    EmployeeTab(Parent rootPane) {
        //Get the borderpane where all of this is contained.

        borderPane = (BorderPane) rootPane.lookup("#employeesBorderpane");
        //Get the center tab pane, where the two tabs are.
        TabPane tabPane = (TabPane) borderPane.getCenter();
        //Get the project employee table view from the GUI.
        projectEmployeeTableView = (TableView<Employee>) ((AnchorPane) tabPane.getTabs().get(0)
                .getContent()).getChildren().get(0);

        // Get the free employee table view from the GUI.
        freeEmployeeTableView = (TableView<Employee>) ((AnchorPane) tabPane.getTabs()
                .get(1).getContent()).getChildren().get(0);
        setupEmployeeTab();
    }


    private void setupEmployeeTab() {
        //Get the buttons in the right side.
        VBox buttonPane = ((VBox) ((Pane) borderPane.getRight()).getChildren().get(0));

        //Sets the tool tip for the assign employee(s) button.
        Tooltip assignEmpToProject = new Tooltip("Assigns an selected employee to the project");
        assignEmpToProject.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((Button) buttonPane.getChildren().get(0)).setTooltip(assignEmpToProject);

        //Make the assign button actually assign employee(s) to the project.
        buttonPane.getChildren().get(0).setOnMouseClicked(event -> assignEmployee());
        // Hide the button, because the GUI starts on the current project employee(s) tab.
        buttonPane.getChildren().get(0).setVisible(false);

        //Sets the tool tip for the unassign employee(s) button.
        Tooltip removeEmpFromProject = new Tooltip("Removes an selected employee from the project");
        removeEmpFromProject.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((Button) buttonPane.getChildren().get(1)).setTooltip(removeEmpFromProject);

        //Make the unassign button actually unassign employee(s) from the project.
        buttonPane.getChildren().get(1).setOnMouseClicked(event -> unassignEmployee());

        //Get the left hand of the GUI, where the add employee field is.
        VBox inputVBox = (VBox) borderPane.getLeft();

        //Get the name field from the inputVBox.
        TextField nameTextField = (TextField) inputVBox.getChildren().get(1);
        nameTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.isBlank()) {
                nameTextField.setStyle("-fx-border-color: #ff0000");
            } else {
                nameTextField.setStyle("");
            }
        });

        //Set the tooltip for the Add employee button.
        Tooltip addEmployeeSystem = new Tooltip("Adds an employee to the system");
        addEmployeeSystem.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((Button) inputVBox.getChildren().get(2)).setTooltip(addEmployeeSystem);

        //Makes the add employee actually add the employees to the project.
        inputVBox.getChildren().get(2).setOnMouseClicked(event -> addEmployee(nameTextField));


        //Sets the tool tips for these tabs.
        Tooltip empPageAssToProject = new Tooltip("Page with all employees assigned to the selected project");
        empPageAssToProject.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((TabPane) borderPane.getCenter()).getTabs().get(0).setTooltip(empPageAssToProject);

        Tooltip availableEmp = new Tooltip("Page with all the available employees");
        availableEmp.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((TabPane) borderPane.getCenter()).getTabs().get(1).setTooltip(availableEmp);


        //Set the selection mode to multiple.
        projectEmployeeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Sets the columns of the project employee table view to the fields of the employee.
        setupEmployeeTableView(projectEmployeeTableView);

        //Get the center tab pane, where the two tabs are.
        TabPane tabPane = (TabPane) borderPane.getCenter();

        swapButtons(buttonPane);
        //When tabs are "tabbed" between, toggle the right buttons on the right side.
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                inputVBox.setVisible(true);
            } else if (newValue.intValue() == 1) {
                inputVBox.setVisible(false);
            }


            swapButtons(buttonPane);
        });


        // Sets the columns of the free employee table view to the fields of the employee.
        setupEmployeeTableView(freeEmployeeTableView);

        //Sets the selection mode to multiple.
        freeEmployeeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (JavaFXMain.inputTab != null)
            JavaFXMain.inputTab.assignmentTab.drawEmployees();
    }

    private void swapButtons(VBox buttonPane) {
        List<Node> nodes = new ArrayList<>(buttonPane.getChildren());
        Collections.swap(nodes, 0, 1);
        buttonPane.getChildren().clear();
        buttonPane.getChildren().addAll(nodes);

        buttonPane.getChildren().get(0).setVisible(true);
        buttonPane.getChildren().get(1).setVisible(false);
    }

    private void setupEmployeeTableView(TableView<Employee> projectEmployeeTableView) {
        projectEmployeeTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        projectEmployeeTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        projectEmployeeTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("currentTask"));
    }


    /**
     * Assigns the selected employee(s) to the current project.
     */
    void assignEmployee() {
        //If nothing is selected return.
        if (freeEmployeeTableView.getSelectionModel().getSelectedItems() == null
                && freeEmployeeTableView.getSelectionModel().getSelectedItems().size() == 0) {
            return;
        }
        //Add the employee(s) to the project.
        LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId)
                .addNewEmployee(freeEmployeeTableView.getSelectionModel().getSelectedItems());
        //Update the GUI to reflect the changes.
        drawEmployees();
        if (JavaFXMain.inputTab != null)
            JavaFXMain.inputTab.assignmentTab.drawEmployees();
    }

    /**
     * Removes the selected employee(s) from the current project.
     */
    void unassignEmployee() {
        //If nothing is selected return.
        if (projectEmployeeTableView.getSelectionModel().getSelectedItems() == null
                && projectEmployeeTableView.getSelectionModel().getSelectedItems().size() == 0) {
            return;
        }

        //Foreach of the selected employees remove the tasks associated with this project.
        for (Employee employee : projectEmployeeTableView.getSelectionModel().getSelectedItems()) {
            for (Task task : employee.getCurrentTask()) {
                task.getEmployees().remove(employee);
                DatabaseManager.updateTask(task);
            }
            //Removes the tasks for the employees.
            employee.getCurrentTask().removeAll(employee.getCurrentTask());
            LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId).removeEmployee(employee);
            employee.setProjectId(0);
            DatabaseManager.updateEmployee(employee);
        }
        //Draw the new result.
        drawEmployees();
        if (JavaFXMain.inputTab != null)
            JavaFXMain.inputTab.assignmentTab.drawEmployees();
    }

    /**
     * Draws/updates the table views.
     */
    public void drawEmployees() {
        //Clears the table views to make sure the new data is updated.
        projectEmployeeTableView.getItems().clear();
        freeEmployeeTableView.getItems().clear();

        //Sets the project employee table view to all the employees assigned to this project.
        projectEmployeeTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getEmployeeList().stream().
                filter(employee -> {
                    if (employee.getProject() == null) return false;
                    return employee.getProject().getId()
                            == JavaFXMain.selectedProjectId;
                }).collect(Collectors.toList())));

        //Sets the free employee table view to all the free employees in the database.
        freeEmployeeTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getEmployeeList().
                stream().filter(emp -> emp.getProject() == null || emp.getProject().getId()
                != JavaFXMain.selectedProjectId).collect(Collectors.toList())));

        //Set them to sort after id, because id is at the 0'th column.
        freeEmployeeTableView.getSortOrder().add(freeEmployeeTableView.getColumns().get(0));
        projectEmployeeTableView.getSortOrder().add(projectEmployeeTableView.getColumns().get(0));

    }

    /**
     * Adds a new employee to the system.
     *
     * @param name The name of the employee.
     */
    void addEmployee(TextField name) {
        if (!name.getText().isBlank()) {
            new Employee(name.getText(), LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId));
            drawEmployees();
            name.clear();
            if (JavaFXMain.inputTab != null)
                JavaFXMain.inputTab.assignmentTab.drawEmployees();
        }
    }
}
