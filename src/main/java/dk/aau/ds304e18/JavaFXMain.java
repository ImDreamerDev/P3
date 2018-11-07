package dk.aau.ds304e18;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.ui.InputTab;
import dk.aau.ds304e18.ui.OutputTab;
import dk.aau.ds304e18.ui.ProjectTab;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URISyntaxException;


public class JavaFXMain extends Application {
    private Parent rootPane = null;
    private TextField usernameField;
    private PasswordField passwordField;
    private VBox vBoxLogin;
    public static int selectedProjectId;
    private Image image;

    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        try {
            rootPane = loader.load();
            image = new Image(getClass().getResource("/bg.png").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }

        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0.5, true, Side.TOP, 0.1, true), new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, false, false));
        // Background color
        //  rootPane.lookup("#loginPane").getParent().setStyle("-fx-background-color: black");
        ((Pane) rootPane.lookup("#loginPane")).setBackground(new Background(backgroundImage));
        rootPane.lookup("#loginPane").setVisible(true);
        vBoxLogin = ((VBox) rootPane.lookup("#loginPane"));
        Button loginButton = ((Button) vBoxLogin.getChildrenUnmodifiable().get(3));
        usernameField = (TextField) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(0)).getChildren().get(1);
        //Username label color
        //  ((Label) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(0)).getChildren().get(0)).setTextFill(Color.WHITE);
        usernameField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                logIn();
            }
        });

        passwordField = (PasswordField) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(1)).getChildren().get(1);
        // Password label color
        //  ((Label) ((HBox) vBoxLogin.getChildrenUnmodifiable().get(1)).getChildren().get(0)).setTextFill(Color.WHITE);
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