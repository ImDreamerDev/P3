package dk.aau.ds304e18;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.gui.Login;
import dk.aau.ds304e18.gui.input.InputTab;
import dk.aau.ds304e18.gui.output.OutputTab;
import dk.aau.ds304e18.gui.ProjectTab;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;


public class JavaFXMain extends Application {
    private Parent rootPane = null;
    public static int selectedProjectId;
    private Image image;

    private Login loginScreen;
    public static ProjectTab projectTab;
    public static InputTab inputTab;
    public static OutputTab outputTab;

    /**
     * The method that starts the program. The program starts on the loginScreen page.
     *
     * @param stage - the primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        try {
            rootPane = loader.load();
            image = new Image(getClass().getResource("/bg.png").toExternalForm());
            stage.getIcons().add(new Image(getClass().getResource("/icon.png").toExternalForm()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loginScreen = new Login(image, rootPane);

        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(0).setTooltip(new Tooltip("The page with all the projects"));
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(1).setTooltip(new Tooltip("The page to input tasks and employees to the selected project"));
      
        Tab inputTab1 = ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(1);
        Tab outputTab = ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2);

        TabPane inputTabPane = ((TabPane) ((AnchorPane) inputTab1.getContent()).getChildren().get(0));
        TabPane outputTabPane = (TabPane) ((HBox) ((AnchorPane) outputTab.getContent()).getChildren().get(0)).getChildren().get(1);

        inputTabPane.getTabs().get(0).setTooltip(new Tooltip("The page with the tasks in the selected project"));
        inputTabPane.getTabs().get(1).setTooltip(new Tooltip("The page with the employees assigned to the selected project and the available employees"));

        outputTabPane.getTabs().get(0).setTooltip(new Tooltip("The page with the probabilities for the project"));
        outputTabPane.getTabs().get(1).setTooltip(new Tooltip("The page with some Gantt?"));
        outputTabPane.getTabs().get(2).setTooltip(new Tooltip("The page to assign employees to the task on the selected project"));


 //       outputTab.setTooltip(new Tooltip("The page with the result of the selected project"));

  //      Node tasksTab =  rootPane.lookup("#TasksTab");
   //     System.out.println(tasksTab.getProperties());
   //     Tooltip.install(tasksTab,new Tooltip("The page with the tasks in the selected project"));

 //       Node employeeTap = rootPane.lookup("#EmployeesTab");
 //       Tooltip.install(employeeTap, new Tooltip("The page with the employees assigned to the selected project and the available employees"));

        //Update button
        ((Button) ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(1)).setTooltip(new Tooltip("Updates the program"));
        ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(1).setOnMouseClicked(event -> {
            JavaFXMain.selectedProjectId = 0;
            Task<Void> voidTask = DatabaseManager.distributeModels(LocalObjStorage.getProjectManager().get(0));
            ProgressBar bar = new ProgressBar();
            bar.progressProperty().bind(voidTask.progressProperty());
            ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().add(bar);
            voidTask.setOnSucceeded(observable -> {
                ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().remove(bar);
                JavaFXMain.outputTab = new OutputTab(rootPane);
                inputTab = new InputTab(rootPane);
                projectTab = new ProjectTab(rootPane, LocalObjStorage.getProjectManager().get(0));
                
                ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getSelectionModel().select(0);
                ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(1).setDisable(true);
                ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setDisable(true);

            });
            voidTask.setOnFailed(observable -> bar.setStyle("-fx-progress-color: red"));
            new Thread(voidTask).start();
        });

        ((Button) ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(3)).setTooltip(new Tooltip("Logs out of the program"));
        ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(3).setOnMouseClicked(event -> {
            loginScreen.logOut();
            JavaFXMain.outputTab = null;
            inputTab = null;
            projectTab = null;
            selectedProjectId = 0;
        });

        Scene scene = new Scene(rootPane, 1280, 720);
        stage.setTitle("Planexus");
        stage.setScene(scene);
        stage.show();
        loginScreen.focusUsername();
    }

    public static void main(String[] args) {
        launch();
    }
}