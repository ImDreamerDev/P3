package dk.aau.ds304e18;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.MonteCarlo;
import dk.aau.ds304e18.sequence.ParseSequence;
import dk.aau.ds304e18.sequence.Sequence;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class JavaFXMain extends Application {
    private Parent content = null;
    private int selectedProjectId;
    private ProjectManager projectManager;

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) {

        projectManager = DatabaseManager.getPM(1);
        DatabaseManager.distributeModels();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));

        try {
            content = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        var d = ((TableView) ((AnchorPane) ((TabPane) content.getChildrenUnmodifiable().get(1)).getTabs().get(0).getContent()).getChildren().get(2));
        ((TableColumn) d.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<Project, String>("id"));
        ((TableColumn) d.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<Project, String>("name"));
        ((TableColumn) d.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<Project, String>("name"));
        ((TableColumn) d.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<Project, String>("sequence"));
        ((TableColumn) d.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<Project, String>("duration"));
        FilteredList<Project> flProjects = ((FilteredList<Project>) new FilteredList(FXCollections.observableArrayList(LocalObjStorage.getProjectList())));
        SortedList<Project> sortedList = new SortedList<>(flProjects);
        sortedList.comparatorProperty().bind(d.comparatorProperty());

        d.setItems(FXCollections.observableArrayList(sortedList));

        d.setOnMouseClicked(event -> {
            if (d.getSelectionModel().getSelectedIndex() != -1 && selectedProjectId !=
                    ((int) ((TableColumn) d.getColumns().get(0)).getCellObservableValue(d.getSelectionModel().getSelectedIndex()).getValue())) {
                selectedProjectId = ((int) ((TableColumn) d.getColumns().get(0)).getCellObservableValue(d.getSelectionModel().getSelectedIndex()).getValue());
                setUpView();
            }
        });

        TextField textField = ((TextField) ((HBox) d.getParent().getChildrenUnmodifiable().get(3)).getChildren().get(1));
        Button archiveButton = ((Button) d.getParent().getChildrenUnmodifiable().get(1));
        archiveButton.setOnMouseClicked(event -> {
            if (selectedProjectId != 0) {
                projectManager.addOldProject(LocalObjStorage.getProjectById(selectedProjectId));
                d.getItems().remove(d.getSelectionModel().getSelectedIndex());
            }
        });

        textField.setPromptText("Search here!");
        textField.setOnKeyReleased(keyEvent -> {
            if (isFirstLetter(textField.getText()))
                flProjects.setPredicate(p -> Integer.toString(p.getId()).contains(textField.getText().toLowerCase().trim()));
            else
                flProjects.setPredicate(p -> p.getName().toLowerCase().contains(textField.getText().toLowerCase().trim()));
        });


        Scene scene = new Scene(content, 1280, 720);

        stage.setScene(scene);
        stage.show();
    }

    private void setUpView() {
        Project pro = LocalObjStorage.getProjectList().stream().filter(project -> project.getId() == selectedProjectId).findFirst().orElse(null);
        assert pro != null;
        if (pro.getSequence().equals(""))
            Sequence.sequenceTasks(pro);
        if (pro.getDuration() == 0)
            MonteCarlo.estimateTime(pro);

        ((TabPane) content.getChildrenUnmodifiable().get(1)).getTabs().get(1).setText("Output: " +
                pro.getName() + ":" + selectedProjectId);
        var pane = ((AnchorPane) ((ScrollPane) ((TabPane) content.getChildrenUnmodifiable().get(1)).getTabs().get(1).getContent())
                .getContent());
        pane.getChildren().clear();

        drawTasks(pro, pane);

        pane.getChildren().add(new Text(10, 10, "Time: " + pro.getDuration()));
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

    public static void main(String[] args) {
        launch();
    }

    private boolean isFirstLetter(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}