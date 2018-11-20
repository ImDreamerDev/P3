package dk.aau.ds304e18.gui.output;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

public class OutputTab {
    private final Parent rootPane;
    private final BarChart<String, Number> barChart;
    private final AssignmentTab assignmentTab;

    public OutputTab(Parent rootPane) {
        this.rootPane = rootPane;
        barChart = ((BarChart<String, Number>) rootPane.lookup("#tasksDiagram"));
        AnchorPane pane = ((AnchorPane) rootPane.lookup("#outputScrollView"));
        pane.getChildren().clear();
        ((ListView<Task>) ((VBox) rootPane.lookup("#outputPane")).getChildren().get(1)).getItems().clear();
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output");
        BorderPane borderPane = (BorderPane) rootPane.lookup("#employeesBorderPane");

        barChart.getData().clear();
        assignmentTab = new AssignmentTab(borderPane);

    }

    public void drawOutputTab(boolean useMonty) {
        if (JavaFXMain.selectedProjectId == 0) {

            return;
        }
        GanttTab ganttTab = new GanttTab(rootPane);
        Project pro = LocalObjStorage.getProjectList().stream().
                filter(project -> project.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output: " +
                pro.getName() + ":" + JavaFXMain.selectedProjectId);
        AnchorPane pane = ((AnchorPane) rootPane.lookup("#outputScrollView"));
        pane.getChildren().clear();
        if (pro.getSequence() != null) {


            ganttTab.drawTasks();
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
            assignmentTab.drawEmployees();
        }

        if (pro.getPossibleCompletions() != null) populateChart();

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
}
