package com.usp.icmc.libraryControl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("../view/startScene.fxml")
        );
        Parent root = loader.load();
        primaryStage.setTitle("Library Controller");
        primaryStage.setScene(new Scene(root));
        primaryStage.setHeight(270);
        primaryStage.setWidth(500);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
