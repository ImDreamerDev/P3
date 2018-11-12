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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class OutputTab {
    private final Parent rootPane;
    private BarChart<String, Number> barChart;

    public OutputTab(Parent rootPane) {
        this.rootPane = rootPane;
        barChart = ((BarChart<String, Number>) rootPane.lookup("#tasksDiagram"));


    }

    public void drawOutputTab(boolean useMonty) {
        Project pro = LocalObjStorage.getProjectList().stream().filter(project -> project.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
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
        Project pro = LocalObjStorage.getProjectList().stream().filter(project -> project.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        barChart.getData().clear();
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Probabilities");
        barChart.setAnimated(false);
        List<Double> possibleCompletions = pro.getPossibleCompletions();
        double total = possibleCompletions.stream().mapToDouble(value -> value).sum();
        double sum = 0;
        for (int i = 0; i < possibleCompletions.size(); i++) {
            sum += possibleCompletions.get(i);
            if (sum / total * 100 > 1 && sum / total * 100 < 99)
                series1.getData().add(new XYChart.Data<>(i + "", sum / total * 100));
        }
        barChart.getData().add(series1);
        series1.getChart().getXAxis().setLabel("Working hours");
        series1.getChart().getYAxis().setLabel("Chance of completion");
    }

    private void drawTasks(Project pro, AnchorPane pane) {
        List<AnchorPane> tasks = new ArrayList<>();
        List<List<Task>> res = ParseSequence.parseToMultipleLists(pro);
        int x = 0, y;
        for (List<Task> seq : res) {
            y = 0;
            for (Task task : seq) {
                AnchorPane taskBox = new AnchorPane();
                taskBox.setLayoutX(110 * (x + 1));
                taskBox.setLayoutY(25 + (y * 50));
                Rectangle ret = new Rectangle(task.getEstimatedTime(), 20);
                ret.setStroke(Color.BLACK);
                ret.setFill(Color.web("#ff9c00"));
                Text id = new Text(ret.getX() + 5, ret.getY() + 15, "\nName: " + task.getName());
                taskBox.getChildren().addAll(ret, id);
                tasks.add(taskBox);
                y++;
            }
            x++;
        }
        pane.getChildren().addAll(tasks);
    }
}
