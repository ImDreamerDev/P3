package dk.aau.ds304e18.gui.output;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
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

class GanttTab {
    private double zoomFactor = 1;
    private final Project project;
    private AnchorPane scrollViewPane;
    private Label zoomFactorLabel;
    private Parent rootPane;

    public GanttTab(Parent rootPane) {
        //Sets the current project.
        project = LocalObjStorage.getProjectList().stream().
                filter(pro -> pro.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert project != null;
        //Sets the title of the output tab to include the name of the project and id.
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output: " +
                project.getName() + ":" + JavaFXMain.selectedProjectId);

        //Get the anchor scrollViewPane used from the GUI.
        this.scrollViewPane = ((AnchorPane) rootPane.lookup("#outputScrollView"));

        //Get the zoom level label to display the current zoom level. 
        zoomFactorLabel = (Label) rootPane.lookup("#zoomLevelLabel");
        this.rootPane = rootPane;
        //The event for zooming when scrolling while holding ctrl down.
        this.scrollViewPane.setOnScroll(scrollEvent -> {
            if (scrollEvent.isControlDown()) {
                zoomFactor += scrollEvent.getDeltaY() * 0.05;
                if (zoomFactor < 0.5) zoomFactor = 0.5;
                else if (zoomFactor > 4) zoomFactor = 4;

                zoomFactorLabel.setText("Zoom level: " + (int) (zoomFactor * 100d) + "%");
                this.scrollViewPane.getChildren().clear();
                drawTasks();
            }

        });

        //Zooms in when zoom in button is pressed
        rootPane.lookup("#zoomInButton").setOnMouseClicked(mouseEvent -> zoomIn());

        //Zooms out when the zoom out button is pressed
        rootPane.lookup("#zoomOutButton").setOnMouseClicked(mouseEvent -> zoomOut());

        //Resets the zoom to 1 when the 1:1 button is pressed.
        rootPane.lookup("#resetZoomButton").setOnMouseClicked(mouseEvent -> {
            zoomFactor = 1;
            zoomFactorLabel.setText("Zoom level: 100%");
            this.scrollViewPane.getChildren().clear();
            drawTasks();
        });

        //Zooms in or out if the button pressed is either - or + while control is down
        scrollViewPane.setOnKeyReleased(keyEvent -> {
            if (keyEvent.isControlDown()) {
                if (keyEvent.getText().equals("-")) {
                    zoomOut();
                } else if (keyEvent.getText().equals("+")) {
                    zoomIn();
                }
                keyEvent.consume();

            }
        });
    }

    /**
     * The function for zooming out.
     */
    private void zoomOut() {
        if (zoomFactor > 0.5) {
            zoomFactor -= 0.1;
            if (zoomFactor < 0.5) zoomFactor = 0.5;
            zoomFactorLabel.setText("Zoom level: " + (int) (zoomFactor * 100d) + "%");
            scrollViewPane.getChildren().clear();
            drawTasks();
        }
    }


    /**
     * The function for zooming in
     */
    private void zoomIn() {
        if (zoomFactor <= 4d) {
            zoomFactor += 0.1;
            if (zoomFactor > 4) zoomFactor = 4;
            zoomFactorLabel.setText("Zoom level: " + (int) (zoomFactor * 100d) + "%");
            scrollViewPane.getChildren().clear();
            drawTasks();
        }
    }


    /**
     * The function for drawing the tasks and arrows
     */
    public void drawTasks() {
        recommend();
        List<AnchorPane> anchorPanes = new ArrayList<>();
        List<List<Task>> taskListOfTasks = ParseSequence.parseToMultipleLists(project);
        List<Shape> shapeList = new ArrayList<>();
        HashMap<Task, AnchorPane> taskToAP = new HashMap<>();
        int paddingY = 55, xPadding = 50;
        List<Task> sortedTask = project.getTasks();
        sortedTask.sort((o1, o2) -> (int) (o1.getStartTime() - o2.getStartTime()));

        int y = 0;

        for (Task task : sortedTask) {
            //We don't want to draw anything if any of the tasks does not have a set time yet.
            if (task.getStartTime() < 0) return;


            AnchorPane taskBox = new AnchorPane();

            //Sets the AnchorPane's start x value. Upper left corner is start
            taskBox.setLayoutX(task.getStartTime() * zoomFactor + xPadding);

            //Sets the AnchorPane's start y
            taskBox.setLayoutY(35 * y + paddingY);

            //Makes a rectangle to represent the task with height 20 and length as the tasks estimated time
            Rectangle ret = new Rectangle(task.getEstimatedTime() * zoomFactor, 20);

            //Set stroke color and fill
            ret.setStroke(Color.BLACK);
            ret.setFill(Color.web("#ff9c00"));

            //Shows tasks name in tooltip
            Tooltip tooltip = new Tooltip(task.getName() + "\nStart: " + task.getStartTime() + "\nEnd: "
                    + (task.getStartTime() + task.getEstimatedTime()) + "\nDuration:" + task.getEstimatedTime());
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(ret, tooltip);

            //Adds the rectangle to the AnchorPane
            taskBox.getChildren().addAll(ret);
            //Adds AnchorPane to all AnchorPanes
            anchorPanes.add(taskBox);

            //Inserts into hashmap
            taskToAP.put(task, taskBox);

            y++;
        }

        double maxX = 0;

        //Keeps tract of the maximum x value for drawing scale on top of page.
        for (AnchorPane ap : anchorPanes) {
            double thisXMax = ap.getLayoutX() + ((Rectangle) ap.getChildrenUnmodifiable().get(0)).getWidth();
            if (thisXMax > maxX) maxX = thisXMax;
        }

        //For some reason this gets fucked up idk so i need to divide
        maxX /= zoomFactor;

        int distanceBetweenText = 20;
        //Determines distance between text being drawn
        if (zoomFactor <= 0.7) distanceBetweenText = 40;
        else if (zoomFactor < 1) distanceBetweenText = 30;
        else if (zoomFactor > 1.3 && zoomFactor <= 1.6) distanceBetweenText = 15;
        else if (zoomFactor > 1.6) distanceBetweenText = 10;

        //Adds duration text
        for (int i = 0; i < maxX; i = i + distanceBetweenText) {
            Text text = new Text((i * zoomFactor) + xPadding, 25, "" + i);
            text.setRotate(90);
            scrollViewPane.getChildren().add(text);
            //And line between
            scrollViewPane.getChildren().add(new Line((i * zoomFactor) + xPadding,
                    15, (i * zoomFactor) + xPadding, 20));
        }

        //Keeps track of how many tasks are dependant of the current task, not the amount of dependencies.
        HashMap<Task, AtomicInteger> numberOfTasksDependingOnTask = new HashMap<>();

        //Keeps track of how many lines have been drawn from this task to determine where to draw line from.
        HashMap<Task, AtomicInteger> numberOfTimesLineHasBeenDrawnForTask = new HashMap<>();

        //Maps this task to an integer representing the values described.
        taskListOfTasks.forEach(taskList -> taskList.forEach(task -> {
            numberOfTasksDependingOnTask.put(task, new AtomicInteger(0));
            numberOfTimesLineHasBeenDrawnForTask.put(task, new AtomicInteger(0));
        }));

        //Determines the amount of tasks depending on task.
        taskListOfTasks.forEach(taskList -> taskList.forEach(task -> {
            task.getDependencies().forEach(dep -> numberOfTasksDependingOnTask.get(dep).incrementAndGet());
        }));

        //Now to drawing arrows.....
        for (Task task : taskToAP.keySet()) {
            List<Task> dependencies = task.getDependencies();

            //Only draw arrows if this task has dependencies
            if (dependencies != null && dependencies.size() != 0) {
                AtomicInteger dependencyNumber = new AtomicInteger(0);

                dependencies.forEach(depTask -> {
                    int numberOfDepenceanciLineToDraw = numberOfTasksDependingOnTask.get(depTask).get()
                            - numberOfTimesLineHasBeenDrawnForTask.get(depTask).incrementAndGet();

                    //Start and end AnchorPane
                    AnchorPane startAncPane = taskToAP.get(depTask);
                    AnchorPane endAncPane = taskToAP.get(task);

                    double sx, sy, ex, ey;
                    //Sets start point
                    sx = startAncPane.getLayoutX() + ((Rectangle) startAncPane.getChildrenUnmodifiable().get(0)).getWidth();
                    sy = startAncPane.getLayoutY() + ((Rectangle) startAncPane.getChildrenUnmodifiable().get(0)).getHeight() / 2;

                    //Number of units between lines drawn
                    int pixelsBetweenLines = 3;
                    int pixelsBetweenVerticalLines = 4;

                    //To kinda ensure EVERYTHING isn't drawn on top of each other
                    //We first draw above middle of task, then below middle
                    //So if we have above 8 tasks dependant on the task it start to draw below task lel.
                    if (sy - (numberOfDepenceanciLineToDraw * pixelsBetweenLines) <
                            (sy - ((Rectangle) startAncPane.getChildrenUnmodifiable().get(0)).getHeight() / 2))
                        sy = startAncPane.getLayoutY() + (numberOfDepenceanciLineToDraw * pixelsBetweenLines);
                    else
                        sy -= (numberOfDepenceanciLineToDraw * pixelsBetweenLines);

                    //Sets endpoint
                    ex = endAncPane.getLayoutX();
                    ey = endAncPane.getLayoutY() + ((Rectangle) endAncPane.getChildrenUnmodifiable().get(0)).getHeight();

                    int pixelsRightOfPaneStartToPoint = 5;

                    //Checks if there is place for the arrowhead.
                    if (((Rectangle) endAncPane.getChildrenUnmodifiable().get(0)).getWidth() / 2 >
                            pixelsRightOfPaneStartToPoint + (pixelsBetweenVerticalLines * dependencyNumber.get()))
                        ex += pixelsRightOfPaneStartToPoint + (pixelsBetweenVerticalLines * dependencyNumber.get());

                    if (sy < ey) ey -= ((Rectangle) endAncPane.getChildrenUnmodifiable().get(0)).getHeight();

                    //Makes the lines and sets start and end
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


                    double triangleP1X, triangleP1Y, triangleP2X, triangleP2Y;

                    //Determines if arrow should point up or down.
                    if (ey > sy) {
                        //Down
                        triangleP1X = ex - 4;
                        triangleP1Y = ey - 4;
                        triangleP2X = ex + 4;
                        triangleP2Y = ey - 4;
                    } else {
                        //Up
                        triangleP1X = ex - 4;
                        triangleP1Y = ey + 4;
                        triangleP2X = ex + 4;
                        triangleP2Y = ey + 4;
                    }

                    //Drawn arrowhead polygon.
                    Polygon triangle = new Polygon(ex, ey, triangleP1X, triangleP1Y, triangleP2X, triangleP2Y);

                    //Adds to lists
                    shapeList.add(triangle);
                    dependencyNumber.incrementAndGet();
                });

            }
        }

        //Adds AnchorPanes and shapes to the scrollViewPane.
        scrollViewPane.getChildren().addAll(anchorPanes);
        scrollViewPane.getChildren().addAll(shapeList);
    }

    private void recommend() {
        VBox optGroup0 = (VBox) rootPane.lookup("#OptGroup0");
        ((Label) optGroup0.getChildren().get(0)).setText(Math.round(project.getNumberOfEmployees()) + "");
        ((Label) optGroup0.getChildren().get(1)).setText(Math.round((project.getDuration())) + "");

        VBox optGroup1 = (VBox) rootPane.lookup("#OptGroup1");
        VBox optGroup2 = (VBox) rootPane.lookup("#OptGroup2");
        if (project.getRecommendedEmployees() == null || project.getRecommendedEmployees().getAmountEmployees().size() == 0) {
            clearText(optGroup1);
            clearText(optGroup2);
            return;
        }
        ((Label) optGroup1.getChildren().get(0)).setText(project.getRecommendedEmployees().getAmountEmployees().get(0) + " Work groups");
        ((Label) optGroup1.getChildren().get(1)).setText(Math.round(project.getRecommendedEmployees().getEstimatedTime().get(0)) + "");
        if (project.getRecommendedEmployees().getAmountEmployees().size() == 1) {
            clearText(optGroup2);
            return;
        }
        ((Label) optGroup2.getChildren().get(0)).setText(project.getRecommendedEmployees().getAmountEmployees().get(1) + " Work groups");
        ((Label) optGroup2.getChildren().get(1)).setText(Math.round(project.getRecommendedEmployees().getEstimatedTime().get(1)) + "");
    }

    private void clearText(VBox optGroup) {
        ((Label) optGroup.getChildren().get(0)).setText("");
        ((Label) optGroup.getChildren().get(1)).setText("");
    }
}
