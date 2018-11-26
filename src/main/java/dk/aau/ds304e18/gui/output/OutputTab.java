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

/**
 * The outputTab of the GUI.
 */
public class OutputTab {
    private final Parent rootPane;
    private final BarChart<String, Number> barChart;
    private final AssignmentTab assignmentTab;

    /**
     * Create and sets up a output tab.
     *
     * @param rootPane The root pane of the GUI.
     */
    public OutputTab(Parent rootPane) {
        this.rootPane = rootPane;
        //Get the chart from the GUI.
        barChart = ((BarChart<String, Number>) rootPane.lookup("#tasksDiagram"));
        //Get the gantt pane from the GUI.
        AnchorPane pane = ((AnchorPane) rootPane.lookup("#outputScrollView"));
        //Clean the pane.
        pane.getChildren().clear();
        //Clear the recommend path
        ((ListView<Task>) ((VBox) rootPane.lookup("#outputPane")).getChildren().get(1)).getItems().clear();
        //Set the tab name.
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output");
        //Get the employee border pane.
        BorderPane borderPane = (BorderPane) rootPane.lookup("#employeesBorderPane");

        //Clear the chart.
        barChart.getData().clear();
        //Create a new assignment tab.
        assignmentTab = new AssignmentTab(borderPane);

    }

    /**
     * The method for drawing the whole outputTab.
     *
     * @param useMonty - Boolean for using or not using monte carlo.
     */
    public void drawOutputTab(boolean useMonty) {
        if (JavaFXMain.selectedProjectId == 0) {
            return;
        }
        // adds the ganttTab button to the tab pane.
        GanttTab ganttTab = new GanttTab(rootPane);

        Project pro = LocalObjStorage.getProjectList().stream().
                filter(project -> project.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output: " +
                pro.getName() + ":" + JavaFXMain.selectedProjectId);
        AnchorPane pane = ((AnchorPane) rootPane.lookup("#outputScrollView"));
        pane.getChildren().clear();
        if (pro.getSequence() != null) {

            // The part of the method that draws the ganttTab.
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

            pane.getChildren().add(new Text(10, 10, "Estimated Time: " + pro.getDuration()));
            assignmentTab.drawEmployees();
        }

        if (pro.getPossibleCompletions() != null) populateChart();

    }

    /**
     * This method fills the chart with the data from the monte carlo.
     */
    public void populateChart() {
        //Get the current selected project.
        Project project = LocalObjStorage.getProjectList().stream()
                .filter(pro -> pro.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert project != null;
        //Make sure the chart is clear.
        barChart.getData().clear();

        //Set up a series of Strings and numbers.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        //Set the series name.
        series.setName("Probabilities");
        //Disable animations to stop it from looking weird. 
        barChart.setAnimated(false);
        // Get the possible completions from the project. 
        List<Double> possibleCompletions = project.getPossibleCompletions();
        //The total sum of all the values in the list.
        double total = possibleCompletions.stream().mapToDouble(value -> value).sum();
        double sum = 0;
        for (int i = 0; i < possibleCompletions.size(); i++) {
            //Convert to percent.
            double percent = sum / total * 100;
            sum += possibleCompletions.get(i);
            //If percent is less than 1 or is greater than 99 don't insert "bars"
            if (percent > 1 && percent < 99)
                series.getData().add(new XYChart.Data<>(i + "", percent));
        }
        //Add the series to the chart.
        barChart.getData().add(series);

        // Sets the x axis label to "Working hours".
        series.getChart().getXAxis().setLabel("Duration");
        // Sets the y axis label to "chance of completion"
        series.getChart().getYAxis().setLabel("Chance of completion");
        //Create a tooltip for every dataset in the chart.
        for (XYChart.Data<String, Number> entry : series.getData()) {
            Tooltip t = new Tooltip("Duration: " + entry.getXValue() + "\nChance: " + entry.getYValue().intValue() + "%");
            t.setShowDelay(Duration.millis(15));
            Tooltip.install(entry.getNode(), t);
        }
    }
}
