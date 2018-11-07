package dk.aau.ds304e18.ui;
/*
 * Author: Lasse Stig Emil Rasmussen
 * Email: lser17@student.aau.dk
 * Class: Software 2nd semester
 */

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class OutputTab {
    private Parent rootPane;

    public OutputTab(Parent rootPane) {
        this.rootPane = rootPane;
    }

    void drawOutputTab(boolean useMonty) {
        Project pro = LocalObjStorage.getProjectList().stream().filter(project -> project.getId() == JavaFXMain.selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setText("Output: " +
                pro.getName() + ":" + JavaFXMain.selectedProjectId);
        var pane = ((AnchorPane) rootPane.lookup("#outputScrollView"));
        pane.getChildren().clear();
        if (pro.getSequence() != null) {
            drawTasks(pro, pane);
            if (useMonty) {
                ((ListView) ((AnchorPane) rootPane.lookup("#outputPane")).getChildren().get(0))
                        .setItems(FXCollections.observableArrayList(ParseSequence.parseToSingleList(pro, true)));
            } else if (pro.getRecommendedPath() != null && !pro.getRecommendedPath().equals("")) {
                ((ListView) ((AnchorPane) rootPane.lookup("#outputPane")).getChildren().get(0))
                        .setItems(FXCollections.observableArrayList(ParseSequence.parseToSingleList(pro, true)));
            } else
                ((ListView) ((AnchorPane) rootPane.lookup("#outputPane")).getChildren().get(0))
                        .getItems().clear();

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
}
