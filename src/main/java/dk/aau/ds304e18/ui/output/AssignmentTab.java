package dk.aau.ds304e18.ui.output;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
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

public class AssignmentTab {

    private TableView<Employee> employeeTableView;
    private TableView<Task> taskTableView;

    public AssignmentTab(BorderPane borderPane) {
        taskTableView = (TableView<Task>) ((VBox) borderPane.getLeft()).getChildren().get(1);
        employeeTableView = (TableView<Employee>) ((VBox) borderPane.getCenter()).getChildren().get(1);
        taskTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        taskTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("employees"));

        taskTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));


        employeeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        employeeTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));


        TableColumn<Employee, String> column = (TableColumn<Employee, String>) employeeTableView.getColumns().get(1);
        column.setCellValueFactory(cellData ->
        {
            if (cellData.getValue().getCurrentTask() != null && cellData.getValue().getCurrentTask().size() != 0)
                return new SimpleStringProperty("Assigned");
            return new SimpleStringProperty("Not assigned");
        });


        VBox buttons = ((VBox) borderPane.getRight());

        ((Button) buttons.getChildren().get(0)).setTooltip(new Tooltip("Assigns an selected employee to the selected task"));
        buttons.getChildren().get(0).setOnMouseClicked(event -> assignEmployee());
        ((Button) buttons.getChildren().get(1)).setTooltip(new Tooltip("Removes an selected employee for their task"));
        buttons.getChildren().get(1).setOnMouseClicked(event -> unassignEmployee());

    }

    public void drawEmployees() {
        taskTableView.getItems().clear();
        employeeTableView.getItems().clear();
        taskTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
        employeeTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getEmployeeList().stream().
                filter(employee -> {
                    if (employee.getProject() == null) return false;
                    return employee.getProject().getId()
                            == JavaFXMain.selectedProjectId;
                }).collect(Collectors.toList())));

    }

    private void assignEmployee() {
        if (taskTableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        if (employeeTableView.getSelectionModel().getSelectedItems() == null) {
            return;
        }

        for (Employee employee : employeeTableView.getSelectionModel().getSelectedItems()) {
            taskTableView.getSelectionModel().getSelectedItem().addEmployee(employee);
        }


        drawEmployees();
    }

    private void unassignEmployee() {
        if (taskTableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        if (employeeTableView.getSelectionModel().getSelectedItems() == null) {
            return;
        }

        for (Employee employee : employeeTableView.getSelectionModel().getSelectedItems()) {
            taskTableView.getSelectionModel().getSelectedItem().getEmployees().remove(employee);
            employee.getCurrentTask().remove(taskTableView.getSelectionModel().getSelectedItem());
            DatabaseManager.updateEmployee(employee);
        }

        DatabaseManager.updateTask(taskTableView.getSelectionModel().getSelectedItem());

        drawEmployees();
    }
}
