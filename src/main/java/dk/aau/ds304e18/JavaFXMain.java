package dk.aau.ds304e18;

import dk.aau.ds304e18.database.DatabaseDistributor;
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
import javafx.util.Duration;

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
     * The duration it takes for a tooltip to show.
     */
    private static Duration tooltipShowDelay = Duration.ZERO;

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
        Tooltip projectTabTooltip = new Tooltip("The page with all the projects");
        projectTabTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(0).setTooltip(projectTabTooltip);

        Tooltip inputTabTooltip = new Tooltip("The page to input tasks and employees to the selected project");
        inputTabTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(1).setTooltip(inputTabTooltip);

        Tooltip outputTabTooltip = new Tooltip("The page with the output of the calculation");
        outputTabTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2).setTooltip(outputTabTooltip);

        //Get the outer tab from the root pane.
        Tab inputTab1 = ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(1);
        //Get the inner input tab pane from the outer input tab pane.
        TabPane inputTabPane = ((TabPane) ((AnchorPane) inputTab1.getContent()).getChildren().get(0));

        //The input pane tabs tooltips.
        Tooltip taskTabTooltip = new Tooltip("The page with the tasks in the selected project");
        taskTabTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        inputTabPane.getTabs().get(0).setTooltip(taskTabTooltip);

        Tooltip employeeTabTooltip = new Tooltip("The page with the employees assigned to the selected project and the available employees");
        employeeTabTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        inputTabPane.getTabs().get(1).setTooltip(employeeTabTooltip);

        //Get the outer output tab from the root pane.
        Tab outputTab = ((TabPane) rootPane.getChildrenUnmodifiable().get(1)).getTabs().get(2);
        //Get the inner output tab pane from the outer output tab pane.
        TabPane outputTabPane = (TabPane) ((HBox) ((AnchorPane) outputTab.getContent()).getChildren().get(0)).getChildren().get(1);
        //The output pane tabs tooltips.
        Tooltip probabilityTabTooltip = new Tooltip("The page with the probabilities for the project");
        probabilityTabTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        outputTabPane.getTabs().get(0).setTooltip(probabilityTabTooltip);

        Tooltip ganttTabTooltip = new Tooltip("The page with visual depiction of the projects tasks?");
        ganttTabTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        outputTabPane.getTabs().get(1).setTooltip(ganttTabTooltip);

        Tooltip assignEmpToProjectTabTooltip = new Tooltip("The page to assign employees to the task on the selected project");
        assignEmpToProjectTabTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        outputTabPane.getTabs().get(2).setTooltip(assignEmpToProjectTabTooltip);

        //Get the update button from the GUI.
        Button updateButton = (Button) ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(0);
        //Set the tooltip of the update button.
        Tooltip updateButtonTooltip = new Tooltip("Updates the program");
        updateButtonTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        updateButton.setTooltip(updateButtonTooltip);
        //Makes the update button update the local stored objects.
        updateButton.setOnMouseClicked(event -> update());

        //Get the log out button from the GUI.
        Button logoutButton = (Button) ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(2);
        //Set the tooltip of the log out button.
        Tooltip logOutButtonTooltip = new Tooltip("Logs out of the program");
        logOutButtonTooltip.setShowDelay(JavaFXMain.getTooltipShowDelay());
        logoutButton.setTooltip(logOutButtonTooltip);
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
        Task<Void> voidTask = DatabaseDistributor.distributeModels(LocalObjStorage.getProjectManagerList().get(0));
        //Create and add a progressbar showing the progress of the task.
        ProgressBar bar = new ProgressBar();
        bar.progressProperty().bind(voidTask.progressProperty());
        ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().add(bar);
        //When the task succeeds
        voidTask.setOnSucceeded(observable -> {
            //Remove the progress bar.
            ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().remove(bar);
            //Create a new set of tabs.
            outputTab = new OutputTab(rootPane);
            inputTab = new InputTab(rootPane);
            projectTab = new ProjectTab(rootPane, LocalObjStorage.getProjectManagerList().get(0));

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

    /**
     * Getter for tooltipShowDelay
     * @return The duration it takes for a tooltip to show
     */
    public static Duration getTooltipShowDelay() {
        return tooltipShowDelay;
    }

    public static void main(String[] args) {
        launch();
    }
}