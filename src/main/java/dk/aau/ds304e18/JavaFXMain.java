package dk.aau.ds304e18;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.gui.Login;
import dk.aau.ds304e18.gui.ProjectTab;
import dk.aau.ds304e18.gui.input.InputTab;
import dk.aau.ds304e18.gui.output.OutputTab;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The entry point of the program.
 */
public class JavaFXMain extends Application {
    /**
     * The root pane of the GUI.
     */
    private Parent rootPane;

    /**
     * The currently selected project id.
     */
    public static int selectedProjectId;

    /**
     * The login in screen background.
     */
    private Image image;

    /**
     * The components of the GUI.
     */
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
        //Set up a fxm loader to load the main fxml.
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        try {
            //Load the main.fxml file in the resources folder.
            rootPane = loader.load();
            //Load the background from the resources folder.
            image = new Image(getClass().getResource("/bg.png").toExternalForm());
            //Set and load the icon for the program.
            stage.getIcons().add(new Image(getClass().getResource("/icon.png").toExternalForm()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Set up the login component.
        loginScreen = new Login(image, rootPane);

        //Tooltips for the main pages.
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(0).setTooltip(new Tooltip("The page with all the projects"));
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(1).setTooltip(new Tooltip("The page to input tasks and employees to the selected project"));

        //Get the outer tab from the root pane.
        Tab inputTab1 = ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(1);
        //Get the inner input tab pane from the outer input tab pane.
        TabPane inputTabPane = ((TabPane) ((AnchorPane) inputTab1.getContent()).getChildren().get(0));
        //The input pane tabs tooltips.
        inputTabPane.getTabs().get(0).setTooltip(new Tooltip("The page with the tasks in the selected project"));
        inputTabPane.getTabs().get(1).setTooltip(new Tooltip("The page with the employees assigned to the selected project and the available employees"));

        //Get the outer output tab from the root pane.
        Tab outputTab = ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2);
        //Get the inner output tab pane from the outer output tab pane.
        TabPane outputTabPane = (TabPane) ((HBox) ((AnchorPane) outputTab.getContent()).getChildren().get(0)).getChildren().get(1);
        //The output pane tabs tooltips.
        outputTabPane.getTabs().get(0).setTooltip(new Tooltip("The page with the probabilities for the project"));
        outputTabPane.getTabs().get(1).setTooltip(new Tooltip("The page with some Gantt?"));
        outputTabPane.getTabs().get(2).setTooltip(new Tooltip("The page to assign employees to the task on the selected project"));

        //Get the update button from the GUI.
        Button updateButton = (Button) ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(1);
        //Set the tooltip of the update button.
        updateButton.setTooltip(new Tooltip("Updates the program"));
        //Makes the update button update the local stored objects.
        updateButton.setOnMouseClicked(event -> update());

        //Get the log out button from the GUI.
        Button logoutButton = (Button) ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(3);
        //Set the tooltip of the log out button.
        logoutButton.setTooltip(new Tooltip("Logs out of the program"));
        //Makes the logout button log the user out.
        logoutButton.setOnMouseClicked(event -> {
            loginScreen.logOut();
            JavaFXMain.outputTab = null;
            inputTab = null;
            projectTab = null;
            selectedProjectId = 0;
        });
        //Create the JavaFX scene.
        Scene scene = new Scene(rootPane, 1280, 720);
        //Set the program title.
        stage.setTitle("Planexus");
        //Set the scene.
        stage.setScene(scene);
        //Show the program.
        stage.show();
        //Focus the username field.
        loginScreen.focusUsername();
    }

    private void update() {
        //Set the selected id to 0.
        JavaFXMain.selectedProjectId = 0;
        //Make a new task
        Task<Void> voidTask = DatabaseManager.distributeModels(LocalObjStorage.getProjectManager().get(0));
        //Create and add a progressbar showing the progress of the task.
        ProgressBar bar = new ProgressBar();
        bar.progressProperty().bind(voidTask.progressProperty());
        ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().add(bar);
        //When the task succeeds
        voidTask.setOnSucceeded(observable -> {
            //Remove the progress bar.
            ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().remove(bar);
            //Create a new set of tabs.
            JavaFXMain.outputTab = new OutputTab(rootPane);
            inputTab = new InputTab(rootPane);
            projectTab = new ProjectTab(rootPane, LocalObjStorage.getProjectManager().get(0));

            //Select the first element.
            ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getSelectionModel().select(0);
            //Disable the other tabs.
            ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(1).setDisable(true);
            ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setDisable(true);

        });
        //If the task fails set the progress bar red.
        voidTask.setOnFailed(observable -> bar.setStyle("-fx-progress-color: red"));
        
        //Start the task
        new Thread(voidTask).start();
    }


    public static void main(String[] args) {
        launch();
    }
}