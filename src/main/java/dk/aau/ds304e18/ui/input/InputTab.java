package dk.aau.ds304e18.ui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Maths;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.Sequence;
import dk.aau.ds304e18.ui.output.OutputTab;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputTab {

    private List<Task> taskDependencies = new ArrayList<>();
    private final Parent rootPane;
    private final OutputTab outputTab;
    private TableView<Task> tableView;
    private TabPane tabPane;
    private ListView<Task> listViewDependency;
    private DependenciesPopup dependenciesPopup;
    private EmployeeTab employeeTab;

    /**
     * @param rootPane  - This is the parent of all ui elements in the inputTab.
     * @param outputTab - The class outputTab is used.
     */
    public InputTab(Parent rootPane, OutputTab outputTab) {
        this.rootPane = rootPane;
        this.outputTab = outputTab;
        tabPane = ((TabPane) rootPane.getChildrenUnmodifiable().get(1));
        setupInputTab();
    }

    /**
     * The method that draws the inputTab. If the project is ongoing you can interact with the ui, if it isnt you cannot.
     * Uses the method disableInput - if the project is archived.
     * Uses the method enableInput - if the project is ongoing.
     */
    public void drawInputTab() {
        if (JavaFXMain.selectedProjectId != 0 && LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId).getState()
                == ProjectState.ARCHIVED)
            disableInput();
        else
            enableInput();
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().stream().filter(task ->
                task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));

        TableView<Task> dependencies = ((TableView<Task>) ((FlowPane) rootPane.getChildrenUnmodifiable().get(3)).getChildren().get(1));
        dependencies.getItems().clear();
        dependencies.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList()
                .stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId)
                .collect(Collectors.toList())));

        employeeTab.drawEmployees();
        if (JavaFXMain.selectedProjectId != 0)
            outputTab.drawOutputTab(true);
    }

    /**
     * Method that disables the interaction with the ui on the inputTab.
     */
    private void disableInput() {
        BorderPane flowPane = ((BorderPane) rootPane.lookup("#inputFlowPane"));
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        ((VBox) ((Pane) flowPane.getChildren().get(2)).getChildren().get(0)).getChildren().get(0).setDisable(true);
        inputVBox.setDisable(true);
        ((TabPane) flowPane.getParent().getParent().getParent()).getTabs().get(1).setDisable(true);
    }

    /**
     *
     */
    private void enableInput() {
        BorderPane flowPane = ((BorderPane) rootPane.lookup("#inputFlowPane"));
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        ((VBox) ((Pane) flowPane.getChildren().get(2)).getChildren().get(0)).getChildren().get(0).setDisable(false);
        inputVBox.setDisable(false);
        ((TabPane) flowPane.getParent().getParent().getParent()).getTabs().get(1).setDisable(false);
    }

    List<TextField> textFields = new ArrayList<>();

    /**
     * The method that sets up the contents of the whole input tab.
     */
    private void setupInputTab() {
        BorderPane flowPane = ((BorderPane) rootPane.lookup("#inputFlowPane"));

        //Table view
        tableView = ((TableView<Task>) flowPane.getChildren().get(1));
        setupTaskTable();

        //Input view
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        TextField nameTextField = ((TextField) inputVBox.getChildren().get(1));
        nameTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.isBlank()) {
                nameTextField.setStyle("-fx-border-color: #ff0000");
            } else {
                nameTextField.setStyle("");
            }
        });

        TextField priority = ((TextField) inputVBox.getChildren().get(3));
        TextField estimatedTimeTextField = ((TextField) inputVBox.getChildren().get(5));

        HBox probsHBox = ((HBox) inputVBox.getChildren().get(7));
        HBox probsHBox2 = ((HBox) inputVBox.getChildren().get(8));
        HBox probsHBox3 = ((HBox) inputVBox.getChildren().get(9));
        listViewDependency = ((ListView<Task>) inputVBox.getChildren().get(11));
        HBox buttonsForDependencies = (HBox) inputVBox.getChildren().get(12);


        priority.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(priority, newValue, true, false));
        estimatedTimeTextField.textProperty().addListener((observable, oldValue, newValue) ->
                validateNumericInput(estimatedTimeTextField, newValue, false, false));


        TextField probs1 = ((TextField) probsHBox.getChildren().get(0));
        probs1.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs1, newValue, false, true));
        TextField probs2 = ((TextField) probsHBox.getChildren().get(1));
        probs2.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs2, newValue, false, true));

        TextField probs3 = ((TextField) probsHBox2.getChildren().get(0));
        probs3.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs3, newValue, false, true));
        TextField probs4 = ((TextField) probsHBox2.getChildren().get(1));
        probs4.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs4, newValue, false, true));

        TextField probs5 = ((TextField) probsHBox3.getChildren().get(0));
        probs5.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs5, newValue, false, true));
        TextField probs6 = ((TextField) probsHBox3.getChildren().get(1));
        probs6.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(probs6, newValue, false, true));
        
        ((Button) buttonsForDependencies.getChildren().get(0)).setTooltip(new Tooltip("Opens a list of tasks in the project to add as dependencies"));
        buttonsForDependencies.getChildren().get(0).setOnMouseClicked(event -> dependenciesPopup.openDependenciesPopup());

        ((Button) inputVBox.getChildren().get(13)).setTooltip(new Tooltip("Clears all the input fields"));
        inputVBox.getChildren().get(13).setOnMouseClicked(event -> clearInputFields(listViewDependency, probs1, probs2, probs3,
                probs4, probs5, probs6, nameTextField, estimatedTimeTextField, priority));


        //Middle column
        Pane paneSplitter = ((Pane) flowPane.getChildren().get(2));
        VBox vBoxSplitter = ((VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren().get(1));
        TextField numOfEmployees = ((TextField) vBoxSplitter.getChildren().get(1));

        ((Button) ((VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).setTooltip(new Tooltip("Adds the task to the project"));
        //Add task
        ((VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren().get(0)).getChildren().get(0).setOnMouseClicked(event -> {
            List<Probabilities> probabilities = new ArrayList<>();
            if (!probs1.getText().isBlank())
                probabilities.add(new Probabilities(Double.parseDouble(probs1.getText()), Maths.clamp(Double.parseDouble(probs2.getText()), 0.0, 100.0)));
            if (!probs3.getText().isBlank())
                probabilities.add(new Probabilities(Double.parseDouble(probs3.getText()), Maths.clamp(Double.parseDouble(probs4.getText()), 0.0, 100.0)));
            if (!probs5.getText().isBlank())
                probabilities.add(new Probabilities(Double.parseDouble(probs5.getText()), Maths.clamp(Double.parseDouble(probs6.getText()), 0.0, 100.0)));
            if (probabilities.stream().allMatch(probabilities1 -> probabilities1.getProbability() == 0 && probabilities1.getDuration() == 0))
                return;
            if (nameTextField.getText().isBlank() || estimatedTimeTextField.getText().isBlank() || priority.getText().isBlank()) {
                return;
            }


            addTask(nameTextField.getText(), Double.parseDouble(estimatedTimeTextField.getText()),
                    Integer.parseInt(priority.getText()), probabilities);
            clearInputFields(listViewDependency, probs1, probs2, probs3,
                    probs4, probs5, probs6, nameTextField, estimatedTimeTextField, priority);
            numOfEmployees.setText("1");
        });


        numOfEmployees.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(numOfEmployees, newValue, true, false));
        numOfEmployees.setText("1");

        ((Button) vBoxSplitter.getChildren().get(4)).setTooltip(new Tooltip("Calculates the probability for the length of the project"));
        vBoxSplitter.getChildren().get(4).setOnMouseClicked(event -> calculate(
                LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId),
                ((CheckBox) vBoxSplitter.getChildren().get(2)).isSelected(),
                Double.parseDouble(numOfEmployees.getText()), ((CheckBox) vBoxSplitter.getChildren().get(3)).isSelected()));

        ((Button) ((VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren().get(0)).getChildren().get(1)).setTooltip(new Tooltip("Removes the selected task from the project"));
        ((VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren().get(0)).getChildren().get(1).setOnMouseClicked(event -> removeTask());

        tableView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    editTask(listViewDependency, probs1, probs2, probs3,
                            probs4, probs5, probs6, nameTextField, estimatedTimeTextField, priority);
                }
            }
        });
        dependenciesPopup = new DependenciesPopup(rootPane, listViewDependency, taskDependencies);
        employeeTab = new EmployeeTab(rootPane, outputTab);
        drawInputTab();
    }

    /**
     * This method calculates and produces the output.
     *
     * @param project        - the project to calculate.
     * @param numOfEmployees - the amount of employees.
     * @param useFast        - Is the useFast toggled or not. (boolean)
     * @param useMonty       - the monte carlo method is used.
     */
    private void calculate(Project project, boolean useMonty, double numOfEmployees, boolean useFast) {
        project.setNumberOfEmployees(numOfEmployees);
        Instant start = Instant.now();
        Sequence.sequenceTasks(project, useMonty, useFast);
        Instant end = java.time.Instant.now();
        Duration between = java.time.Duration.between(start, end);
        System.out.format((char) 27 + "[31mNote: total in that unit!\n" + (char) 27 +
                        "[39mHours: %02d Minutes: %02d Seconds: %02d Milliseconds: %04d \n",
                between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis()); // 0D, 00:00:01.1001

        outputTab.drawOutputTab(useMonty);
        drawInputTab();
        tabPane.getSelectionModel().select(tabPane.getTabs().get(2));
        outputTab.populateChart();
    }

    /**
     * This method is used to make sure that the estimated time and all the textboxes that only are supposed to take numeric values,
     * actually contain numeric values.
     *
     * @param intField  - a textbox where only numbers can be entered.
     * @param textField - the textbox
     * @param newValue  - the contents of the textbox
     */
    private void validateNumericInput(TextField textField, String newValue, boolean intField, boolean probability) {
        if (intField) {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[[\\D]]", ""));
            }
        } else if (!newValue.matches("\\d*\\.")) {
            textField.setText(newValue.replaceAll("[[^\\d^\\.]]", ""));
        }
        if (probability) {
            return;
        }
        if (newValue.equals(""))
            textField.setStyle("-fx-border-color: red");
        else
            textField.setStyle("");
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
        drawInputTab();
    }

    /**
     * The method for adding a task.
     *
     * @param name          - The name of the task - filled into the textbox.
     * @param estimatedTime - filled into the textbox.
     * @param priority      - filled into textbox.
     * @param probabilities - filled into textbox.
     */
    private void addTask(String name, double estimatedTime, int priority, List<Probabilities> probabilities) {
        List<Task> tasks = LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList());
        Task t = tasks.stream().filter(task -> task.getName().equals(name)).findFirst().orElse(null);
        if (t != null) {
            List<Task> deps = new ArrayList<>(taskDependencies);
            LocalObjStorage.getTaskList().remove(t);
            t.setEstimatedTime(estimatedTime);
            t.setPriority(priority);
            t.getProbabilities().clear();
            t.getProbabilities().addAll(probabilities);
            t.getDependencies().clear();
            t.addDependency(new ArrayList<>(deps));
            LocalObjStorage.getTaskList().add(t);
        } else {
            Task ttt = new Task(name, estimatedTime, priority,
                    LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId));
            ttt.getProbabilities().addAll(probabilities);
            ttt.addDependency(taskDependencies);

        }
    }

    private void editTask(ListView<Task> listViewDependency, TextField... textFields) {
        int taskId = (int) ((TableColumn) tableView.getColumns().get(0)).
                getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();
        Task task = LocalObjStorage.getTaskById(taskId);
        if (task.getProbabilities().size() > 0) {
            textFields[0].setText(task.getProbabilities().get(0).getDuration() + "");
            textFields[1].setText(task.getProbabilities().get(0).getProbability() + "");
        }
        if (task.getProbabilities().size() > 1) {
            textFields[2].setText(task.getProbabilities().get(1).getDuration() + "");
            textFields[3].setText(task.getProbabilities().get(1).getProbability() + "");
        }
        if (task.getProbabilities().size() > 2) {
            textFields[4].setText(task.getProbabilities().get(2).getDuration() + "");
            textFields[5].setText(task.getProbabilities().get(2).getProbability() + "");
        }
        textFields[6].setText(task.getName());
        textFields[7].setText(task.getEstimatedTime() + "");
        textFields[8].setText(task.getPriority() + "");
        taskDependencies = new ArrayList<>(task.getDependencies());
        listViewDependency.setItems(FXCollections.observableArrayList(taskDependencies));
        drawInputTab();
    }


    /**
     * Method for removing a task. A task is selected in the table,
     * and then if the button(cancel task) is pressed the task is removed.
     */
    private void removeTask() {
        int taskId = (int) ((TableColumn) tableView.getColumns().get(0))
                .getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();
        Project project = LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId);
        project.getTasks().remove(LocalObjStorage.getTaskById(taskId));
        project.setSequence("");
        DatabaseManager.removeTask(taskId);
        LocalObjStorage.getTaskList().remove(LocalObjStorage.getTaskById(taskId));
        drawInputTab();
    }

    /**
     * The method that sets up the task table.
     */
    private void setupTaskTable() {
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList()
                .stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId)
                .collect(Collectors.toList())));
        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        tableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("priority"));
        tableView.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("probabilities"));
        tableView.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("dependencies"));
    }
}
