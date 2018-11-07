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
import javafx.scene.Node;
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
    private TableView<Project> tableView;
    private FilteredList<Project> flProjects;
    private HBox projectToolBar;


    public ProjectTab(Parent rootPane, ProjectManager projectManager, InputTab inputTab) {
        this.projectManager = projectManager;
        this.rootPane = rootPane;
        setupProjectTab();
        this.inputTab = inputTab;
    }

    private SortedList<Project> updateProjects() {
        FilteredList<Project> flProjects = new FilteredList<>(FXCollections.observableArrayList(LocalObjStorage.getProjectList()));
        return new SortedList<>(flProjects);
    }

    @SuppressWarnings("unchecked")
    private void setupProjectTab() {
        tableView = ((TableView<Project>) rootPane.lookup("#projectView"));
        projectToolBar = ((HBox) rootPane.lookup("#projectToolbar"));
        setUpProjectTable();
        tableView.setOnMouseClicked(event -> onTableElementSelected());


        Button createButton = ((Button) projectToolBar.getChildren().get(2));
        createButton.setOnMouseClicked(event -> createProject(((TextField) projectToolBar.getChildrenUnmodifiable().get(1))));

        Button archiveButton = ((Button) projectToolBar.getChildren().get(5));
        archiveButton.setOnMouseClicked(event -> archiveProject());

        CheckBox showArchived = ((CheckBox) rootPane.lookup("#showArchivedCheckbox"));
        showArchived.selectedProperty().addListener((ov, old_val, new_val) -> onShowArchived(new_val));

        TextField searchField = ((TextField) projectToolBar.getChildren().get(7));
        searchField.setPromptText("Search here!");
        searchField.setOnKeyReleased(keyEvent -> search(searchField, showArchived));
    }

    /**
     * Method that is used in the Search Function. If the first letter in the search is a string then the name of the project is searched for
     * if it is a number the Id is used.
     *
     * @param str
     * @return true - if it is a letter - false if is a number.
     */
    private boolean isFirstLetter(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Method for creating a new project.
     *
     * @param projectName - the name of the project.
     */
    private void createProject(TextField projectName) {
        new Project(projectName.getText(), projectManager);
        projectName.clear();
        sortedList = updateProjects();
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getState() ==
                ProjectState.ONGOING && project.getCreator() != null && project.getCreator().getId() == projectManager.getId()).collect(Collectors.toList())));
    }

    /**
     * Method for searching for projects using the textbox
     *
     * @param searchField  - the inputfield for the text.
     * @param showArchived - if the show archived box is toggled or not.
     */
    private void search(TextField searchField, CheckBox showArchived) {
        if (isFirstLetter(searchField.getText())) {
            flProjects.setPredicate(p -> Integer.toString(p.getId()).contains(searchField.getText().toLowerCase().trim()));
        } else
            flProjects.setPredicate(p -> p.getName().toLowerCase().contains(searchField.getText().toLowerCase().trim()));

        showArchived.setSelected(!showArchived.isSelected());
        showArchived.setSelected(!showArchived.isSelected());
    }

    /**
     * Method for setting up the table on the project tab.
     */
    private void setUpProjectTable() {
        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory("name"));
        tableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory("creator"));
        tableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory("sequence"));
        tableView.getColumns().get(4).setCellValueFactory(new PropertyValueFactory("duration"));
        tableView.getColumns().get(5).setCellValueFactory(new PropertyValueFactory("state"));
        flProjects = (new FilteredList<Project>(FXCollections.observableArrayList(LocalObjStorage.getProjectList())));
        sortedList = new SortedList<>(flProjects);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getState() ==
                ProjectState.ONGOING && project.getCreator() != null && project.getCreator().getId() == projectManager.getId()).collect(Collectors.toList())));

    }

    /**
     * Method for the display archived box.
     *
     * @param new_val
     */
    private void onShowArchived(boolean new_val) {
        if (!new_val)
            tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getState()
                    == ProjectState.ONGOING && project.getCreator() != null && project.getCreator().getId() == projectManager.getId()).collect(Collectors.toList())));
        else
            tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getCreator() != null && project.getCreator().getId()
                    == projectManager.getId()).collect(Collectors.toList())));
    }

    /**
     * Method for selecting a project.
     */
    private void onTableElementSelected() {
        if (tableView.getSelectionModel().getSelectedIndex() != -1 && JavaFXMain.selectedProjectId !=
                ((int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue())) {
            JavaFXMain.selectedProjectId = ((int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue());
            inputTab.drawInputTab();
            ((Label) projectToolBar.getChildren().get(4)).setText("Selected: " + LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId).getName());
        }
    }

    /**
     * Method for archiving an on going project.
     */
    private void archiveProject() {
        if (JavaFXMain.selectedProjectId != 0) {
            projectManager.addOldProject(LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId));
            tableView.getItems().remove(tableView.getSelectionModel().getSelectedIndex());
        }
    }
}
