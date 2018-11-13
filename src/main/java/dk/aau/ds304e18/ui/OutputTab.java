package dk.aau.ds304e18.ui;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class OutputTab {
    private final Parent rootPane;
    private BarChart<String, Number> barChart;
    private static final double arrowLength = 8;
    private static final double arrowWidth = 4;


    public OutputTab(Parent rootPane) {
        this.rootPane = rootPane;
        barChart = ((BarChart<String, Number>) rootPane.lookup("#tasksDiagram"));
        AnchorPane pane = ((AnchorPane) rootPane.lookup("#outputScrollView"));
        pane.getChildren().clear();
        ((ListView<Task>) ((VBox) rootPane.lookup("#outputPane")).getChildren().get(1)).getItems().clear();
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output");
        barChart.getData().clear();
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
        }

    }

    public void populateChart() {
        Project pro = LocalObjStorage.getProjectList().stream()
                .filter(project -> project.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        barChart.getData().clear();
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Probabilities");
        barChart.setAnimated(false);
        List<Double> possibleCompletions = pro.getPossibleCompletions().get(0);
        double total = possibleCompletions.stream().mapToDouble(value -> value).sum();
        double sum = 0;
        for (int i = 0; i < possibleCompletions.size(); i++) {
            double percent = sum / total * 100;
            sum += possibleCompletions.get(i);
            if (percent > 1 && sum / total * 100 < 99)
                series1.getData().add(new XYChart.Data<>(i + "", percent));
        }
        barChart.getData().add(series1);
        series1.getChart().getXAxis().setLabel("Working hours");
        series1.getChart().getYAxis().setLabel("Chance of completion");
    }

    private void drawTasks(Project pro, AnchorPane pane) {
        List<AnchorPane> andhorPanes = new ArrayList<>();
        List<List<Task>> taskListOfTasks = ParseSequence.parseToMultipleLists(pro);
        List<Shape> shapeList = new ArrayList<>();
        HashMap<Task, AnchorPane> taskToAP = new HashMap<>();
        int paddingY = 50;
        int yPerManHour = 110;

        int x = 0, y;
        for (List<Task> seq : taskListOfTasks) {
            y = 0;
            for (Task task : seq) {
                AnchorPane taskBox = new AnchorPane();
                AtomicReference<Double> xVar = new AtomicReference<>((double) 0);
                if (x == 0) xVar.set((double) yPerManHour);

                else {
                    //If current list contains more than prev line
                    if (taskListOfTasks.get(x - 1).size() < taskListOfTasks.get(x).size() && y >= taskListOfTasks.get(x - 1).size()) {
                        xVar.set(andhorPanes.get(andhorPanes.size() - 1).getLayoutX());
                    } else
                        xVar.set(andhorPanes.get(andhorPanes.size() - (y + 2)).getLayoutX() + (taskListOfTasks.get(x - 1).get(y).getEstimatedTime()));

                    if (task.getDependencies().size() > 0) {
                        task.getDependencies().forEach(dependency -> {
                            if (dependency.getEstimatedTime() + taskToAP.get(dependency).getLayoutX() > xVar.get())
                                xVar.set(dependency.getEstimatedTime() + taskToAP.get(dependency).getLayoutX());
                        });
                    }
                }

                taskBox.setLayoutX(xVar.get());
                taskBox.setLayoutY(25 + ((y + (0.5 * x)) * paddingY));
                Rectangle ret = new Rectangle(task.getEstimatedTime(), 20);
                ret.setStroke(Color.BLACK);
                ret.setFill(Color.web("#ff9c00"));
                Tooltip tooltip = new Tooltip(task.getName());
                tooltip.setShowDelay(Duration.millis(100));
                Tooltip.install(ret, tooltip);

                taskBox.getChildren().addAll(ret);
                andhorPanes.add(taskBox);
                y++;
                taskToAP.put(task, taskBox);
            }
            x++;
        }
        /*for (Task task : taskToAP.keySet()) {
            List<Task> dependencies = task.getDependencies();
            if (dependencies != null && dependencies.size() != 0) {
                dependencies.forEach(depTask -> {
                    AnchorPane startAncPane = taskToAP.get(task);
                    AnchorPane endAncPane = taskToAP.get(depTask);
                    double sx, sy, ex, ey;
                    sx = startAncPane.getLayoutX() + startAncPane.getWidth() / 2;
                    sy = startAncPane.getLayoutY() + startAncPane.getHeight() / 2;

                    ex = endAncPane.getLayoutX() - endAncPane.getWidth() / 2;
                    ey = endAncPane.getLayoutY() - endAncPane.getHeight() / 2;
                    Line line = new Line();
                    Line arrow1 = new Line();
                    Line arrow2 = new Line();
                    line.setStartX(sx);
                    line.setStartY(sy);
                    line.setEndX(ex);
                    line.setEndY(ey);


                    double factor = arrowLength / Math.hypot(sx - ex, sy - ey);
                    double factorO = arrowWidth / Math.hypot(sx - ex, sy - ey);

                    // part in direction of main line
                    double dx = (sx - ex) * factor;
                    double dy = (sy - ey) * factor;

                    // part ortogonal to main line
                    double ox = (sx - ex) * factorO;
                    double oy = (sy - ey) * factorO;
                    double triangleP1X = ex + dx - oy, triangleP1Y = ey + dy + ox;
                    double triandleP2X = ex + dx + oy, triangleP2Y = ey + dy - ox;

                    Polygon triangle = new Polygon(ex, ey, triangleP1X, triangleP1Y, triandleP2X, triangleP2Y);
                    shapeList.add(line);
                    shapeList.add(triangle);

                });

            }
        }*/
        pane.getChildren().addAll(andhorPanes);
        //pane.getChildren().addAll(shapeList);
    }
}
