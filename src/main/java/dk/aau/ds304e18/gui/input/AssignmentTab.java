package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

/**
 * The assignment tab of the GUI.
 */
class AssignmentTab {

    /**
     * The table view of the employee(s) on the project.
     */
    private final TableView<Employee> employeeTableView;

    /**
     * The table view of the task(s) on the project.
     */
    private final TableView<Task> taskTableView;


    /**
     * Creates and sets up the assignment tab.
     *
     * @param borderPane The border pane where all of this is contained.
     */
    public AssignmentTab(BorderPane borderPane) {
        //Gets the task table view from the GUI.
        taskTableView = (TableView<Task>) ((VBox) borderPane.getLeft()).getChildren().get(1);
        //Gets the employee table view from the GUI.
        employeeTableView = (TableView<Employee>) ((VBox) borderPane.getCenter()).getChildren().get(1);

        //Sets up the task table view's columns to show the correct data.
        taskTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        taskTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("employees"));

        //Sets the items of the task table view to all the task(s) on the project.
        taskTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));

        //Sets the selection model to multiple
        employeeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Sets up the employee table view's columns to show the correct data.
        employeeTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));

        //Get the second column of the employee table view
        TableColumn<Employee, String> column = (TableColumn<Employee, String>) employeeTableView.getColumns().get(1);

        //Set the column data to show how many tasks the employees is assigned to.
        column.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCurrentTask() != null && cellData.getValue().getCurrentTask().size() != 0)
                return new SimpleStringProperty("Assigned to: " + cellData.getValue().getCurrentTask().size()+" tasks");
            return new SimpleStringProperty("Not assigned");
        });
        //Get the buttons box to the right.
        VBox buttons = ((VBox) borderPane.getRight());

        //The tool tip of the assign employee button.
        Tooltip assignEmpToTaskTooltip = new Tooltip("Assigns an selected employee to the project");
        assignEmpToTaskTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((Button) buttons.getChildren().get(0)).setTooltip(assignEmpToTaskTooltip);
        //When the assign button is click assign the selected employee.
        buttons.getChildren().get(0).setOnMouseClicked(event -> assignEmployee());

        //Sets the tool tip of the unassign employee button.
        Tooltip removeEmpToTask = new Tooltip("Removes an selected employee for their task");
        removeEmpToTask.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((Button) buttons.getChildren().get(1)).setTooltip(removeEmpToTask);
        //When the unassign button is click unassign the selected employee.
        buttons.getChildren().get(1).setOnMouseClicked(event -> unassignEmployee());
        //Updates the input up to reflect the changes.
        if (JavaFXMain.inputTab != null)
            JavaFXMain.inputTab.employeeTab.drawEmployees();
    }

    
    
    /**
     * Updates/draws the employees in the assignment tab.
     */
    public void drawEmployees() {
        //Clears the table views to make sure the data is up to date.
        taskTableView.getItems().clear();
        employeeTableView.getItems().clear();

        //Sets the task table view to all the the task on the project.
        taskTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));

        //Sets the employee table view to all the employees on the project.
        employeeTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getEmployeeList().stream().
                filter(employee -> {
                    if (employee.getProject() == null) return false;
                    return employee.getProject().getId()
                            == JavaFXMain.selectedProjectId;
                }).collect(Collectors.toList())));


    }

    /**
     * Assign the selected employee(s) to the selected task.
     */
    private void assignEmployee() {
        if (taskTableView.getSelectionModel().getSelectedItem() == null || employeeTableView.getSelectionModel().getSelectedItems() == null) {
            return;
        }
        //Assign the employee(s) to the task.
        for (Employee employee : employeeTableView.getSelectionModel().getSelectedItems()) {
            taskTableView.getSelectionModel().getSelectedItem().addEmployee(employee);
        }

        //Update the GUI to reflect this change.
        drawEmployees();
        //Updates the input up to reflect the changes.
        if (JavaFXMain.inputTab != null)
            JavaFXMain.inputTab.employeeTab.drawEmployees();
    }

    /**
     * Unassign the selected employee(s) from the selected task.
     */
    private void unassignEmployee() {
        //If nothing is selected return.
        if (taskTableView.getSelectionModel().getSelectedItem() == null ||
                employeeTableView.getSelectionModel().getSelectedItems() == null) {
            return;
        }

        // Unassign the employee(s) from the task.
        for (Employee employee : employeeTableView.getSelectionModel().getSelectedItems()) {
            taskTableView.getSelectionModel().getSelectedItem().getEmployees().remove(employee);
            employee.getCurrentTask().remove(taskTableView.getSelectionModel().getSelectedItem());
            DatabaseManager.updateEmployee(employee);
        }
        //Update the task in the database.
        DatabaseManager.updateTask(taskTableView.getSelectionModel().getSelectedItem());

        //Update the GUI to reflect this change.
        drawEmployees();
        //Updates the input up to reflect the changes.
        if (JavaFXMain.inputTab != null)
            JavaFXMain.inputTab.employeeTab.drawEmployees();
    }
}
