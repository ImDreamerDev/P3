package dk.aau.ds304e18;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.ui.InputTab;
import dk.aau.ds304e18.ui.OutputTab;
import dk.aau.ds304e18.ui.ProjectTab;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;


public class JavaFXMain extends Application {
    private Parent rootPane = null;
    private TextField usernameField;
    private PasswordField passwordField;
    private VBox vBoxLogin;
    public static int selectedProjectId;
    private Image image;
    private OutputTab outputTab;
    private InputTab inputTab;
    private ProjectTab projectTab;

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

        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0.5, true, Side.TOP, 0.1,
                        true), new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO,
                false, false, false, false));

        ((Pane) rootPane.lookup("#loginPane")).setBackground(new Background(backgroundImage));
        rootPane.lookup("#loginPane").getParent().setVisible(true);
        vBoxLogin = ((VBox) rootPane.lookup("#loginPane"));
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

        //Update button
        ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(1).setOnMouseClicked(event -> {
            Task<Void> voidTask = DatabaseManager.distributeModels(LocalObjStorage.getProjectManager().get(0));
            ProgressBar bar = new ProgressBar();
            bar.progressProperty().bind(voidTask.progressProperty());
            ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().add(bar);
            voidTask.setOnSucceeded(observable -> {
                ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().remove(bar);
                outputTab = new OutputTab(rootPane);
                inputTab = new InputTab(rootPane, outputTab);
                projectTab = new ProjectTab(rootPane, LocalObjStorage.getProjectManager().get(0), inputTab);
                rootPane.getChildrenUnmodifiable().get(2).setVisible(false);
            });
            voidTask.setOnFailed(observable -> bar.setStyle("-fx-progress-color: red"));
            new Thread(voidTask).start();
        });

        ((HBox) rootPane.getChildrenUnmodifiable().get(0)).getChildren().get(3).setOnMouseClicked(event -> {
            DatabaseManager.logOut();
            rootPane.getChildrenUnmodifiable().get(2).setVisible(true);
            usernameField.clear();
            passwordField.clear();
            TabPane tabPane = (TabPane) rootPane.getChildrenUnmodifiable().get(1);
            tabPane.getSelectionModel().select(0);
            outputTab = null;
            inputTab = null;
            projectTab = null;
            selectedProjectId = 0;
            tabPane.getTabs().get(1).setDisable(true);
            tabPane.getTabs().get(2).setDisable(true);
        });

        loginButton.setOnMouseClicked(event -> logIn());
        Scene scene = new Scene(rootPane, 1280, 720);
        stage.setTitle("Planexus");

        stage.setScene(scene);
        stage.show();
        usernameField.requestFocus();
    }


    public static void main(String[] args) {
        launch();
    }

    private void logIn() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        System.out.println(password + " " + username);
        AtomicReference<ProjectManager> pm = new AtomicReference<>(DatabaseManager.logIn(username, password));

        if (pm.get() == null) {
            Label error = ((Label) vBoxLogin.getChildren().get(2));
            error.setText("Error: Username and Password does not match");
            error.setVisible(true);
        } else {

            Task<Void> voidTask = DatabaseManager.distributeModels(pm.get());
            ProgressBar bar = new ProgressBar();
            bar.progressProperty().bind(voidTask.progressProperty());
            ((StackPane) rootPane.getChildrenUnmodifiable().get(2)).getChildren().add(bar);
            voidTask.setOnSucceeded(observable -> {
                ((StackPane) rootPane.getChildrenUnmodifiable().get(2)).getChildren().remove(bar);
                pm.set(LocalObjStorage.getProjectManagerById(pm.get().getId()));
                OutputTab outputTab = new OutputTab(rootPane);
                InputTab inputTab = new InputTab(rootPane, outputTab);
                ProjectTab projectTab = new ProjectTab(rootPane, pm.get(), inputTab);
                rootPane.getChildrenUnmodifiable().get(2).setVisible(false);
            });
            voidTask.setOnFailed(observable -> bar.setStyle("-fx-progress-color: red"));
            new Thread(voidTask).start();
        }
    }
}