package dk.aau.ds304e18;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.ProjectManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.Collectors;

public class CreateProjectManager extends Application {
    private Parent rootPane = null;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField passwordFieldRepeat;
    private Label errorLabel;
    private VBox vBoxLogin;
    ListView<String> listView;

    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("createProjectManager.fxml"));
        try {
            rootPane = loader.load();
            stage.getIcons().add(new Image(getClass().getResource("/icon.png").toExternalForm()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        vBoxLogin = ((VBox) rootPane.lookup("#loginPane"));

        usernameField = (TextField) vBoxLogin.getChildrenUnmodifiable().get(2);

        usernameField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                CreatePM();
            }
        });

        passwordField = (PasswordField) vBoxLogin.getChildrenUnmodifiable().get(4);
        passwordField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                CreatePM();
            }
        });

        passwordFieldRepeat = (PasswordField) vBoxLogin.getChildrenUnmodifiable().get(6);
        passwordFieldRepeat.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                CreatePM();
            }
        });

        errorLabel = ((Label) vBoxLogin.getChildren().get(7));


        Button loginButton = ((Button) vBoxLogin.getChildrenUnmodifiable().get(8));
        loginButton.setOnMouseClicked(event -> CreatePM());

        listView = ((ListView) ((VBox) rootPane.lookup("#currentUsers")).getChildren().get(1));
        listView.setItems(FXCollections.observableArrayList(DatabaseManager.getAllProjectManagers().stream().
                map(ProjectManager::getName).collect(Collectors.toList())));

        Scene scene = new Scene(rootPane, 1280, 720);
        stage.setTitle("Planexus");

        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args) {
        launch();
    }

    private void CreatePM() {
        if (usernameField.getText().equals("")) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Error: Username must not be empty");
            errorLabel.setVisible(true);
        } else if (passwordField.getText().equals("") || passwordFieldRepeat.getText().equals("")) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Error: Passwords must not be empty");
            errorLabel.setVisible(true);
        } else if (passwordField.getText().equals(usernameField.getText())) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Error: Password can't be the same as username!");
            errorLabel.setVisible(true);
        } else if (!passwordFieldRepeat.getText().equals(passwordField.getText())) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Error: Passwords must match");
            errorLabel.setVisible(true);
        } else if (listView.getItems().contains(usernameField.getText())) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Error: User exists!");
            errorLabel.setVisible(true);
        } else {
            new ProjectManager(usernameField.getText(), passwordField.getText());
            errorLabel.setTextFill(Color.BLACK);
            errorLabel.setText("Create the project manager: " + usernameField.getText());
            errorLabel.setVisible(true);
            listView.setItems(FXCollections.observableArrayList(DatabaseManager.getAllProjectManagers().stream().
                    map(ProjectManager::getName).collect(Collectors.toList())));
            usernameField.clear();
            passwordField.clear();
            passwordFieldRepeat.clear();

        }

    }

}
