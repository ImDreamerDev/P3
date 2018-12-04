package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.models.Task;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The DependenciesPopup for tasks
 */
class DependenciesPopup {

    /**
     * The root pane of the GUI.
     */
    private final Parent rootPane;

    /**
     * The list view of the current task's dependencies.
     */
    private final ListView<Task> listViewDependency;


    /**
     * The actual list of the task's dependencies.
     */
    private final List<Task> taskDependencies;

    private final TableView<Task> dependencies;


    /**
     * Creates and sets up the DependenciesPopup
     *
     * @param rootPane           The root pane of the GUI
     * @param listViewDependency The list view where the current dependencies are shown to the user.
     */
    DependenciesPopup(Parent rootPane, ListView<Task> listViewDependency) {
        //Init fields
        this.rootPane = rootPane;
        this.listViewDependency = listViewDependency;
        this.taskDependencies = new ArrayList<>();
        //Get the table view from the root pane
        dependencies = ((TableView<Task>) ((FlowPane) rootPane.getChildrenUnmodifiable().get(3)).getChildren().get(1));

        setupDeDependenciesPopup();
    }

    private void setupDeDependenciesPopup() {
        //Get the bottom toolbar of the dependencies popup.
        ToolBar bar = ((ToolBar) ((FlowPane) rootPane.getChildrenUnmodifiable().get(3)).getChildren().get(2));

        ((HBox) bar.getItems().get(0)).getChildren().get(0).setDisable(true);
        ((HBox) bar.getItems().get(0)).getChildren().get(1).setDisable(true);

        //Set the items of the table view, equal to the all the tasks on this project.
        dependencies.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList()
                .stream().filter(task -> {
                    if (task.getProject() == null)
                        return task.getProjectId() == JavaFXMain.selectedProjectId;
                    return task.getProject().getId() == JavaFXMain.selectedProjectId;
                })
                .collect(Collectors.toList())));

        // Set if any element is double clicked either remove them from the dependencies or add them to the dependencies
        dependencies.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1 && taskDependencies.stream().noneMatch(task -> task.getId() == dependencies
                    .getSelectionModel().getSelectedItem().getId())) {
                addDependency(Collections.singletonList(dependencies.getSelectionModel().getSelectedItem()));
            } else if (event.getClickCount() > 1) {
                removeDependency(Collections.singletonList(dependencies.getSelectionModel().getSelectedItem()));
            }
        });

        //Sets the columns of the table view to display the different fields of the task.
        setupTableView(dependencies);
        dependencies.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                showContextualButtons(newValue, bar));

        //Set the selection mode to multiple
        dependencies.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        // Make the add button on click add the selected task to the dependencies.
        ((HBox) bar.getItems().get(0)).getChildren().get(0).setOnMouseClicked(event ->
                addDependency(dependencies.getSelectionModel().getSelectedItems()));

        Tooltip addDependencyTooltip = new Tooltip("Adds the selected task(s) to dependencies");
        addDependencyTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((Button) ((HBox) bar.getItems().get(0)).getChildren().get(0)).setTooltip(addDependencyTooltip);

        //Get the remove button from the toolbar.
        Button removeDep = (Button) ((HBox) bar.getItems().get(0)).getChildren().get(1);

        //Make the remove button remove the selected task from the dependencies.
        removeDep.setOnMouseClicked(event -> removeDependency(dependencies.getSelectionModel().getSelectedItems()));
        //Set the tool tip of the remove button.
        Tooltip removeDependencyTooltip = new Tooltip("Removes the selected task from dependencies");
        removeDependencyTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        removeDep.setTooltip(removeDependencyTooltip);
    }

    static void setupTableView(TableView<Task> dependencies) {
        dependencies.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        dependencies.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        dependencies.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        dependencies.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("priority"));
        dependencies.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("probabilities"));
        dependencies.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("dependencies"));
    }


    /**
     * Opens the dependencies popup
     */
    void openDependenciesPopup() {
        dependencies.getSortOrder().add(dependencies.getColumns().get(0));
        rootPane.getChildrenUnmodifiable().get(3).setVisible(true);
        dependencies.getSelectionModel().clearSelection();
        if (listViewDependency.getSelectionModel().getSelectedIndex() != -1)
            dependencies.getSelectionModel().select(listViewDependency.getSelectionModel().getSelectedItem());
    }

    /**
     * Closes the dependencies popup
     */
    void closeDependenciesPopup() {
        ToolBar bar = ((ToolBar) ((FlowPane) rootPane.getChildrenUnmodifiable().get(3)).getChildren().get(2));
        rootPane.getChildrenUnmodifiable().get(3).setVisible(false);
        ((HBox) bar.getItems().get(0)).getChildren().get(0).setDisable(true);
        ((HBox) bar.getItems().get(0)).getChildren().get(1).setDisable(true);
    }


    /**
     * Method for removing a dependency from the new task.
     * This happens by selecting the task and pressing the remove button.
     *
     * @param selectedItems The current selected items.
     */
    void removeDependency(List<Task> selectedItems) {
        ToolBar bar = ((ToolBar) ((FlowPane) rootPane.getChildrenUnmodifiable().get(3)).getChildren().get(2));
        for (Task task : selectedItems) {
            if (taskDependencies.contains(task)) {
                taskDependencies.remove(task);
                listViewDependency.setItems(FXCollections.observableArrayList(taskDependencies));
            }
        }
        ((HBox) bar.getItems().get(0)).getChildren().get(1).setDisable(true);
        ((HBox) bar.getItems().get(0)).getChildren().get(0).setDisable(false);
    }

    /**
     * Method for adding a dependency to a new task.
     *
     * @param tasks - The list of the tasks in the project.
     */
    void addDependency(List<Task> tasks) {
        ToolBar bar = ((ToolBar) ((FlowPane) rootPane.getChildrenUnmodifiable().get(3)).getChildren().get(2));
        if (tasks == null || tasks.size() == 0)
            return;
        for (Task task : tasks) {
            if (!taskDependencies.contains(task)) {
                taskDependencies.add(task);
                listViewDependency.setItems(FXCollections.observableArrayList(taskDependencies));
            }
        }
        ((HBox) bar.getItems().get(0)).getChildren().get(1).setDisable(false);
        ((HBox) bar.getItems().get(0)).getChildren().get(0).setDisable(true);
    }

    List<Task> getTaskDependencies() {
        return taskDependencies;
    }

    void showContextualButtons(Task newValue, ToolBar bar) {
        if (newValue == null)
            return;
        if (taskDependencies.stream().noneMatch(task -> task.getId() == dependencies.getSelectionModel()
                .getSelectedItem().getId())) {
            ((HBox) bar.getItems().get(0)).getChildren().get(0).setDisable(false);
            ((HBox) bar.getItems().get(0)).getChildren().get(1).setDisable(true);
        } else {
            ((HBox) bar.getItems().get(0)).getChildren().get(0).setDisable(true);
            ((HBox) bar.getItems().get(0)).getChildren().get(1).setDisable(false);
        }
        dependencies.requestFocus();
    }
}

