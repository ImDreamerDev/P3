package dk.aau.ds304e18.ui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.models.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DependenciesPopup {
    private final Parent rootPane;
    private ListView<Task> listViewDependency;

    private List<Task> taskDependencies;

    public DependenciesPopup(Parent rootPane, ListView<Task> listViewDependency, List<Task> taskDependencies) {
        this.rootPane = rootPane;
        this.listViewDependency = listViewDependency;
        this.taskDependencies = taskDependencies;
        TableView<Task> dependencies = ((TableView<Task>) ((FlowPane) rootPane.getChildrenUnmodifiable().get(3)).getChildren().get(1));
        dependencies.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList()
                .stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId)
                .collect(Collectors.toList())));
        dependencies.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1 && taskDependencies.stream().noneMatch(task -> task.getId() == dependencies.getSelectionModel().getSelectedItem().getId())) {
                addDependency(Collections.singletonList(dependencies.getSelectionModel().getSelectedItem()));
            } else if (event.getClickCount() > 1) {
                removeDependency(Collections.singletonList(dependencies.getSelectionModel().getSelectedItem()));
            }
        });
        dependencies.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        dependencies.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        dependencies.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        dependencies.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("priority"));
        dependencies.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("probabilities"));
        dependencies.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("dependencies"));
        dependencies.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ToolBar bar = ((ToolBar) ((FlowPane) rootPane.getChildrenUnmodifiable().get(3)).getChildren().get(2));
        ((HBox) bar.getItems().get(0)).getChildren().get(0).setOnMouseClicked(event -> addDependency(dependencies.getSelectionModel().getSelectedItems()));
        Button removeDep = (Button) ((HBox) bar.getItems().get(0)).getChildren().get(1);
        removeDep.setOnMouseClicked(event -> removeDependency(dependencies.getSelectionModel().getSelectedItems()));
        removeDep.setTooltip(new Tooltip("Removes the selected task from dependencies"));

        ((HBox) bar.getItems().get(1)).getChildren().get(0).setOnMouseClicked(event -> closeDependenciesPopup());
    }


    public void openDependenciesPopup() {
        rootPane.getChildrenUnmodifiable().get(3).setVisible(true);
    }

    private void closeDependenciesPopup() {
        rootPane.getChildrenUnmodifiable().get(3).setVisible(false);
    }


    /**
     * Method for revoming a dependency from the new task.
     * This happens by selecting the task and pressing the remove button.
     *
     * @param selectedItems
     */
    private void removeDependency(List<Task> selectedItems) {
        for (Task task : selectedItems) {
            if (taskDependencies.contains(task)) {
                taskDependencies.remove(task);
                listViewDependency.setItems(FXCollections.observableArrayList(taskDependencies));
            }
        }
    }

    /**
     * Method for adding a dependency to a new task.
     *
     * @param tasks - The list of the tasks in the project.
     */
    private void addDependency(List<Task> tasks) {
        if (tasks == null || tasks.size() == 0)
            return;
        for (Task task : tasks) {
            if (!taskDependencies.contains(task)) {
                taskDependencies.add(task);
                listViewDependency.setItems(FXCollections.observableArrayList(taskDependencies));
            }
        }
    }
}
