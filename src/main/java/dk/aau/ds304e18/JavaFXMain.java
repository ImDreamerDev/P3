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
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setTooltip(new Tooltip("The page with the result of the selected project"));


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
                outputTab = new OutputTab(rootPane);
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
            outputTab = null;
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