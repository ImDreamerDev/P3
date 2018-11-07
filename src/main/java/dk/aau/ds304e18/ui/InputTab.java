package dk.aau.ds304e18.ui;
/*
 * Author: Lasse Stig Emil Rasmussen
 * Email: lser17@student.aau.dk
 * Class: Software 2nd semester
 */

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

    public InputTab(Parent rootPane, OutputTab outputTab) {
        this.rootPane = rootPane;
        this.outputTab = outputTab;
        setupInputTab();
    }

    void drawInputTab() {
        var flowPane = ((FlowPane) rootPane.lookup("#inputFlowPane"));
        var tableView = ((TableView) flowPane.getChildren().get(2));
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
    }

    private void setupInputTab() {
        var flowPane = ((FlowPane) rootPane.lookup("#inputFlowPane"));
        var tableView = ((TableView) flowPane.getChildren().get(2));
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
        ((TableColumn) tableView.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<Task, String>("id"));
        ((TableColumn) tableView.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
        ((TableColumn) tableView.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<Task, String>("estimatedTime"));
        ((TableColumn) tableView.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<Task, String>("priority"));
        ((TableColumn) tableView.getColumns().get(4)).setCellValueFactory(new PropertyValueFactory<Task, String>("probabilities"));
        ((TableColumn) tableView.getColumns().get(5)).setCellValueFactory(new PropertyValueFactory<Task, String>("dependencies"));
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        TextField nameTextField = ((TextField) inputVBox.getChildren().get(1));
        TextField priority = ((TextField) inputVBox.getChildren().get(3));
        priority.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                priority.setText(newValue.replaceAll("[\\D]", ""));
            }
        });
        TextField estimatedTimeTextField = ((TextField) inputVBox.getChildren().get(5));
        estimatedTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                estimatedTimeTextField.setText(newValue.replaceAll("[\\D]", ""));
            }
        });
        ListView<Task> listViewDeps = ((ListView<Task>) inputVBox.getChildren().get(11));

        HBox prorbsHBox = ((HBox) inputVBox.getChildren().get(7));
        TextField probs1 = ((TextField) prorbsHBox.getChildren().get(0));
        probs1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                probs1.setText(newValue.replaceAll("[\\D]", ""));
            }
        });
        TextField probs2 = ((TextField) prorbsHBox.getChildren().get(1));
        probs2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                probs2.setText(newValue.replaceAll("[\\D]", ""));
            }
        });
        HBox prorbsHBox2 = ((HBox) inputVBox.getChildren().get(8));
        TextField probs3 = ((TextField) prorbsHBox2.getChildren().get(0));
        probs3.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                probs3.setText(newValue.replaceAll("[\\D]", ""));
            }
        });
        TextField probs4 = ((TextField) prorbsHBox2.getChildren().get(1));
        probs4.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                probs4.setText(newValue.replaceAll("[\\D]", ""));
            }
        });
        HBox prorbsHBox3 = ((HBox) inputVBox.getChildren().get(9));
        TextField probs5 = ((TextField) prorbsHBox3.getChildren().get(0));
        probs5.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                probs5.setText(newValue.replaceAll("[\\D]", ""));
            }
        });
        TextField probs6 = ((TextField) prorbsHBox3.getChildren().get(1));
        probs6.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                probs6.setText(newValue.replaceAll("[\\D]", ""));
            }
        });

        HBox buttonsDeps = (HBox) inputVBox.getChildren().get(12);
        buttonsDeps.getChildren().get(0).setOnMouseClicked(event -> {
            int taskId = (int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();
            Task task = LocalObjStorage.getTaskById(taskId);
            if (!taskDependencies.contains(task)) {

                taskDependencies.add(LocalObjStorage.getTaskById(taskId));
                listViewDeps.setItems(FXCollections.observableArrayList(taskDependencies));
            }
        });

        buttonsDeps.getChildren().get(1).setOnMouseClicked(event -> {
            Task task = listViewDeps.getSelectionModel().getSelectedItem();
            if (taskDependencies.contains(task)) {
                taskDependencies.remove(task);
                listViewDeps.setItems(FXCollections.observableArrayList(taskDependencies));
            }
        });


        HBox buttonHbox = (HBox) inputVBox.getChildren().get(13);
        buttonHbox.getChildren().get(0).setOnMouseClicked(event -> {
            Task ttt = new Task(nameTextField.getText(), Integer.parseInt(estimatedTimeTextField.getText()), Integer.parseInt(priority.getText()),
                    LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId));
            if (!probs1.getText().equals(""))
                ttt.getProbabilities().add(new Probabilities(Integer.parseInt(probs1.getText()), Integer.parseInt(probs2.getText())));
            if (!probs3.getText().equals(""))
                ttt.getProbabilities().add(new Probabilities(Integer.parseInt(probs3.getText()), Integer.parseInt(probs4.getText())));
            if (!probs5.getText().equals(""))
                ttt.getProbabilities().add(new Probabilities(Integer.parseInt(probs5.getText()), Integer.parseInt(probs6.getText())));
            ttt.addDependency(taskDependencies);
            tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                    stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
        });
        System.out.println(buttonHbox.getChildren().get(1).getClass().getSimpleName());
        buttonHbox.getChildren().get(1).setOnMouseClicked(event -> {
            probs1.clear();
            probs2.clear();
            probs3.clear();
            probs4.clear();
            probs5.clear();
            probs6.clear();
            priority.clear();
            nameTextField.clear();
            estimatedTimeTextField.clear();
            taskDependencies.clear();
            listViewDeps.setItems(FXCollections.observableArrayList(taskDependencies));
        });
        VBox vboxSplitter = ((VBox) ((Pane) flowPane.getChildren().get(1)).getChildren().get(0));
        vboxSplitter.getChildren().get(1).setOnMouseClicked(event -> calc(LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId), ((CheckBox) vboxSplitter.getChildren().get(2)).isSelected()));
        vboxSplitter.getChildren().get(0).setOnMouseClicked(event -> {
            int taskId = (int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();

            Project project = LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId);
            project.getTasks().remove(LocalObjStorage.getTaskById(taskId));
            project.setSequence("");
            DatabaseManager.removeTask(taskId);
            LocalObjStorage.getTaskList().remove(LocalObjStorage.getTaskById(taskId));

            tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                    stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
        });
        drawInputTab();
    }

    private void calc(Project pro, boolean useMonty) {
        Instant start = java.time.Instant.now();

        Sequence.sequenceTasks(pro, useMonty);
        outputTab.drawOutputTab(useMonty);
        drawInputTab();

        Instant end = java.time.Instant.now();
        Duration between = java.time.Duration.between(start, end);
        System.out.format((char) 27 + "[31mNote: total in that unit!\n" + (char) 27 + "[39mHours: %02d Minutes: %02d Seconds: %02d Milliseconds: %04d \n",
                between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis()); // 0D, 00:00:01.1001
    }

}
