package dk.aau.ds304e18.gui;

import dk.aau.ds304e18.JavaFXMain;
import dk.aau.ds304e18.database.LocalObjStorage;
import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.gui.input.InputTab;
import dk.aau.ds304e18.gui.output.OutputTab;
import javafx.concurrent.Task;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The login pane.
 */
public class Login {
    /**
     * The root pane of the GUI.
     */
    private final Parent rootPane;

    /**
     * The username field.
     */
    private final TextField usernameField;

    /**
     * The password field.
     */
    private final PasswordField passwordField;

    /**
     * The box containing the login components.
     */
    private final VBox vBoxLogin;

    /**
     * Creates and sets up the login
     *
     * @param image    The background for the login screen.
     * @param rootPane The root pane of the GUI.
     */
    public Login(Image image, Parent rootPane) {
        this.rootPane = rootPane;
        //Create the background.
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0.5, true, Side.TOP, 0.1,
                        true), new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO,
                false, false, false, false));

        //Sets the background of the login screen.
        ((Pane) rootPane.lookup("#loginPane")).setBackground(new Background(backgroundImage));

        //Set the login screen visible.
        rootPane.lookup("#loginPane").getParent().setVisible(true);

        //Sets the login pane.
        vBoxLogin = ((VBox) rootPane.lookup("#loginPane"));

        //Gets the login box from the login pane.
        Button loginButton = ((Button) vBoxLogin.getChildrenUnmodifiable().get(3));
        //Gets the username from the login pane.
        usernameField = (TextField) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(0)).getChildren().get(1);

        //If enter is pressed when username field is focused login.
        usernameField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                logIn();
            }
        });

        //Gets the password for the login pane.
        passwordField = (PasswordField) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(1)).getChildren().get(1);

        //If enter is pressed when password field is focused login.
        passwordField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                logIn();
            }
        });
        //Sets the login button to login when pressed.
        loginButton.setOnMouseClicked(event -> logIn());
    }

    /**
     * Focus the username.
     */
    public void focusUsername() {
        usernameField.requestFocus();
    }

    /**
     * Try to log the user in.
     */
    public Task<Void> logIn(String username, String password) {
        //Get the username and password from their fields.

        //Try to login with the username and password.
        AtomicReference<ProjectManager> pm = new AtomicReference<>(DatabaseManager.logIn(username, password));

        //If it returns null no user exists or the password is wrong.
        if (pm.get() == null) {
            Label error = ((Label) vBoxLogin.getChildren().get(2));
            error.setText("Error: Username and Password does not match any user");
            error.setVisible(true);
            //If the pms name = Connection error the program couldn't reach the database.    
        } else if (pm.get().getName().equals("Connection error")) {
            Label error = ((Label) vBoxLogin.getChildren().get(2));
            error.setText("Error: Couldn't connect to the database");
            error.setVisible(true);
            //Otherwise the login was successful.    
        } else {
            Label error = ((Label) vBoxLogin.getChildren().get(2));
            error.setText("");
            error.setVisible(false);
            Task<Void> voidTask = DatabaseManager.distributeModels(pm.get());
            //Create and bind a progressbar to show the process on downloading and setting up everything.
            ProgressBar bar = new ProgressBar();
            bar.progressProperty().bind(voidTask.progressProperty());
            ((StackPane) rootPane.getChildrenUnmodifiable().get(2)).getChildren().add(bar);

            //When the task succeeds
            voidTask.setOnSucceeded(observable -> {
                //Remove the progressbar.
                ((StackPane) rootPane.getChildrenUnmodifiable().get(2)).getChildren().remove(bar);
                //Update the project manager, with the new information from the database.
                pm.set(LocalObjStorage.getProjectManagerById(pm.get().getId()));
                //Create all the tabs.
                JavaFXMain.outputTab = new OutputTab(rootPane);
                JavaFXMain.inputTab = new InputTab(rootPane);
                JavaFXMain.projectTab = new ProjectTab(rootPane, pm.get());
                //Hide the login pane.
                rootPane.getChildrenUnmodifiable().get(2).setVisible(false);
            });
            //When the task fails set the progressbar red.
            voidTask.setOnFailed(observable -> bar.setStyle("-fx-progress-color: red"));
            //Start the task.
            return voidTask;
        }
        return null;
    }

    private void logIn() {
        new Thread(logIn(usernameField.getText(), passwordField.getText())).start();
    }

    /**
     * Logs the user out of the program and resets tabs.
     */
    public void logOut() {
        //Close the connection.
        DatabaseManager.logOut();

        //Set the login pane visible.
        rootPane.getChildrenUnmodifiable().get(2).setVisible(true);
        //Clear the input fields.
        usernameField.clear();
        passwordField.clear();

        //Get the main tab pane.
        TabPane tabPane = (TabPane) rootPane.getChildrenUnmodifiable().get(1);

        //Select the first tab.
        tabPane.getSelectionModel().select(0);

        //Disables the others tabs.
        tabPane.getTabs().get(1).setDisable(true);
        tabPane.getTabs().get(2).setDisable(true);
    }

}
