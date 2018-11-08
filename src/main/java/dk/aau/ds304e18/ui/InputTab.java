package dk.aau.ds304e18.ui;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.Sequence;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InputTab {

    private List<Task> taskDependencies = new ArrayList<>();
    private Parent rootPane;
    private OutputTab outputTab;
    private TableView<Task> tableView;
    public static HBox progressBarContainer;

    public InputTab(Parent rootPane, OutputTab outputTab) {
        this.rootPane = rootPane;
        this.outputTab = outputTab;
        setupInputTab();
    }

    void drawInputTab() {
        if (JavaFXMain.selectedProjectId != 0 && LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId).getState() == ProjectState.ARCHIVED)
            disableInput();
        else
            enableInput();
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
    }

    void disableInput() {
        var flowPane = ((FlowPane) rootPane.lookup("#inputFlowPane"));
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        VBox vBoxSplitter = ((VBox) ((Pane) flowPane.getChildren().get(1)).getChildren().get(0));
        vBoxSplitter.getChildren().get(0).setDisable(true);
        inputVBox.setDisable(true);
    }

    void enableInput() {
        var flowPane = ((FlowPane) rootPane.lookup("#inputFlowPane"));
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        VBox vBoxSplitter = ((VBox) ((Pane) flowPane.getChildren().get(1)).getChildren().get(0));
        vBoxSplitter.getChildren().get(0).setDisable(false);
        inputVBox.setDisable(false);
    }

    /**
     * The method that sets up the contents of the whole input tab.
     */
    private void setupInputTab() {
        var flowPane = ((FlowPane) rootPane.lookup("#inputFlowPane"));

        //Table view
        tableView = ((TableView<Task>) flowPane.getChildren().get(2));
        setUpTaskTable();

        //Input view
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        TextField nameTextField = ((TextField) inputVBox.getChildren().get(1));
        TextField priority = ((TextField) inputVBox.getChildren().get(3));
        TextField estimatedTimeTextField = ((TextField) inputVBox.getChildren().get(5));
        TextField numOfEmployees = ((TextField) inputVBox.getChildren().get(7));
        HBox probsHBox = ((HBox) inputVBox.getChildren().get(9));
        HBox probsHBox2 = ((HBox) inputVBox.getChildren().get(10));
        HBox probsHBox3 = ((HBox) inputVBox.getChildren().get(11));
        ListView<Task> listViewDependency = ((ListView<Task>) inputVBox.getChildren().get(13));
        HBox buttonsForDependencies = (HBox) inputVBox.getChildren().get(14);
        HBox bottomButtonsHBox = (HBox) inputVBox.getChildren().get(15);
        progressBarContainer = ((HBox) flowPane.getChildren().get(3));

        priority.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(priority, newValue, true));
        estimatedTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(estimatedTimeTextField, newValue, false));

        numOfEmployees.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(numOfEmployees, newValue, true));

        TextField probs1 = ((TextField) probsHBox.getChildren().get(0));
        probs1.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs1, newValue, false));
        TextField probs2 = ((TextField) probsHBox.getChildren().get(1));
        probs2.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs2, newValue, false));

        TextField probs3 = ((TextField) probsHBox2.getChildren().get(0));
        probs3.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs3, newValue, false));
        TextField probs4 = ((TextField) probsHBox2.getChildren().get(1));
        probs4.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs4, newValue, false));

        TextField probs5 = ((TextField) probsHBox3.getChildren().get(0));
        probs5.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs5, newValue, false));
        TextField probs6 = ((TextField) probsHBox3.getChildren().get(1));
        probs6.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs6, newValue, false));

        buttonsForDependencies.getChildren().get(0).setOnMouseClicked(event -> addDependency(listViewDependency));
        buttonsForDependencies.getChildren().get(1).setOnMouseClicked(event -> removeDependency(listViewDependency));

        bottomButtonsHBox.getChildren().get(0).setOnMouseClicked(event -> {
            List<Probabilities> probabilities = new ArrayList<>();
            if (!probs1.getText().equals(""))
                probabilities.add(new Probabilities(Double.parseDouble(probs1.getText()), Double.parseDouble(probs2.getText())));
            if (!probs3.getText().equals(""))
                probabilities.add(new Probabilities(Double.parseDouble(probs3.getText()), Double.parseDouble(probs4.getText())));
            if (!probs5.getText().equals(""))
                probabilities.add(new Probabilities(Double.parseDouble(probs5.getText()), Double.parseDouble(probs6.getText())));
            addTask(nameTextField.getText(), Double.parseDouble(estimatedTimeTextField.getText()),
                    Integer.parseInt(priority.getText()), probabilities, tableView, Double.parseDouble(numOfEmployees.getText()));
            clearInputFields(listViewDependency, probs1, probs2, probs3,
                    probs4, probs5, probs6, nameTextField, estimatedTimeTextField, priority);
        });
        bottomButtonsHBox.getChildren().get(1).setOnMouseClicked(event -> clearInputFields(listViewDependency, probs1, probs2, probs3,
                probs4, probs5, probs6, nameTextField, estimatedTimeTextField, priority));


        //Middle column
        VBox vBoxSplitter = ((VBox) ((Pane) flowPane.getChildren().get(1)).getChildren().get(0));
        vBoxSplitter.getChildren().get(1).setOnMouseClicked(event -> calculate(LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId), ((CheckBox) vBoxSplitter.getChildren().get(2)).isSelected()));
        vBoxSplitter.getChildren().get(0).setOnMouseClicked(event -> removeTask());
        drawInputTab();
    }

    /**
     * This method calculates and produces the output.
     *
     * @param pro
     * @param useMonty - the monte carlo method is used.
     */
    private void calculate(Project pro, boolean useMonty) {
        Sequence.sequenceTasks(pro, useMonty);
        outputTab.drawOutputTab(useMonty);
        drawInputTab();
    }

    /**
     * This method is used to make sure that the estimated time and all the textboxes that only are supposed to take numeric values,
     * actually contain numeric values.
     *
     * @param textField - the textbox
     * @param newValue  - the contents of the textbox
     */
    private void validateNumericInput(TextField textField, String newValue, boolean intField) {
        if (intField)
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[[\\D]]", ""));
            } else if (!newValue.matches("\\d*\\.")) {
                textField.setText(newValue.replaceAll("[[^\\d^\\.]]", ""));
            }
    }

    /**
     * This method clears the whole inputfield for creating a new task. This means all the textboxes and the dependencies table.
     *
     * @param listViewDependency - the list of dependencies
     * @param textFields         - the specific textbox
     */
    private void clearInputFields(ListView<Task> listViewDependency, TextField... textFields) {
        for (TextField textField : textFields)
            textField.clear();
        taskDependencies.clear();
        listViewDependency.setItems(FXCollections.observableArrayList(taskDependencies));
    }

    /**
     * The method for adding a task.
     *
     * @param name          - The name of the task - filled into the textbox.
     * @param estimatedTime - filled into the textbox.
     * @param priority      - filled into textbox.
     * @param probabilities - filled into textbox.
     * @param tableView     - the dependencies table - these are added through the addDependency method.
     */
    private void addTask(String name, double estimatedTime, int priority, List<Probabilities> probabilities, TableView<Task> tableView, double numberOfEmployees) {
        Task ttt = new Task(name, estimatedTime, priority,
                LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId), numberOfEmployees);
        for (Probabilities pro : probabilities)
            ttt.getProbabilities().add(pro);
        ttt.addDependency(taskDependencies);
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
    }

    /**
     * Method for revoming a dependency from the new task. This happens by selecting the task and pressing the remove button.
     *
     * @param listViewDependency
     */
    private void removeDependency(ListView<Task> listViewDependency) {
        Task task = listViewDependency.getSelectionModel().getSelectedItem();
        if (taskDependencies.contains(task)) {
            taskDependencies.remove(task);
            listViewDependency.setItems(FXCollections.observableArrayList(taskDependencies));
        }
    }

    /**
     * Method for adding a dependency to a new task.
     *
     * @param listViewDependency
     */
    private void addDependency(ListView<Task> listViewDependency) {
        int taskId = (int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();
        Task task = LocalObjStorage.getTaskById(taskId);
        if (!taskDependencies.contains(task)) {
            taskDependencies.add(LocalObjStorage.getTaskById(taskId));
            listViewDependency.setItems(FXCollections.observableArrayList(taskDependencies));
        }
    }

    /**
     * Method for removing a task. A task is selected in the table, and then if the button(cancel task) is pressed the task is removed.
     */
    private void removeTask() {
        int taskId = (int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();
        Project project = LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId);
        project.getTasks().remove(LocalObjStorage.getTaskById(taskId));
        project.setSequence("");
        DatabaseManager.removeTask(taskId);
        LocalObjStorage.getTaskList().remove(LocalObjStorage.getTaskById(taskId));
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
    }

    /**
     * The method that sets up the task table.
     */
    private void setUpTaskTable() {
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        tableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("priority"));
        tableView.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("probabilities"));
        tableView.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("dependencies"));
    }
}
