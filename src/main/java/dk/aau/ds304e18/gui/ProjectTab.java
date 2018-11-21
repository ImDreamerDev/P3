package dk.aau.ds304e18.gui;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.ProjectState;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.ButtonBarSkin;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import java.util.stream.Collectors;

/**
 * The main tab of the program.
 * Displaying all the projects owned by the user.
 */
public class ProjectTab {

    /**
     * The root pane of the GUI.
     */
    private final Parent rootPane;
    /**
     * The sorted list of projects.
     */
    private SortedList<Project> sortedList;

    /**
     * The logged in project manager.
     */
    private final ProjectManager projectManager;

    /**
     * The project table view.
     */
    private TableView<Project> tableView;

    /**
     * The filter list of projects.
     */
    private FilteredList<Project> flProjects;

    /**
     * The project toolbar.
     */
    private HBox projectToolBar;

    /**
     * The main tab pane.
     */
    private final TabPane tabPane;


    public ProjectTab(Parent rootPane, ProjectManager projectManager) {
        //Init the values.
        this.projectManager = projectManager;
        this.rootPane = rootPane;
        tabPane = ((TabPane) rootPane.getChildrenUnmodifiable().get(1));

        //Setup the project tab.
        setupProjectTab();
    }

    /**
     * Update the project list
     *
     * @return Returns a sorted list of all the projects in the local object storage.
     */
    private SortedList<Project> updateProjects() {
        FilteredList<Project> flProjects = new FilteredList<>
                (FXCollections.observableArrayList(LocalObjStorage.getProjectList()));
        return new SortedList<>(flProjects);
    }


    private void setupProjectTab() {
        //Get the table view from the GUI.
        tableView = ((TableView<Project>) rootPane.lookup("#projectView"));

        //Get the left project toolbar from the GUI.
        projectToolBar = ((HBox) rootPane.lookup("#projectToolbar"));
        //Set up the project tab.
        setUpProjectTable();
        //If a table element is click more than once, open the input tab for that project.
        tableView.setOnMouseClicked(event -> {
            onTableElementSelected();
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() > 1) {
                if (JavaFXMain.selectedProjectId != 0)
                    tabPane.getSelectionModel().select(tabPane.getTabs().get(1));
            }
        });
        //Sort the table view after id.
        tableView.getSortOrder().add(tableView.getColumns().get(0));

        //Get the New project button from the GUI.
        Button createButton = ((Button) projectToolBar.getChildren().get(2));
        //Set the tool tip of the new project button.
        createButton.setTooltip(new Tooltip("Creates a new project with the selected name"));

        //Create a new project when the button is clicked.
        createButton.setOnMouseClicked(event ->
                createProject(((TextField) projectToolBar.getChildrenUnmodifiable().get(1))));

        //Get the project Toolbar to the right from the GUI.
        HBox projectToolBarRight = ((HBox) rootPane.lookup("#projectToolbar2"));

        //Gets the archived button from the GUI.
        Button archiveButton = ((Button) projectToolBarRight.getChildren().get(0));
        //Sets the tool tip for the archive button.
        archiveButton.setTooltip(new Tooltip("Archives the selected project"));
        //Sets the archive button to call the archive project method when pressed.
        archiveButton.setOnMouseClicked(event -> archiveProject());

        //Gets the search field from the GUI.
        TextField searchField = ((TextField) projectToolBarRight.getChildren().get(2));
        //Sets the prompt text for the search field.
        searchField.setPromptText("Search here!");
        //When something is typed inside of the search field call search.
        searchField.setOnKeyReleased(keyEvent -> search(searchField));

        //Gets the show archived checkbox from the GUI.
        CheckBox showArchived = ((CheckBox) projectToolBarRight.getChildren().get(3));
        showArchived.selectedProperty().addListener((ov, old_val, new_val) -> onShowArchived(new_val));
        showArchived.setSelected(false);
    }

    /**
     * Method that is used in the Search Function.
     * If the first letter in the search is a string then the name of the project is searched for
     * if it is a number the Id is used.
     *
     * @param str The string to check
     * @return true - if it is a letter - false if is a number.
     */
    private boolean isFirstLetter(String str) {
        //If the str is parsable it's a int otherwise it's a character.
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
        //If we try to create a project and the name is blank make the border red and return.
        if (projectName.getText().isBlank()) {
            projectName.setStyle("-fx-border-color: red");
            return;
        }
        //Remove the red border.
        projectName.setStyle("");
        //Create the new project with the name and the logged in user.
        new Project(projectName.getText().trim(), projectManager);
        //Clear the project name field.
        projectName.clear();
        sortedList = updateProjects();
        //Bind the list to the table.
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        //Clears the items to make sure the correct data is displayed.
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getState() ==
                ProjectState.ONGOING && project.getCreator() != null && project.getCreator()
                .getId() == projectManager.getId()).collect(Collectors.toList())));
    }

    /**
     * Method for searching for projects using the text box
     *
     * @param searchField - the input field for the text.
     */
    private void search(TextField searchField) {
        //If the fist character is a letter, we are searching by name. 
        //Otherwise we are searching by id.
        if (isFirstLetter(searchField.getText())) {
            flProjects.setPredicate(p -> Integer.toString(p.getId()).contains(searchField.getText().toLowerCase().trim()));
        } else
            flProjects.setPredicate(p -> p.getName().toLowerCase().contains(searchField.getText().toLowerCase().trim()));
        //Set the appropriate items.
        tableView.setItems(flProjects);
    }

    /**
     * Method for setting up the table on the project tab.
     */
    private void setUpProjectTable() {
        //Set the columns of the table view to the display the fields named in "".
        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("creator"));
        tableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("sequence"));
        tableView.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("duration"));
        tableView.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("state"));

        //Get the 7th column and set the data equal to the number of tasks in the project.
        TableColumn<Project, String> column = (TableColumn<Project, String>) tableView.getColumns().get(6);
        column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTasks().size() + ""));

        //Set filtered list to all the projects in local storage.
        flProjects = (new FilteredList<>(FXCollections.observableArrayList(LocalObjStorage.getProjectList())));
        //Create a sorted list to make the list both sortable and searchable.
        sortedList = new SortedList<>(flProjects);
        //Bind the list to the table.
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        //Sets the tables items equal to all the projects owned by the user and is ongoing.
        tableView.setItems(FXCollections.observableArrayList(sortedList.stream().
                filter(project -> project.getState() ==
                        ProjectState.ONGOING && project.getCreator() != null && project.getCreator()
                        .getId() == projectManager.getId()).collect(Collectors.toList())));
    }

    /**
     * Method for the display archived box.
     *
     * @param new_val The new value of the button
     */
    private void onShowArchived(boolean new_val) {
        //If the new value is false draw only current projects. Otherwise draw all projects assigned to the 
        // project manager.
        if (!new_val)
            tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getState()
                    == ProjectState.ONGOING && project.getCreator() != null && project
                    .getCreator().getId() == projectManager.getId()).collect(Collectors.toList())));
        else
            tableView.setItems(FXCollections.observableArrayList(sortedList.stream()
                    .filter(project -> project.getCreator() != null && project.getCreator().getId()
                            == projectManager.getId()).collect(Collectors.toList())));

        //Sort the table after id.
        tableView.getSortOrder().add(tableView.getColumns().get(0));
    }

    /**
     * Called when a element in the table view is selected.
     */
    private void onTableElementSelected() {
        //If nothing is selected return.
        if (tableView.getSelectionModel().getSelectedIndex() == -1 || JavaFXMain.selectedProjectId ==
                ((int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel()
                        .getSelectedIndex()).getValue())) {
            return;
        }
        //Update the selected project id.
        JavaFXMain.selectedProjectId = ((int) ((TableColumn) tableView.getColumns().get(0))
                .getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue());

        //Update the input tab.
        JavaFXMain.inputTab.drawInputTab();

        //Set the selected label equal to the selected project name.
        ((Label) projectToolBar.getChildren().get(4)).setText("Selected: " +
                LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId).getName());

        //Get the tabs from the GUI.
        TabPane tabPane = (TabPane) rootPane.getChildrenUnmodifiable().get(1);
        //Enable the other tabs when project is selected.
        tabPane.getTabs().get(1).setDisable(false);
        tabPane.getTabs().get(2).setDisable(false);
    }

    /**
     * Method for archiving an on going project.
     */
    private void archiveProject() {
        //If a project is selected.
        if (JavaFXMain.selectedProjectId != 0) {
            //Set the current project as a old project.
            projectManager.addOldProject(LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId));

            //Update the table view depending on if the show archived is true.
            if (!((CheckBox) rootPane.lookup("#showArchivedCheckbox")).isSelected())
                tableView.setItems(FXCollections.observableArrayList(sortedList.stream().filter(project -> project.getState()
                        == ProjectState.ONGOING && project.getCreator() != null && project.getCreator()
                        .getId() == projectManager.getId()).collect(Collectors.toList())));
            else
                tableView.setItems(FXCollections.observableArrayList(sortedList.stream()
                        .filter(project -> project.getCreator() != null && project.getCreator().getId()
                                == projectManager.getId()).collect(Collectors.toList())));
        }
    }
}
