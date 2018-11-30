package dk.aau.ds304e18.gui.input;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.math.Maths;
import dk.aau.ds304e18.math.MonteCarlo;
import dk.aau.ds304e18.math.MonteCarloExecutorService;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.Sequence;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InputTab {

    private final Parent rootPane;
    private TableView<Task> tableView;
    private final TabPane tabPane;
    private ListView<Task> listViewDependency;
    private DependenciesPopup dependenciesPopup;
    public EmployeeTab employeeTab;

    private TextField duration1;
    private TextField probability1;

    private TextField duration2;
    private TextField probability2;

    private TextField duration3;
    private TextField probability3;

    private BorderPane flowPane;
    private boolean isEditMode;

    /**
     * @param rootPane - This is the parent of all gui elements in the inputTab.
     */
    public InputTab(Parent rootPane) {
        this.rootPane = rootPane;
        tabPane = ((TabPane) rootPane.getChildrenUnmodifiable().get(1));
        setupInputTab();
    }

    /**
     * The method that draws the inputTab. If the project is ongoing you can interact with the gui, if it isn't you
     * cannot.
     * Uses the method disableInput - if the project is archived.
     * Uses the method enableInput - if the project is ongoing.
     */
    public void drawInputTab() {
        //If the selected project is archived disable the input. Otherwise enable the input.
        if (JavaFXMain.selectedProjectId != 0 && LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId)
                .getState() == ProjectState.ARCHIVED)
            disableInput();
        else
            enableInput();
        //Clear the table view of tasks.
        tableView.getItems().clear();
        //Set the items of the table view to all the tasks on the project.
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().stream().filter(task ->
                task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));

        //Get the dependencies from the GUI.
        TableView<Task> dependencies = ((TableView<Task>) ((FlowPane) rootPane.getChildrenUnmodifiable()
                .get(3)).getChildren().get(1));

        dependencies.getItems().clear();
        dependencies.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList()
                .stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId)
                .collect(Collectors.toList())));

        flowPane = ((BorderPane) rootPane.lookup("#inputFlowPane"));
        Pane paneSplitter = ((Pane) flowPane.getChildren().get(2));
        VBox vBoxSplitter = ((VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren().get(1));
        if (JavaFXMain.selectedProjectId == 0 && LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId) != null)
            ((TextField) vBoxSplitter.getChildren().get(1)).setText(LocalObjStorage.getProjectById
                    (JavaFXMain.selectedProjectId).getNumberOfEmployees() + "");

        employeeTab.drawEmployees();
        if (JavaFXMain.selectedProjectId != 0)
            JavaFXMain.outputTab.drawOutputTab(true);

        tableView.getSortOrder().add(tableView.getColumns().get(0));
    }

    /**
     * Method that disables the interaction with the gui on the inputTab.
     */
    private void disableInput() {
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        ((VBox) ((Pane) flowPane.getChildren().get(2)).getChildren().get(0)).getChildren().get(0).setDisable(true);
        inputVBox.setDisable(true);
        ((TabPane) flowPane.getParent().getParent().getParent()).getTabs().get(1).setDisable(true);
    }

    /**
     *
     */
    private void enableInput() {
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        Node addEditDeleteTaskButtons = ((VBox) ((Pane) flowPane.getChildren()
                .get(2)).getChildren().get(0)).getChildren().get(0);

        addEditDeleteTaskButtons.setDisable(false);
        inputVBox.setDisable(false);
        Tab employeeTab = ((TabPane) flowPane.getParent().getParent().getParent()).getTabs().get(1);
        employeeTab.setDisable(false);
    }


    private void setupDependencies() {
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        HBox buttonsForDependencies = (HBox) inputVBox.getChildren().get(12);
        ((Button) buttonsForDependencies.getChildren().get(0)).setTooltip(
                new Tooltip("Opens a list of tasks in the project to add as dependencies"));

        buttonsForDependencies.getChildren().get(0).setOnMouseClicked
                (event -> dependenciesPopup.openDependenciesPopup());

        listViewDependency = ((ListView<Task>) inputVBox.getChildren().get(11));
        listViewDependency.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                dependenciesPopup.openDependenciesPopup();
            }
        });
        dependenciesPopup = new DependenciesPopup(rootPane, listViewDependency);
    }


    /**
     * The method that sets up the contents of the whole input tab.
     */
    private void setupInputTab() {
        flowPane = ((BorderPane) rootPane.lookup("#inputFlowPane"));

        //Table view
        tableView = ((TableView<Task>) flowPane.getChildren().get(1));
        setupTaskTable();

        //Input view
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        TextField nameTextField = ((TextField) inputVBox.getChildren().get(1));
        nameTextField.setTooltip(new Tooltip("The name of the Task" +
                System.lineSeparator() + "Can be both letters and numbers"));

/*
        nameTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.isBlank()) {
                nameTextField.setStyle("-fx-border-color: #ff0000");
            } else {
                nameTextField.setStyle("");
            }
        });
*/
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            TableView<Task> dependencies = ((TableView<Task>) ((FlowPane) rootPane.getChildrenUnmodifiable()
                    .get(3)).getChildren().get(1));
            Task task = LocalObjStorage.getTaskList().stream()
                    .filter(t -> t.getProject().getId() == JavaFXMain.selectedProjectId && t.getName()
                            .equals(newValue)).findFirst().orElse(null);

            if (task != null && !isEditMode) {
                nameTextField.setStyle("-fx-border-color: #ff9c00");
            } else if (((task == null) && nameTextField.getText().isBlank()) || !nameTextField.getText().isBlank()) {
                nameTextField.setStyle("");
            } else {
                nameTextField.setStyle("-fx-border-color: #ff0000");
            }

            Button addOrEditTaskButton = (Button) ((VBox) ((VBox) ((Pane) flowPane.getChildren().get(2))
                    .getChildren().get(0)).getChildren().get(0)).getChildren().get(0);

            if (task != null) {
                addOrEditTaskButton
                        .setText("Update Task");
                List<Task> tasks = LocalObjStorage.getTaskList()
                        .stream().filter(task2 -> task2.getProject()
                                .getId() == JavaFXMain.selectedProjectId && task2.getId() != task.getId())
                        .collect(Collectors.toList());
                dependencies.getItems().clear();
                dependencies.setItems(FXCollections.observableArrayList(tasks));
            } else {
                addOrEditTaskButton
                        .setText("Add Task");
                List<Task> tasks = LocalObjStorage.getTaskList()
                        .stream().filter(task2 -> task2.getProject().getId() == JavaFXMain.selectedProjectId)
                        .collect(Collectors.toList());
                dependencies.getItems().clear();
                dependencies.setItems(FXCollections.observableArrayList(tasks));
            }
        });
        TextField priority = ((TextField) inputVBox.getChildren().get(3));
        priority.setTooltip(new Tooltip("The priority of the task, the bigger the more important" +
                System.lineSeparator() + "This value is measured in integers"));
        priority.textProperty().addListener((observable, oldValue, newValue) ->
                validateNumericInput(priority, newValue, true));

        TextField estimatedTimeTextField = ((TextField) inputVBox.getChildren().get(5));
        estimatedTimeTextField.setTooltip(new Tooltip("The average time of the task" +
                System.lineSeparator() + "Can be a decimal number separated by point"));

        estimatedTimeTextField.textProperty().addListener((observable, oldValue, newValue) ->
                validateNumericInput(estimatedTimeTextField, newValue, false));

        setUpProbabilitiesFields();
        setupDependencies();

        Button clearInputButton = ((Button) inputVBox.getChildren().get(13));
        clearInputButton.setTooltip(new Tooltip("Clears all the input fields"));
        clearInputButton.setOnMouseClicked(event -> clearInputFields(duration1,
                probability1, duration2, probability2, duration3, probability3,
                nameTextField, estimatedTimeTextField, priority));

        //Input view end

        //Right side input
        Pane paneSplitter = ((Pane) flowPane.getChildren().get(2));
        VBox vBoxWhereCalculateButtonIs = (VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren().get(1);
        VBox vBoxSplitter = vBoxWhereCalculateButtonIs;
        TextField numOfMonte = ((TextField) vBoxSplitter.getChildren().get(1));
        numOfMonte.setTooltip(new Tooltip("The amount of times the project will be simulated"));
        numOfMonte.textProperty().addListener((observable, oldValue, newValue) ->
                validateNumericInput(numOfMonte, newValue, true));
        TextField numOfEmployees = ((TextField) vBoxSplitter.getChildren().get(3));
        numOfEmployees.setTooltip(new Tooltip("The amount of tasks which can be worked on in parallel" +
                System.lineSeparator() + "Input must be a positive integer"));
        numOfEmployees.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank() && newValue.charAt(0) == '0') {
                numOfEmployees.setText(newValue.replace("0", ""));
            }
            if (!newValue.isBlank())
                numOfEmployees.setStyle("");
        });

        Node addTaskButton = ((VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren().get(0))
                .getChildren().get(0);
        ((Button) addTaskButton).setTooltip(new Tooltip("Adds the task to the project"));
        //Add task
        addTaskButton.setOnMouseClicked(event -> {
            List<Probabilities> probabilities = convertToProbabilities();

            if (validate(nameTextField, estimatedTimeTextField, priority) != 0)
                return;
            boolean taskNameIsThere = LocalObjStorage.getTaskList().stream().anyMatch(task ->
                    task.getName().equals(nameTextField.getText()) && task.getProject()
                            .getId() == JavaFXMain.selectedProjectId);
            if (taskNameIsThere && !isEditMode) {
                return;
            }
            addTask(nameTextField.getText(), Double.parseDouble(estimatedTimeTextField.getText()),
                    Integer.parseInt(priority.getText()), probabilities);
            clearInputFields(duration1, probability1, duration2, probability2, duration3, probability3,
                    nameTextField, estimatedTimeTextField, priority);
        });

        numOfEmployees.textProperty().addListener((observable, oldValue, newValue) ->
                validateNumericInput(numOfEmployees, newValue, true));

        numOfEmployees.setTooltip(new Tooltip("The amount of tasks which can be worked on in parallel" +
                System.lineSeparator() + "Input must be an integer"));

        Button CalculateButton = (Button) vBoxSplitter.getChildren().get(5);
        CalculateButton.setTooltip(new Tooltip("Calculates the probability for the length of the project"));

        CheckBox useFastCheckbox = (CheckBox) vBoxSplitter.getChildren().get(4);
        Tooltip.install(useFastCheckbox,
                new Tooltip("If checked the program will try to give the most optimal path for tasks"));

        CalculateButton.setOnMouseClicked(event -> {
            disableInput();
            rootPane.lookup("#projectView").setDisable(true);
            if (numOfEmployees.getText().isBlank()) {
                numOfEmployees.setStyle("-fx-background-color: red");
                return;
            }

            javafx.concurrent.Task<Void> calcTask = calculate(
                    LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId),
                    Double.parseDouble(numOfEmployees.getText()),
                    ((CheckBox) useFastCheckbox).isSelected(), Integer.parseInt(numOfMonte.getText()));
            ProgressBar bar = new ProgressBar();
            bar.progressProperty().bind(calcTask.progressProperty());
            CalculateButton.setText("Stop");
            CalculateButton.setOnMouseClicked(event1 -> {
                calcTask.cancel();
                vBoxWhereCalculateButtonIs.getChildren().remove(bar);
                CalculateButton.setText("Calculate");
                rootPane.lookup("#projectView").setDisable(false);
                setupInputTab();
            });


            vBoxWhereCalculateButtonIs.getChildren().add(bar);
            bar.setTooltip(new Tooltip("Current progress."));
            calcTask.progressProperty().addListener((observable, oldValue, newValue) ->
                    bar.getTooltip().setText("Progress: " + Math.round(calcTask.getWorkDone() * 100) + "%"));

            calcTask.setOnSucceeded(event1 -> {
                JavaFXMain.outputTab.drawOutputTab(true);
                tabPane.getSelectionModel().select(tabPane.getTabs().get(2));
                vBoxWhereCalculateButtonIs.getChildren().remove(bar);
                enableInput();
                rootPane.lookup("#projectView").setDisable(false);
                MonteCarloExecutorService.shutdownExecutor();
                CalculateButton.setText("Calculate");
                setupInputTab();
            });

            MonteCarloExecutorService.init();
            Thread thread = new Thread(calcTask);
            thread.setPriority(9);
            thread.setName("Calculate");
            thread.start();
        });

        Button editTaskButton = (Button) ((VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren()
                .get(0)).getChildren().get(1);
        editTaskButton.setTooltip(new Tooltip("Edits the selected task from the project"));
        editTaskButton.setOnMouseClicked(event -> editTask(duration1, probability1, duration2, probability2, duration3,
                probability3, nameTextField, estimatedTimeTextField, priority));

        Button removeTaskButton = (Button) ((VBox) ((VBox) paneSplitter.getChildren().get(0)).getChildren()
                .get(0)).getChildren().get(2);
        removeTaskButton.setTooltip(new Tooltip("Removes the selected task from the project"));
        removeTaskButton.setOnMouseClicked(event -> removeTask());
        //End right side input


        tableView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                editTask(duration1, probability1, duration2, probability2, duration3,
                        probability3, nameTextField, estimatedTimeTextField, priority);
            }
        });

        employeeTab = new EmployeeTab(rootPane);
        drawInputTab();
    }

    private List<Probabilities> convertToProbabilities() {
        List<Probabilities> probabilities = new ArrayList<>();
        if (!probability1.getText().isBlank())
            probabilities.add(new Probabilities(Double.parseDouble(duration1.getText()),
                    Maths.clamp(Double.parseDouble(probability1.getText()), 0.0, 100.0)));
        if (!probability2.getText().isBlank())
            probabilities.add(new Probabilities(Double.parseDouble(duration2.getText()),
                    Maths.clamp(Double.parseDouble(probability2.getText()), 0.0, 100.0)));
        if (!probability3.getText().isBlank())
            probabilities.add(new Probabilities(Double.parseDouble(duration3.getText()),
                    Maths.clamp(Double.parseDouble(probability3.getText()), 0.0, 100.0)));
        return probabilities;
    }

    private void setUpProbabilitiesFields() {
        VBox inputVBox = ((VBox) flowPane.getChildren().get(0));
        HBox probabilityHBox = ((HBox) inputVBox.getChildren().get(7));
        HBox probabilityHBox2 = ((HBox) inputVBox.getChildren().get(8));
        HBox probabilityHBox3 = ((HBox) inputVBox.getChildren().get(9));

        duration1 = ((TextField) probabilityHBox.getChildren().get(0));
        probability1 = ((TextField) probabilityHBox.getChildren().get(1));

        duration2 = ((TextField) probabilityHBox2.getChildren().get(0));
        probability2 = ((TextField) probabilityHBox2.getChildren().get(1));

        duration3 = ((TextField) probabilityHBox3.getChildren().get(0));
        probability3 = ((TextField) probabilityHBox3.getChildren().get(1));

        List<TextField> durations = new ArrayList<>();
        durations.add(duration1);
        durations.add(duration2);
        durations.add(duration3);

        List<TextField> probabilities = new ArrayList<>();
        probabilities.add(probability1);
        probabilities.add(probability2);
        probabilities.add(probability3);

        for (int i = 0; i < 3; i++) {
            durations.get(i).setTooltip(new Tooltip("Estimated duration of the task" +
                    System.lineSeparator() + "Can be a decimal number separated by point"));
            probabilities.get(i).setTooltip(new Tooltip("The chance of the task being finished at the given time" +
                    System.lineSeparator()
                    + "Input is given in percentage and can be a decimal number separated by point"));
            int finalI = i;
            durations.get(i).textProperty().addListener((observable, oldValue, newValue) ->
                    validateNumericInput(durations.get(finalI), newValue, false));
            probabilities.get(i).textProperty().addListener((observable, oldValue, newValue) ->
                    validateNumericInput(probabilities.get(finalI), newValue, false));
        }

    }

    private int validate(TextField... textFields) {
        int failed = 0;
        for (TextField textField : textFields) {
            if (textField.getText().isBlank()) {
                failed++;
                textField.setStyle("-fx-border-color: red");
            } else {
                textField.setStyle("");
            }
        }
        return failed;
    }


    /**
     * This method calculates and produces the output.
     *
     * @param project        - the project to calculate.
     * @param numOfEmployees - the amount of employees.
     * @param useFast        - Is the useFast toggled or not. (boolean)
     */
    private javafx.concurrent.Task<Void> calculate(Project project, double numOfEmployees,
                                                   boolean useFast, int numOfMonte) {
        return new javafx.concurrent.Task<>() {

            @Override
            protected void cancelled() {
                MonteCarloExecutorService.shutdownNow();
            }

            @Override
            protected Void call() {
                //Set the number of employees of the project.
                project.setNumberOfEmployees(numOfEmployees);
                //Start time taking.
                Instant start = Instant.now();
                //Sequence the tasks.
                MonteCarlo.progressProperty().addListener((obs, oldProgress, newProgress) ->
                        updateProgress(newProgress.doubleValue(), 1));

                Sequence.sequenceAndCalculateProject(project, useFast, numOfMonte);
                //Stop the time taking.
                Instant end = java.time.Instant.now();
                //Calculate the time between start and end.
                Duration between = java.time.Duration.between(start, end);
                //Print the result out.
                System.out.format((char) 27 + "[31mNote: total in that unit!\n" + (char) 27 +
                                "[39mHours: %02d Minutes: %02d Seconds: %02d Milliseconds: %04d \n",
                        between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis());
                // 0D, 00:00:01.1001

                //Update all the tasks.
                project.getTasks().forEach(DatabaseManager::updateTask);
                return null;
            }
        };
    }

    /**
     * This method is used to make sure that the estimated time and all the text boxes that only are supposed to
     * take numeric values, actually contain numeric values.
     *
     * @param intField  - a text box where only numbers can be entered.
     * @param textField - the text box
     * @param newValue  - the contents of the text box
     */
    private void validateNumericInput(TextField textField, String newValue, boolean intField) {
        //If it's a int field. Only allow numeric values.
        if (intField) {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[[\\D]]", ""));
            }
        } else if (!newValue.matches("\\d*\\.")) {
            //Otherwise allow the dot(.) separator.
            textField.setText(newValue.replaceAll("[[^\\d^\\.]]", ""));
        }
    }

    /**
     * This method clears the whole input field for creating a new task. This means all the text boxes and the
     * dependencies table.
     *
     * @param textFields - the specific text box
     */
    private void clearInputFields(TextField... textFields) {
        //Clear all the text fields.
        for (TextField textField : textFields)
            textField.clear();
        //Clear the dependencies.
        dependenciesPopup.getTaskDependencies().clear();
        //Clear the GUI dependencies.
        listViewDependency.setItems(FXCollections.observableArrayList(dependenciesPopup.getTaskDependencies()));
        setAddTaskButtonText();
        //Update the GUI.
        drawInputTab();
    }

    /**
     * The method for adding a task.
     *
     * @param name          - The name of the task - filled into the text box.
     * @param estimatedTime - filled into the text box.
     * @param priority      - filled into text box.
     * @param probabilities - filled into text box.
     */
    private void addTask(String name, double estimatedTime, int priority, List<Probabilities> probabilities) {
        //Get all the tasks on this project.
        List<Task> tasks = LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId)
                .collect(Collectors.toList());
        //Get the task with the same name otherwise returns null to signify no task with that name exists.
        Task t = tasks.stream().filter(task -> task.getName().equals(name)).findFirst().orElse(null);
        //If we found a task.
        if (t != null) {
            //Get the dependencies currently selected.
            List<Task> dependencies = new ArrayList<>(dependenciesPopup.getTaskDependencies());
            //Remove the task from local object storage.
            LocalObjStorage.getTaskList().remove(t);
            //Set the fields of the task.
            t.setEstimatedTime(estimatedTime);
            t.setPriority(priority);
            t.getProbabilities().clear();
            t.getProbabilities().addAll(probabilities);
            t.getDependencies().clear();
            t.addDependency(new ArrayList<>(dependencies));
            //Re add the task to the local object storage.
            LocalObjStorage.getTaskList().add(t);
            //And update the database with the changed task.
            DatabaseManager.updateTask(t);
            setAddTaskButtonText();
        } else {
            //Otherwise create a new task.
            Task ttt = new Task(name, estimatedTime, priority,
                    LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId));
            ttt.getProbabilities().addAll(probabilities);
            ttt.addDependency(dependenciesPopup.getTaskDependencies());

        }
    }

    private void setAddTaskButtonText() {
        Button addTaskButton = (Button) ((VBox) ((VBox) ((Pane) flowPane.getChildren().get(2)).getChildren()
                .get(0)).getChildren().get(0)).getChildren().get(0);
        addTaskButton.setText("Add Task");
        isEditMode = false;
    }


    private void editTask(TextField... textFields) {

        Button addTaskButton = (Button) ((VBox) ((VBox) ((Pane) flowPane.getChildren().get(2)).getChildren()
                .get(0)).getChildren().get(0)).getChildren().get(0);
        addTaskButton
                .setText("Update Task");
        //If no task is selected just return.
        if (tableView.getSelectionModel().getSelectedIndex() == -1)
            return;


        //Get the task id from the selected row.
        int taskId = (int) ((TableColumn) tableView.getColumns().get(0)).
                getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();
        //Get the actual task from the id.
        Task task = LocalObjStorage.getTaskById(taskId);
        isEditMode = true;
        //Check if task probabilities exists in task, and then fill them out. 
        if (task.getProbabilities().size() > 0) {
            textFields[0].setText(task.getProbabilities().get(0).getDuration() + "");
            textFields[1].setText(task.getProbabilities().get(0).getProbability() + "");
        } else {
            textFields[0].setText("");
            textFields[1].setText("");
        }
        if (task.getProbabilities().size() > 1) {
            textFields[2].setText(task.getProbabilities().get(1).getDuration() + "");
            textFields[3].setText(task.getProbabilities().get(1).getProbability() + "");
        } else {
            textFields[2].setText("");
            textFields[3].setText("");
        }
        if (task.getProbabilities().size() > 2) {
            textFields[4].setText(task.getProbabilities().get(2).getDuration() + "");
            textFields[5].setText(task.getProbabilities().get(2).getProbability() + "");
        } else {
            textFields[4].setText("");
            textFields[5].setText("");
        }
        //Fill out the name of the task.
        textFields[6].setText(task.getName());
        textFields[6].setStyle("");
        //Fill out the estimated time of the task.
        textFields[7].setText(task.getEstimatedTime() + "");
        //Fill out the priority of the task.
        textFields[8].setText(task.getPriority() + "");

        //Fill out the task dependencies.
        dependenciesPopup.getTaskDependencies().clear();
        dependenciesPopup.getTaskDependencies().addAll(new ArrayList<>(task.getDependencies()));
        listViewDependency.setItems(FXCollections.observableArrayList(dependenciesPopup.getTaskDependencies()));
        //Update the input tab to reflect the change.
        TableView<Task> dependencies = ((TableView<Task>) ((FlowPane) rootPane.getChildrenUnmodifiable()
                .get(3)).getChildren().get(1));
        //Set the items of the table view, equal to the all the tasks on this project.

        List<Task> tasks = LocalObjStorage.getTaskList().stream().filter(task2 ->
                task2.getProject().getId() == JavaFXMain.selectedProjectId && task2.getId() != task.getId())
                .collect(Collectors.toList());
        drawInputTab();
        dependencies.getItems().clear();
        dependencies.setItems(FXCollections.observableArrayList(tasks));
    }


    /**
     * Method for removing a task. A task is selected in the table,
     * and then if the button(cancel task) is pressed the task is removed.
     */
    private void removeTask() {
        //Get the selected task id.
        int taskId = (int) ((TableColumn) tableView.getColumns().get(0))
                .getCellObservableValue(tableView.getSelectionModel().getSelectedIndex()).getValue();
        //Get the current project.
        Project project = LocalObjStorage.getProjectById(JavaFXMain.selectedProjectId);
        //Remove the selected task from the project.
        project.removeTask(LocalObjStorage.getTaskById(taskId));
        //Reset the sequence.
        project.setSequence("");
        //Remove the task from the database.
        DatabaseManager.removeTask(taskId);
        //And removed it from the local storage.
        LocalObjStorage.getTaskList().remove(LocalObjStorage.getTaskById(taskId));

        //Update the GUI to reflect this change.
        drawInputTab();
    }

    /**
     * The method that sets up the task table.
     */
    private void setupTaskTable() {
        //Set the table view items to all the tasks on the current project.
        tableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList()
                .stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId)
                .collect(Collectors.toList())));

        //Sets the columns of the table view to display the different fields of the task.
        DependenciesPopup.setupTableView(tableView);

        //Set the sorting order of the table to sort after id.
        tableView.getSortOrder().add(tableView.getColumns().get(0));
    }
}
