package dk.aau.ds304e18.ui.output;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GanttTab {
    public double zoomFactor = 1;

    public GanttTab(Parent rootPane) {
        Project pro = LocalObjStorage.getProjectList().stream().
                filter(project -> project.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output: " +
                pro.getName() + ":" + JavaFXMain.selectedProjectId);
        AnchorPane pane = ((AnchorPane) rootPane.lookup("#outputScrollView"));
        Label zoomFactorLabel = (Label) rootPane.lookup("#zoomLevelLabel");
        pane.setOnScroll(scrollEvent -> {
            if (scrollEvent.isControlDown()) {
                zoomFactor += scrollEvent.getDeltaY() * 0.05;
                if (zoomFactor < 0.5) zoomFactor = 0.5;
                else if (zoomFactor > 4) zoomFactor = 4;

                zoomFactorLabel.setText("Zoom level: " + (int) (zoomFactor * 100d) + "%");
                pane.getChildren().clear();
                drawTasks(pro, pane);
            }

        });
        rootPane.lookup("#zoomInButton").setOnMouseClicked(mouseEvent -> zoomIn(pro, pane, zoomFactorLabel));
        rootPane.lookup("#zoomOutButton").setOnMouseClicked(mouseEvent -> zoomOut(pro, pane, zoomFactorLabel));

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
    }

    private void zoomOut(Project pro, AnchorPane pane, Label zoomFactorLabel) {
        if (zoomFactor > 0.5) {
            zoomFactor -= 0.1;
            if (zoomFactor < 0.5) zoomFactor = 0.5;
            zoomFactorLabel.setText("Zoom level: " + (int) (zoomFactor * 100d) + "%");
            pane.getChildren().clear();
            drawTasks(pro, pane);
        }
    }

    private void zoomIn(Project pro, AnchorPane pane, Label zoomFactorLabel) {
        if (zoomFactor <= 4d) {
            zoomFactor += 0.1;
            if (zoomFactor > 4) zoomFactor = 4;
            zoomFactorLabel.setText("Zoom level: " + (int) (zoomFactor * 100d) + "%");
            pane.getChildren().clear();
            drawTasks(pro, pane);
        }
    }

    public void drawTasks(Project pro, AnchorPane pane) {
        List<AnchorPane> anchorPanes = new ArrayList<>();
        List<List<Task>> taskListOfTasks = ParseSequence.parseToMultipleLists(pro);
        List<Shape> shapeList = new ArrayList<>();
        HashMap<Task, AnchorPane> taskToAP = new HashMap<>();
        int paddingY = 55, xPadding = 50;
        List<Task> sortedTask = pro.getTasks();
        sortedTask.sort((o1, o2) -> (int) (o1.getStartTime() - o2.getStartTime()));

        int y = 0;

        for (Task task : sortedTask) {
            AnchorPane taskBox = new AnchorPane();
            AtomicReference<Double> xVar = new AtomicReference<>((double) 0);

            xVar.set(task.getStartTime());


            taskBox.setLayoutX(xVar.get() * zoomFactor + xPadding);

            taskBox.setLayoutY(35 * y + paddingY);
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

        double maxX = 0;
        for (
                AnchorPane ap : anchorPanes) {
            double thisXMax = ap.getLayoutX() + ((Rectangle) ap.getChildrenUnmodifiable().get(0)).getWidth();
            if (thisXMax > maxX) maxX = thisXMax;
        }

        maxX /= zoomFactor;

        int pixelsBetweenText = 20;
        if (zoomFactor <= 0.7) pixelsBetweenText = 40;
        else if (zoomFactor < 1) pixelsBetweenText = 30;
        else if (zoomFactor > 1.3 && zoomFactor <= 1.6) pixelsBetweenText = 15;
        else if (zoomFactor > 1.6) pixelsBetweenText = 10;

        for (
                int i = 0;
                i < maxX; i = i + pixelsBetweenText) {

            Text text = new Text((i * zoomFactor) + xPadding, 25, "" + i);
            text.setRotate(90);

            pane.getChildren().add(text);
            pane.getChildren().add(new Line((i * zoomFactor) + xPadding, 15, (i * zoomFactor) + xPadding, 20));
        }

        HashMap<Task, AtomicInteger> numberOfTasksDependingOnTask = new HashMap<>();
        HashMap<Task, AtomicInteger> numberOfTimesLineHasBeenDrawnForTask = new HashMap<>();

        taskListOfTasks.forEach(lTask -> lTask.forEach(task ->

        {
            numberOfTasksDependingOnTask.put(task, new AtomicInteger(0));
            numberOfTimesLineHasBeenDrawnForTask.put(task, new AtomicInteger(0));
        }));
        taskListOfTasks.forEach(lTask -> lTask.forEach(task ->

        {
            task.getDependencies().forEach(dep -> numberOfTasksDependingOnTask.get(dep).incrementAndGet());
        }));


        for (
                Task task : taskToAP.keySet()) {
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

                    // part orthogonal to main line
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
        pane.getChildren().

                addAll(anchorPanes);
        pane.getChildren().

                addAll(shapeList);
    }
}
