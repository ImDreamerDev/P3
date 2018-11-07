package dk.aau.ds304e18.ui;
/*
 * Author: Lasse Stig Emil Rasmussen
 * Email: lser17@student.aau.dk
 * Class: Software 2nd semester
 */

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.ProjectState;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.stream.Collectors;

public class ProjectTab {

    private Parent rootPane;
    private SortedList<Project> sortedList;
    private ProjectManager projectManager;
    private InputTab inputTab;
  

    public ProjectTab(Parent rootPane, ProjectManager projectManager, InputTab inputTab) {
        this.projectManager = projectManager;
        this.rootPane = null;
        this.rootPane = rootPane;
        setupProjectTab();
        this.inputTab = inputTab;
    }

    private SortedList<Project> updateProjects() {
        FilteredList<Project> flProjects = ((FilteredList<Project>) new FilteredList(FXCollections.observableArrayList(LocalObjStorage.getProjectList())));
        return new SortedList<>(flProjects);
    }

    @SuppressWarnings("unchecked")
    private void setupProjectTab() {

        var tableView = ((TableView) rootPane.lookup("#projectView"));
        ((TableColumn) tableView.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<Project, String>("id"));
        ((TableColumn) tableView.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<Project, String>("name"));
        ((TableColumn) tableView.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<Project, String>("creator"));
        ((TableColumn) tableView.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<Project, String>("sequence"));
        ((TableColumn) tableView.getColumns().get(4)).setCellValueFactory(new PropertyValueFactory<Project, String>("duration"));
        ((TableColumn) tableView.getColumns().get(5)).setCellValueFactory(new PropertyValueFactory<Project, String>("state"));
        FilteredList<Project> flProjects = ((FilteredList<Project>) new FilteredList(FXCollections.observableArrayList(LocalObjStorage.getProjectList())));
        sortedList = new SortedList<>(flProjects);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        CheckBox showArchived = ((CheckBox) rootPane.lookup("#showArchivedCheckbox"));
        tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getState() ==
                ProjectState.ONGOING && project.getCreator() != null && project.getCreator().getId() == projectManager.getId()).collect(Collectors.toList())));

        showArchived.selectedProperty().addListener((ov, old_val, new_val) -> {
            if (!new_val)
                tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getState()
                        == ProjectState.ONGOING && project.getCreator() != null && project.getCreator().getId() == projectManager.getId()).collect(Collectors.toList())));
            else
                tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getCreator() != null && project.getCreator().getId()
                        == projectManager.getId()).collect(Collectors.toList())));
        });

        tableView.setOnMouseClicked(event -> {
            if (tableView.getSelectionModel().getSelectedIndex() != -1 && JavaFXMain.selectedProjectId !=
                    ((int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue())) {
                JavaFXMain.selectedProjectId = ((int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue());
                inputTab.drawInputTab();
            }
        });

        TextField textField = ((TextField) ((HBox) tableView.getParent().getChildrenUnmodifiable().get(5)).getChildren().get(1));
        Button archiveButton = ((Button) tableView.getParent().getChildrenUnmodifiable().get(3));
        archiveButton.setOnMouseClicked(event -> {
            if (JavaFXMain.selectedProjectId != 0) {
                projectManager.addOldProject(LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId));
                tableView.getItems().remove(tableView.getSelectionModel().getSelectedIndex());
            }
        });

        Button createButton = ((Button) tableView.getParent().getChildrenUnmodifiable().get(0));
        createButton.setOnMouseClicked(event -> {
            new Project(((TextField) tableView.getParent().getChildrenUnmodifiable().get(2)).getText(), projectManager);
            sortedList = updateProjects();
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getState() ==
                    ProjectState.ONGOING && project.getCreator() != null && project.getCreator().getId() == projectManager.getId()).collect(Collectors.toList())));
        });

        textField.setPromptText("Search here!");
        textField.setOnKeyReleased(keyEvent -> {
            if (isFirstLetter(textField.getText())) {
                flProjects.setPredicate(p -> Integer.toString(p.getId()).contains(textField.getText().toLowerCase().trim()));
            } else
                flProjects.setPredicate(p -> p.getName().toLowerCase().contains(textField.getText().toLowerCase().trim()));

            showArchived.setSelected(!showArchived.isSelected());
            showArchived.setSelected(!showArchived.isSelected());
        });
    }

    private boolean isFirstLetter(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
