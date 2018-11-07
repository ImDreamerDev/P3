package dk.aau.ds304e18;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.ParseSequence;
import dk.aau.ds304e18.sequence.Sequence;
import dk.aau.ds304e18.ui.InputTab;
import dk.aau.ds304e18.ui.OutputTab;
import dk.aau.ds304e18.ui.ProjectTab;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class JavaFXMain extends Application {
    private Parent rootPane = null;
    private TextField usernameField;
    private PasswordField passwordField;
    private VBox vBoxLogin;
    public static int selectedProjectId;

    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        try {
            rootPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.lookup("#loginPane").setVisible(true);
        vBoxLogin = ((VBox) ((Pane) rootPane.getChildrenUnmodifiable().get(2)).getChildrenUnmodifiable().get(0));
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
        Scene scene = new Scene(rootPane, 1280, 720);
        stage.setTitle("Project planner 2k18");
        //stage.getIcons().add();
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

    private void logIn() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        System.out.println(password + " " + username);
        ProjectManager pm = DatabaseManager.logIn(username, password);
        if (pm == null) {
            Label error = ((Label) vBoxLogin.getChildren().get(2));
            error.setText("Error: No such user");
            error.setVisible(true);
        } else {
            rootPane.getChildrenUnmodifiable().get(2).setVisible(false);
            DatabaseManager.distributeModels();
            pm = LocalObjStorage.getProjectManagerById(pm.getId());
            OutputTab outputTab = new OutputTab(rootPane);
            InputTab inputTab = new InputTab(rootPane, outputTab);
            ProjectTab projectTab = new ProjectTab(rootPane, pm, inputTab);
        }
    }
}