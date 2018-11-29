package dk.aau.ds304e18.gui.input;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class StartUpTest extends Application {
    private static Parent rootPane;

    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        try {
            //Load the main.fxml file in the resources folder.
            rootPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.close();
    }
    
    static Parent getRootPane() {
        return rootPane;
    }
}
