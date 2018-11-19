package dk.aau.ds304e18.ui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.ui.output.OutputTab;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

public class EmployeeTab {

    private OutputTab outputTab;
    private TableView<Employee> employeeTableView;
    private TableView<Employee> employeeProjectTableView;

    public EmployeeTab(Parent rootPane, OutputTab outputTab) {
        this.outputTab = outputTab;
        BorderPane borderPane = (BorderPane) rootPane.lookup("#employeesBorderpane");
        VBox buttonPane = ((VBox) ((Pane) borderPane.getRight()).getChildren().get(0));
        //Assign
        ((Button) buttonPane.getChildren().get(0)).setTooltip(new Tooltip("Assigns an selected employee to the project"));
        buttonPane.getChildren().get(0).setOnMouseClicked(event -> assignEmployee());
        buttonPane.getChildren().get(0).setVisible(false);
        //Unassign
        ((Button) buttonPane.getChildren().get(1)).setTooltip(new Tooltip("Removes an selected employee from the project"));
        buttonPane.getChildren().get(1).setOnMouseClicked(event -> unassignEmployee());


        VBox inputVbox = (VBox) borderPane.getLeft();
        TextField nameTextField = (TextField) inputVbox.getChildren().get(1);
        //Add emp
        ((Button) inputVbox.getChildren().get(2)).setTooltip(new Tooltip("Adds an employee to the system"));
        inputVbox.getChildren().get(2).setOnMouseClicked(event -> addEmployee(nameTextField));


        TabPane tabPane = (TabPane) borderPane.getCenter();
        employeeTableView = (TableView<Employee>) ((AnchorPane) tabPane.getTabs().get(0).getContent()).getChildren().get(0);
        employeeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        employeeTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("currentTask"));
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                buttonPane.getChildren().get(0).setVisible(false);
                buttonPane.getChildren().get(1).setVisible(true);
            } else if (newValue.intValue() == 1) {
                buttonPane.getChildren().get(0).setVisible(true);
                buttonPane.getChildren().get(1).setVisible(false);
            }
        });
        employeeProjectTableView = (TableView<Employee>) ((AnchorPane) tabPane.getTabs().get(1).getContent()).getChildren().get(0);
        employeeProjectTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeProjectTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeProjectTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("currentTask"));
        employeeProjectTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }

    private void assignEmployee() {
        if (employeeProjectTableView.getSelectionModel().getSelectedItems() == null
                && employeeProjectTableView.getSelectionModel().getSelectedItems().size() == 0) {
            return;
        }
        LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId).addNewEmployee(employeeProjectTableView.getSelectionModel().getSelectedItems());
        drawEmployees();
        outputTab.drawOutputTab(false);
    }

    private void unassignEmployee() {
        if (employeeTableView.getSelectionModel().getSelectedItems() == null
                && employeeTableView.getSelectionModel().getSelectedItems().size() == 0) {
            return;
        }
        for (Employee employee : employeeTableView.getSelectionModel().getSelectedItems()) {
            for (Task task : employee.getCurrentTask()) {
                task.getEmployees().remove(employee);
                DatabaseManager.updateTask(task);
            }
            LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId).removeEmployee(employee);

        }

        drawEmployees();
        outputTab.drawOutputTab(false);
    }

    public void drawEmployees() {
        employeeTableView.getItems().clear();
        employeeProjectTableView.getItems().clear();
        employeeTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getEmployeeList().stream().
                filter(employee -> {
                    if (employee.getProject() == null) return false;
                    return employee.getProject().getId()
                            == JavaFXMain.selectedProjectId;
                }).collect(Collectors.toList())));
        employeeProjectTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getEmployeeList().
                stream().filter(emp -> emp.getProject() == null || emp.getProject().getId()
                != JavaFXMain.selectedProjectId).collect(Collectors.toList())));
    }

    private void addEmployee(TextField name) {
        if (!name.getText().equals("")) {
            new Employee(name.getText(), LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId));
            drawEmployees();
            name.clear();
        }
    }
}
