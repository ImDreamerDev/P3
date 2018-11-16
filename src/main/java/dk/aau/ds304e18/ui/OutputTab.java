package dk.aau.ds304e18.ui;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class OutputTab {
    private final Parent rootPane;
    private BarChart<String, Number> barChart;
    private static final double arrowLength = 8;
    private static final double arrowWidth = 4;
    TableView<Employee> employeeTableView;
    TableView<Task> taskTableView;

    public double zoomFactor = 1;


    public OutputTab(Parent rootPane) {
        this.rootPane = rootPane;
        barChart = ((BarChart<String, Number>) rootPane.lookup("#tasksDiagram"));
        AnchorPane pane = ((AnchorPane) rootPane.lookup("#outputScrollView"));
        pane.getChildren().clear();
        ((ListView<Task>) ((VBox) rootPane.lookup("#outputPane")).getChildren().get(1)).getItems().clear();
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output");
        BorderPane borderPane = (BorderPane) rootPane.lookup("#employeesBorderPane");
        taskTableView = (TableView<Task>) ((VBox) borderPane.getLeft()).getChildren().get(1);
        employeeTableView = (TableView<Employee>) ((VBox) borderPane.getCenter()).getChildren().get(1);
        barChart.getData().clear();
        setupEmployees();
    }

    public void drawOutputTab(boolean useMonty) {
        if (JavaFXMain.selectedProjectId == 0) {

            return;
        }
        Project pro = LocalObjStorage.getProjectList().stream().
                filter(project -> project.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output: " +
                pro.getName() + ":" + JavaFXMain.selectedProjectId);
        AnchorPane pane = ((AnchorPane) rootPane.lookup("#outputScrollView"));
        pane.getChildren().clear();
        if (pro.getSequence() != null) {
            pane.setOnScroll(scrollEvent -> {
                if (scrollEvent.isControlDown()) {
                    zoomFactor += scrollEvent.getDeltaY() * 0.05;
                    if (zoomFactor < 1) zoomFactor = 1;
                    else {
                        pane.getChildren().clear();
                        drawTasks(pro, pane);
                    }
                }

            });

            Label zoomFactorLabel = (Label) rootPane.lookup("#zoomLevelLabel");
            rootPane.lookup("#zoomInButton").setOnMouseClicked(mouseEvent -> {
                zoomIn(pro, pane, zoomFactorLabel);
            });
            rootPane.lookup("#zoomOutButton").setOnMouseClicked(mouseEvent -> {
                zoomOut(pro, pane, zoomFactorLabel);
            });

            rootPane.lookup("#resetZoomButton").setOnMouseClicked(mouseEvent -> {
                zoomFactor = 1;
                zoomFactorLabel.setText("Zoom level: 100%");
                pane.getChildren().clear();
                drawTasks(pro, pane);
            });

            rootPane.setOnKeyReleased(keyEvent -> {
                if (keyEvent.isControlDown()) {
                    if (keyEvent.getText().equals("-")) {
                        zoomOut(pro, pane, zoomFactorLabel);
                    } else if (keyEvent.getText().equals("+")) {
                        zoomIn(pro, pane, zoomFactorLabel);
                    }
                    keyEvent.consume();

                }
            });

            drawTasks(pro, pane);
            if (useMonty) {
                ((ListView<Task>) ((VBox) rootPane.lookup("#outputPane")).getChildren().get(1))
                        .setItems(FXCollections.observableArrayList(ParseSequence.parseToSingleList(pro, true)));
            } else if (pro.getRecommendedPath() != null && !pro.getRecommendedPath().equals("")) {
                ((ListView<Task>) ((VBox) rootPane.lookup("#outputPane")).getChildren().get(1))
                        .setItems(FXCollections.observableArrayList(ParseSequence.parseToSingleList(pro, true)));
            } else
                ((ListView) ((VBox) rootPane.lookup("#outputPane")).getChildren().get(1))
                        .getItems().clear();

            pane.getChildren().add(new Text(10, 10, "Time: " + pro.getDuration()));
            drawEmployees();
        }

    }

    private void zoomOut(Project pro, AnchorPane pane, Label zoomFactorLabel) {
        if (zoomFactor > 0.5) {
            zoomFactor -= 0.1;
            zoomFactorLabel.setText("Zoom level: " + (int) (zoomFactor * 100d) + "%");
            pane.getChildren().clear();
            drawTasks(pro, pane);
        }
    }

    private void zoomIn(Project pro, AnchorPane pane, Label zoomFactorLabel) {
        if (zoomFactor <= 4d) {
            zoomFactor += 0.1;
            zoomFactorLabel.setText("Zoom level: " + (int) (zoomFactor * 100d) + "%");
            pane.getChildren().clear();
            drawTasks(pro, pane);
        }
    }

    private void drawEmployees() {
        taskTableView.getItems().clear();
        employeeTableView.getItems().clear();
        taskTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));
        employeeTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getEmployeeList().
                stream().filter(emp -> emp.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));

    }

    private void setupEmployees() {
        taskTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        taskTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("employees"));

        taskTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getTaskList().
                stream().filter(task -> task.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));


        employeeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        employeeTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));


        TableColumn<Employee, String> column = (TableColumn<Employee, String>) employeeTableView.getColumns().get(1);
        column.setCellValueFactory(cellData ->
        {
            if (cellData.getValue().getCurrentTask() != null && cellData.getValue().getCurrentTask().size() != 0)
                return new SimpleStringProperty("Assigned");
            return new SimpleStringProperty("Not assigned");
        });
        employeeTableView.setItems(FXCollections.observableArrayList(LocalObjStorage.getEmployeeList().
                stream().filter(emp -> emp.getProject().getId() == JavaFXMain.selectedProjectId).collect(Collectors.toList())));

        BorderPane borderPane = (BorderPane) rootPane.lookup("#employeesBorderPane");
        VBox buttons = ((VBox) borderPane.getRight());
        buttons.getChildren().get(0).setOnMouseClicked(event -> assignEmployee());
        buttons.getChildren().get(1).setOnMouseClicked(event -> unassignEmployee());

    }


    private void assignEmployee() {
        if (taskTableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        if (employeeTableView.getSelectionModel().getSelectedItems() == null) {
            return;
        }

        for (Employee employee : employeeTableView.getSelectionModel().getSelectedItems()) {
            taskTableView.getSelectionModel().getSelectedItem().addEmployee(employee);
        }


        drawEmployees();
    }

    private void unassignEmployee() {
        if (taskTableView.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        if (employeeTableView.getSelectionModel().getSelectedItems() == null) {
            return;
        }

        for (Employee employee : employeeTableView.getSelectionModel().getSelectedItems()) {
            taskTableView.getSelectionModel().getSelectedItem().getEmployees().remove(employee);
            employee.getCurrentTask().remove(taskTableView.getSelectionModel().getSelectedItem());
            DatabaseManager.updateEmployee(employee);
        }

        DatabaseManager.updateTask(taskTableView.getSelectionModel().getSelectedItem());

        drawEmployees();
    }

    public void populateChart() {
        Project pro = LocalObjStorage.getProjectList().stream()
                .filter(project -> project.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Probabilities");
        barChart.setAnimated(false);
        List<Double> possibleCompletions = pro.getPossibleCompletions();
        double total = possibleCompletions.stream().mapToDouble(value -> value).sum();
        double sum = 0;
        for (int i = 0; i < possibleCompletions.size(); i++) {
            double percent = sum / total * 100;
            sum += possibleCompletions.get(i);
            if (percent > 1 && sum / total * 100 < 99)
                series.getData().add(new XYChart.Data<>(i + "", percent));
        }
        barChart.getData().add(series);

        series.getChart().getXAxis().setLabel("Working hours");
        series.getChart().getYAxis().setLabel("Chance of completion");
        for (XYChart.Data<String, Number> entry : series.getData()) {
            Tooltip t = new Tooltip("Duration: " + entry.getXValue() + "\nChance: " + entry.getYValue().intValue() + "%");
            t.setShowDelay(Duration.millis(15));
            Tooltip.install(entry.getNode(), t);
        }
    }

    private void drawTasks(Project pro, AnchorPane pane) {
        List<AnchorPane> anchorPanes = new ArrayList<>();
        List<List<Task>> taskListOfTasks = ParseSequence.parseToMultipleLists(pro);
        List<Shape> shapeList = new ArrayList<>();
        HashMap<Task, AnchorPane> taskToAP = new HashMap<>();
        int paddingY = 55, xPadding = 50;
        int yPerManHour = 110;

        int x = 0, y;
        for (List<Task> seq : taskListOfTasks) {
            y = 0;
            for (Task task : seq) {
                AnchorPane taskBox = new AnchorPane();
                AtomicReference<Double> xVar = new AtomicReference<>((double) 0);
                if (x == 0) xVar.set((double) xPadding);

                else {
                    //If current list contains more than prev line
                    if (taskListOfTasks.get(x - 1).size() < taskListOfTasks.get(x).size() && y >= taskListOfTasks.get(x - 1).size()) {
                        xVar.set(anchorPanes.get(anchorPanes.size() - 1).getLayoutX());
                    } else {
                        if (y == 0)
                            xVar.set(anchorPanes.get(anchorPanes.size() - (1)).getLayoutX() +
                                    ((taskListOfTasks.get(x - 1).get(y).getEstimatedTime() * zoomFactor)));
                        else
                            xVar.set(anchorPanes.get(anchorPanes.size() - (y + 2)).getLayoutX() +
                                    ((taskListOfTasks.get(x - 1).get(y).getEstimatedTime() * zoomFactor)));
                    }
                    if (task.getDependencies().size() > 0) {
                        task.getDependencies().forEach(dependency -> {
                            if (dependency.getEstimatedTime() * zoomFactor + taskToAP.get(dependency).getLayoutX() > xVar.get())
                                xVar.set(dependency.getEstimatedTime() * zoomFactor + taskToAP.get(dependency).getLayoutX());
                        });
                    }
                }

                taskBox.setLayoutX(xVar.get());

                taskBox.setLayoutY(35 + ((1.5 * y + (0.5 * x)) * paddingY));
                Rectangle ret = new Rectangle(task.getEstimatedTime() * zoomFactor, 20);
                ret.setStroke(Color.BLACK);
                ret.setFill(Color.web("#ff9c00"));
                Tooltip tooltip = new Tooltip(task.getName());
                tooltip.setShowDelay(Duration.millis(100));
                Tooltip.install(ret, tooltip);

                taskBox.getChildren().addAll(ret);
                anchorPanes.add(taskBox);
                y++;
                taskToAP.put(task, taskBox);
            }
            x++;
        }
        double maxX = 0;
        for (AnchorPane ap : anchorPanes) {
            double thisXMax = ap.getLayoutX() + ((Rectangle) ap.getChildrenUnmodifiable().get(0)).getWidth();
            if (thisXMax > maxX) maxX = thisXMax;
        }
        maxX /= zoomFactor;

        int pixelsBetweenText = 20;
        if (zoomFactor <= 0.7) pixelsBetweenText = 40;
        else if (zoomFactor < 1) pixelsBetweenText = 30;
        else if (zoomFactor > 1.3 && zoomFactor <= 1.6) pixelsBetweenText = 15;
        else if (zoomFactor > 1.6) pixelsBetweenText = 10;

        for (int i = 0; i < maxX; i = i + pixelsBetweenText) {

            Text text = new Text((i * zoomFactor) + xPadding, 25, "" + i);
            text.setRotate(90);

            pane.getChildren().add(text);
            pane.getChildren().add(new Line((i * zoomFactor) + xPadding, 15, (i * zoomFactor) + xPadding, 20));
        }

        HashMap<Task, AtomicInteger> numberOfTasksDependingOnTask = new HashMap<>();
        HashMap<Task, AtomicInteger> numberOfTimesLineHasBeenDrawnForTask = new HashMap<>();

        taskListOfTasks.forEach(lTask -> lTask.forEach(task -> {
            numberOfTasksDependingOnTask.put(task, new AtomicInteger(0));
            numberOfTimesLineHasBeenDrawnForTask.put(task, new AtomicInteger(0));
        }));
        taskListOfTasks.forEach(lTask -> lTask.forEach(task -> {
            task.getDependencies().forEach(dep -> numberOfTasksDependingOnTask.get(dep).incrementAndGet());
        }));


        for (Task task : taskToAP.keySet()) {
            List<Task> dependencies = task.getDependencies();
            if (dependencies != null && dependencies.size() != 0) {
                AtomicInteger depNum = new AtomicInteger(0);
                dependencies.forEach(depTask -> {
                    int numDependantOfTHisTask = numberOfTasksDependingOnTask.get(depTask).get() - numberOfTimesLineHasBeenDrawnForTask.get(depTask).incrementAndGet();

                    AnchorPane startAncPane = taskToAP.get(depTask);
                    AnchorPane endAncPane = taskToAP.get(task);


                    double sx, sy, ex, ey;
                    sx = startAncPane.getLayoutX() + ((Rectangle) startAncPane.getChildrenUnmodifiable().get(0)).getWidth();
                    sy = startAncPane.getLayoutY() + ((Rectangle) startAncPane.getChildrenUnmodifiable().get(0)).getHeight() / 2;

                    int pixelsBetweenLines = 3;
                    int pixelsBetweenVerticalLines = 4;

                    //To kinda ensure EVERYTHING isn't drawn on top of each other
                    if (sy - (numDependantOfTHisTask * pixelsBetweenLines) < (sy - ((Rectangle) startAncPane.getChildrenUnmodifiable().get(0)).getHeight() / 2))
                        sy = startAncPane.getLayoutY() + (numDependantOfTHisTask * pixelsBetweenLines);
                    else
                        sy -= (numDependantOfTHisTask * pixelsBetweenLines);

                    ex = endAncPane.getLayoutX();
                    ey = endAncPane.getLayoutY() + ((Rectangle) endAncPane.getChildrenUnmodifiable().get(0)).getHeight();

                    int pixelsRightOfPaneStartToPoint = 5;
                    if (((Rectangle) endAncPane.getChildrenUnmodifiable().get(0)).getWidth() / 2 >
                            pixelsRightOfPaneStartToPoint + (pixelsBetweenVerticalLines * depNum.get()))
                        ex += pixelsRightOfPaneStartToPoint + (pixelsBetweenVerticalLines * depNum.get());

                    if (sy < ey) ey -= ((Rectangle) endAncPane.getChildrenUnmodifiable().get(0)).getHeight();


                    Line horizontalLine = new Line();
                    horizontalLine.setStartX(sx);
                    horizontalLine.setStartY(sy);
                    horizontalLine.setEndX(ex);
                    horizontalLine.setEndY(sy);
                    shapeList.add(horizontalLine);

                    Line verticalLine = new Line();
                    verticalLine.setStartX(ex);
                    verticalLine.setStartY(sy);
                    verticalLine.setEndX(ex);
                    verticalLine.setEndY(ey);
                    shapeList.add(verticalLine);


                    /*
                    sx = ex;
                    double factor = arrowLength / Math.hypot(sx - ex, sy - ey);
                    double factorO = arrowWidth / Math.hypot(sx - ex, sy - ey);

                    // part in direction of main line
                    double dx = (sx - ex) * factor;
                    double dy = (sy - ey) * factor;

                    // part ortogonal to main line
                    double ox = (sx - ex) * factorO;
                    double oy = (sy - ey) * factorO;
                    double triangleP1X = ex + dx - oy, triangleP1Y = ey + dy + ox;
                    double triangleP2X = ex + dx + oy, triangleP2Y = ey + dy - ox;*/

                    double triangleP1X, triangleP1Y, triangleP2X, triangleP2Y;
                    if (ey > sy) {
                        triangleP1X = ex - 4;
                        triangleP1Y = ey - 4;
                        triangleP2X = ex + 4;
                        triangleP2Y = ey - 4;
                    } else {
                        triangleP1X = ex - 4;
                        triangleP1Y = ey + 4;
                        triangleP2X = ex + 4;
                        triangleP2Y = ey + 4;
                    }

                    Polygon triangle = new Polygon(ex, ey, triangleP1X, triangleP1Y, triangleP2X, triangleP2Y);
                    shapeList.add(triangle);
                    depNum.incrementAndGet();
                });

            }
        }
        pane.getChildren().addAll(anchorPanes);
        pane.getChildren().addAll(shapeList);
    }
}
