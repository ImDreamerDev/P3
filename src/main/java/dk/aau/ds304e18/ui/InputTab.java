package dk.aau.ds304e18.ui;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Project;
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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InputTab {

    private List<Task> taskDependencies = new ArrayList<>();
    private Parent rootPane;
    private OutputTab outputTab;
    private TableView<Task> tableView;

    public InputTab(Parent rootPane, OutputTab outputTab) {
        this.rootPane = rootPane;
        this.outputTab = outputTab;
        setupInputTab();
    }

    void drawInputTab() {
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
    }

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
        HBox probsHBox = ((HBox) inputVBox.getChildren().get(7));
        HBox probsHBox2 = ((HBox) inputVBox.getChildren().get(8));
        HBox probsHBox3 = ((HBox) inputVBox.getChildren().get(9));
        ListView<Task> listViewDependency = ((ListView<Task>) inputVBox.getChildren().get(11));
        HBox buttonsForDependencies = (HBox) inputVBox.getChildren().get(12);
        HBox bottomButtonsHBox = (HBox) inputVBox.getChildren().get(13);

        priority.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(priority, newValue));
        estimatedTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(estimatedTimeTextField, newValue));

        TextField probs1 = ((TextField) probsHBox.getChildren().get(0));
        probs1.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs1, newValue));
        TextField probs2 = ((TextField) probsHBox.getChildren().get(1));
        probs2.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs2, newValue));

        TextField probs3 = ((TextField) probsHBox2.getChildren().get(0));
        probs3.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs3, newValue));
        TextField probs4 = ((TextField) probsHBox2.getChildren().get(1));
        probs4.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs4, newValue));

        TextField probs5 = ((TextField) probsHBox3.getChildren().get(0));
        probs5.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs5, newValue));
        TextField probs6 = ((TextField) probsHBox3.getChildren().get(1));
        probs6.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs6, newValue));

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
                    Integer.parseInt(priority.getText()), probabilities, tableView);
        });
        bottomButtonsHBox.getChildren().get(1).setOnMouseClicked(event -> clearInputFields(listViewDependency, probs1, probs2, probs3,
                probs4, probs5, probs6, nameTextField, estimatedTimeTextField, priority));


        //Middle column
        VBox vBoxSplitter = ((VBox) ((Pane) flowPane.getChildren().get(1)).getChildren().get(0));
        vBoxSplitter.getChildren().get(1).setOnMouseClicked(event -> calculate(LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId), ((CheckBox) vBoxSplitter.getChildren().get(2)).isSelected()));
        vBoxSplitter.getChildren().get(0).setOnMouseClicked(event -> removeTask());
        drawInputTab();
    }

    private void calculate(Project pro, boolean useMonty) {
        Instant start = java.time.Instant.now();

        Sequence.sequenceTasks(pro, useMonty);
        outputTab.drawOutputTab(useMonty);
        drawInputTab();

        Instant end = java.time.Instant.now();
        Duration between = java.time.Duration.between(start, end);
        System.out.format((char) 27 + "[31mNote: total in that unit!\n" + (char) 27 + "[39mHours: %02d Minutes: %02d Seconds: %02d Milliseconds: %04d \n",
                between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis()); // 0D, 00:00:01.1001
    }

    private void validateNumericInput(TextField textField, String newValue) {
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[\\D]", ""));
        }
    }

    private void clearInputFields(ListView<Task> listViewDependency, TextField... textFields) {
        for (TextField textField : textFields)
            textField.clear();
        taskDependencies.clear();
        listViewDependency.setItems(FXCollections.observableArrayList(taskDependencies));
    }

    private void addTask(String name, double estimatedTime, int priority, List<Probabilities> probabilities, TableView<Task> tableView) {
        Task ttt = new Task(name, estimatedTime, priority,
                LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId));
        for (Probabilities pro : probabilities)
            ttt.getProbabilities().add(pro);
        ttt.addDependency(taskDependencies);
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
    }

    /**
     * Method for revoming a dependency from the new task. This happens by selecting the task and pressing the remove button.
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
