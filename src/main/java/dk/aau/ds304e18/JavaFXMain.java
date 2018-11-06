package dk.aau.ds304e18;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import dk.aau.ds304e18.sequence.Sequence;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class JavaFXMain extends Application {
    private Parent content = null;
    private int selectedProjectId;
    private ProjectManager projectManager;
    private TextField textField;
    private PasswordField passwordField;
    private VBox vBoxLogin;


    @SuppressWarnings("unchecked")
    private void setupProjectTab() {
        DatabaseManager.distributeModels();
        projectManager = LocalObjStorage.getProjectManagerById(projectManager.getId());
        var tableView = ((TableView) content.lookup("#projectView"));
        ((TableColumn) tableView.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<Project, String>("id"));
        ((TableColumn) tableView.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<Project, String>("name"));
        ((TableColumn) tableView.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<Project, String>("creator"));
        ((TableColumn) tableView.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<Project, String>("sequence"));
        ((TableColumn) tableView.getColumns().get(4)).setCellValueFactory(new PropertyValueFactory<Project, String>("duration"));
        ((TableColumn) tableView.getColumns().get(5)).setCellValueFactory(new PropertyValueFactory<Project, String>("state"));
        FilteredList<Project> flProjects = ((FilteredList<Project>) new FilteredList(FXCollections.observableArrayList(LocalObjStorage.getProjectList())));
        SortedList<Project> sortedList = new SortedList<>(flProjects);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        CheckBox showArchived = ((CheckBox) content.lookup("#showArchivedCheckbox"));
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
            if (tableView.getSelectionModel().getSelectedIndex() != -1 && selectedProjectId !=
                    ((int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue())) {
                selectedProjectId = ((int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue());
                drawProjectsTab();
            }
        });

        TextField textField = ((TextField) ((HBox) tableView.getParent().getChildrenUnmodifiable().get(5)).getChildren().get(1));
        Button archiveButton = ((Button) tableView.getParent().getChildrenUnmodifiable().get(3));
        archiveButton.setOnMouseClicked(event -> {
            if (selectedProjectId != 0) {
                projectManager.addOldProject(LocalObjStorage.getProjectById(selectedProjectId));
                tableView.getItems().remove(tableView.getSelectionModel().getSelectedIndex());
            }
        });

        Button createButton = ((Button) tableView.getParent().getChildrenUnmodifiable().get(0));
        createButton.setOnMouseClicked(event -> {
            new Project(((TextField) tableView.getParent().getChildrenUnmodifiable().get(2)).getText(), projectManager);
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

    List<Task> deps = new ArrayList<>();

    private void drawInputTab() {
        var flowPane = ((FlowPane) content.lookup("#inputFlowPane"));
        var tableView = ((TableView) flowPane.getChildren().get(2));
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().stream().filter(task -> task.getProject().getId() == selectedProjectId).collect(Collectors.toList())));
    }

    private void setupInputTab() {
        var flowPane = ((FlowPane) content.lookup("#inputFlowPane"));
        var tableView = ((TableView) flowPane.getChildren().get(2));
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().stream().filter(task -> task.getProject().getId() == selectedProjectId).collect(Collectors.toList())));
        ((TableColumn) tableView.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<Task, String>("id"));
        ((TableColumn) tableView.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
        ((TableColumn) tableView.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<Task, String>("estimatedTime"));
        ((TableColumn) tableView.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<Task, String>("priority"));
        ((TableColumn) tableView.getColumns().get(4)).setCellValueFactory(new PropertyValueFactory<Task, String>("probabilities"));
        ((TableColumn) tableView.getColumns().get(5)).setCellValueFactory(new PropertyValueFactory<Task, String>("dependencies"));
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        TextField nameTextField = ((TextField) inputVBox.getChildren().get(1));
        TextField priority = ((TextField) inputVBox.getChildren().get(3));
        TextField estimatedTimeTextField = ((TextField) inputVBox.getChildren().get(5));
        ListView<Task> listViewDeps = ((ListView<Task>) inputVBox.getChildren().get(11));

        HBox prorbsHBox = ((HBox) inputVBox.getChildren().get(7));
        TextField probs1 = ((TextField) prorbsHBox.getChildren().get(0));
        TextField probs2 = ((TextField) prorbsHBox.getChildren().get(1));
        HBox prorbsHBox2 = ((HBox) inputVBox.getChildren().get(8));
        TextField probs3 = ((TextField) prorbsHBox2.getChildren().get(0));
        TextField probs4 = ((TextField) prorbsHBox2.getChildren().get(1));
        HBox prorbsHBox3 = ((HBox) inputVBox.getChildren().get(9));
        TextField probs5 = ((TextField) prorbsHBox3.getChildren().get(0));
        TextField probs6 = ((TextField) prorbsHBox3.getChildren().get(1));

        HBox buttonsDeps = (HBox) inputVBox.getChildren().get(12);
        buttonsDeps.getChildren().get(0).setOnMouseClicked(event -> {
            int taskId = (int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();
            Task task = LocalObjStorage.getTaskById(taskId);
            if (!deps.contains(task)) {

                deps.add(LocalObjStorage.getTaskById(taskId));
                listViewDeps.setItems(FXCollections.observableArrayList(deps));
            }
        });

        buttonsDeps.getChildren().get(1).setOnMouseClicked(event -> {
            Task task = listViewDeps.getSelectionModel().getSelectedItem();
            if (deps.contains(task)) {
                deps.remove(task);
                listViewDeps.setItems(FXCollections.observableArrayList(deps));
            }
        });


        HBox buttonHbox = (HBox) inputVBox.getChildren().get(13);
        buttonHbox.getChildren().get(0).setOnMouseClicked(event -> {
            Task ttt = new Task(nameTextField.getText(), Integer.parseInt(estimatedTimeTextField.getText()), Integer.parseInt(priority.getText()),
                    LocalObjStorage.getProjectById(selectedProjectId));
            if (!probs1.getText().equals(""))
                ttt.getProbabilities().add(new Probabilities(Integer.parseInt(probs1.getText()), Integer.parseInt(probs2.getText())));
            if (!probs3.getText().equals(""))
                ttt.getProbabilities().add(new Probabilities(Integer.parseInt(probs3.getText()), Integer.parseInt(probs4.getText())));
            if (!probs5.getText().equals(""))
                ttt.getProbabilities().add(new Probabilities(Integer.parseInt(probs5.getText()), Integer.parseInt(probs6.getText())));
            ttt.addDependency(deps);
            tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                    stream().filter(task -> task.getProject().getId() == selectedProjectId).collect(Collectors.toList())));
        });
        VBox vboxSplitter = ((VBox) ((Pane) flowPane.getChildren().get(1)).getChildren().get(0));
        vboxSplitter.getChildren().get(1).setOnMouseClicked(event -> calc(LocalObjStorage.getProjectById(selectedProjectId)));
        vboxSplitter.getChildren().get(0).setOnMouseClicked(event -> {
            int taskId = (int) ((TableColumn) tableView.getColumns().get(0)).getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();

            Project project = LocalObjStorage.getProjectById(selectedProjectId);
            project.getTasks().remove(LocalObjStorage.getTaskById(taskId));
            project.setSequence("");
            DatabaseManager.removeTask(taskId);
            LocalObjStorage.getTaskList().remove(LocalObjStorage.getTaskById(taskId));

            tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                    stream().filter(task -> task.getProject().getId() == selectedProjectId).collect(Collectors.toList())));
        });
        drawInputTab();
    }

    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        try {
            content = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        content.lookup("#loginPane").setVisible(true);
        vBoxLogin = ((VBox) ((Pane) content.getChildrenUnmodifiable().get(2)).getChildrenUnmodifiable().get(0));
        Button loginButton = ((Button) vBoxLogin.getChildrenUnmodifiable().get(3));
        textField = (TextField) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(0)).getChildren().get(1);
        textField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                logIn();
            }
        });

        passwordField = (PasswordField) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(1)).getChildren().get(1);
        passwordField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                logIn();
            }
        });
        loginButton.setOnMouseClicked(event -> logIn());
        Scene scene = new Scene(content, 1280, 720);

        stage.setScene(scene);
        stage.show();
    }

    private void calc(Project pro) {
        Sequence.sequenceTasks(pro);
        drawProjectsTab();
        drawInputTab();
    }

    private void drawProjectsTab() {
        Project pro = LocalObjStorage.getProjectList().stream().filter(project -> project.getId() == selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        ((TabPane) content.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output: " +
                pro.getName() + ":" + selectedProjectId);
        var pane = ((AnchorPane) ((ScrollPane) ((TabPane) content.getChildrenUnmodifiable().get(1)).getTabs().get(2).getContent())
                .getContent());
        pane.getChildren().clear();
        setupInputTab();
        if (pro.getSequence() != null) {
            drawTasks(pro, pane);
            pane.getChildren().add(new Text(10, 10, "Time: " + pro.getDuration()));
        }
    }

    private void drawTasks(Project pro, AnchorPane pane) {
        List<AnchorPane> tasks = new ArrayList<>();
        var res = ParseSequence.parseToMultipleLists(pro);
        int x = 0, y;
        for (List<Task> seq : res) {
            y = 0;
            pane.getChildren().add(new Text(100 * (x + 1) + 50, 50 + (y * 150) - 5, "" + x));
            for (Task task : seq) {
                AnchorPane taskBox = new AnchorPane();
                taskBox.setLayoutX(110 * (x + 1));
                taskBox.setLayoutY(50 + (y * 150));
                Rectangle ret = new Rectangle(100, 100);
                ret.setStroke(Color.BLACK);
                ret.setFill(Color.web("#ed4b00"));
                String text = task.getName();
                if (text.length() > 9) {
                    if (text.charAt(8) == ' ')
                        text = text.substring(0, 9) + "\n" + text.substring(9);
                    else
                        text = text.substring(0, 9) + "-\n" + text.substring(9);
                }
                Text id = new Text(ret.getX() + 5, ret.getY() + 15, "Id: " + task.getId() + "\nName: " + text);
                taskBox.getChildren().addAll(ret, id);
                tasks.add(taskBox);
                y++;
            }
            x++;
        }
        pane.getChildren().addAll(tasks);
    }

    public static void main(String[] args) {
        launch();
    }

    private boolean isFirstLetter(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void logIn() {
        String username = textField.getText();
        String password = passwordField.getText();
        System.out.println(password + " " + username);
        ProjectManager pm = DatabaseManager.logIn(username, password);
        if (pm == null) {
            Label error = ((Label) vBoxLogin.getChildren().get(2));
            error.setText("Error: No such user");
            error.setVisible(true);
        } else {
            content.getChildrenUnmodifiable().get(2).setVisible(false);
            projectManager = pm;
            setupProjectTab();
        }
    }
}