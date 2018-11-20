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

    public Login(Image image, Parent rootPane) {
        this.rootPane = rootPane;
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0.5, true, Side.TOP, 0.1,
                        true), new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO,
                false, false, false, false));

        ((Pane) this.rootPane.lookup("#loginPane")).setBackground(new Background(backgroundImage));
        this.rootPane.lookup("#loginPane").getParent().setVisible(true);
        vBoxLogin = ((VBox) this.rootPane.lookup("#loginPane"));
        Button loginButton = ((Button) vBoxLogin.getChildrenUnmodifiable().get(3));
        usernameField = (TextField) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(0)).getChildren().get(1);

        usernameField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                logIn();
            }
        });

        passwordField = (PasswordField) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(1)).getChildren().get(1);

        passwordField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                logIn();
            }
        });
        loginButton.setOnMouseClicked(event -> logIn());

    }

    public void focusUsername() {
        usernameField.requestFocus();
    }

    /**
     * Try to log the user in.
     */
    private void logIn() {
        //Get the username and password from their fields.
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        //Try to login with the username and password.
        AtomicReference<ProjectManager> pm = new AtomicReference<>(DatabaseManager.logIn(username, password));

        //If it returns null no user exists or the password is wrong.
        if (pm.get() == null) {
            Label error = ((Label) vBoxLogin.getChildren().get(2));
            error.setText("Error: Username and Password does not match any user");
            error.setVisible(true);
        } else if (pm.get().getName().equals("Connection error")) {
            Label error = ((Label) vBoxLogin.getChildren().get(2));
            error.setText("Error: Couldn't connect to the database");
            error.setVisible(true);
        } else {
            Label error = ((Label) vBoxLogin.getChildren().get(2));
            error.setText("");
            error.setVisible(false);
            Task<Void> voidTask = DatabaseManager.distributeModels(pm.get());
            ProgressBar bar = new ProgressBar();
            bar.progressProperty().bind(voidTask.progressProperty());
            ((StackPane) rootPane.getChildrenUnmodifiable().get(2)).getChildren().add(bar);
            voidTask.setOnSucceeded(observable -> {
                ((StackPane) rootPane.getChildrenUnmodifiable().get(2)).getChildren().remove(bar);
                pm.set(LocalObjStorage.getProjectManagerById(pm.get().getId()));
                JavaFXMain.outputTab = new OutputTab(rootPane);
                JavaFXMain.inputTab = new InputTab(rootPane);
                JavaFXMain.projectTab = new ProjectTab(rootPane, pm.get());
                rootPane.getChildrenUnmodifiable().get(2).setVisible(false);
            });
            voidTask.setOnFailed(observable -> bar.setStyle("-fx-progress-color: red"));
            new Thread(voidTask).start();
        }
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
