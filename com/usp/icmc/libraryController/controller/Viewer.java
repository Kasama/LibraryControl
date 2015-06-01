package com.usp.icmc.libraryController.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;

public class Viewer extends Dialog<ButtonType> {

    @FXML
    private Label label;
    @FXML
    private TextArea text;

    public Viewer(String labelString, String text) {

        FXMLLoader loader = new FXMLLoader(
            getClass()
                .getResource("/com/usp/icmc/libraryController/view/viewer.fxml")
        );
        loader.setController(this);
        this.setResizable(true);
        this.getDialogPane().getStylesheets()
            .add("/com/usp/icmc/libraryController/view/style.css");
        ButtonType buttonOK = new ButtonType(
            "OK", ButtonBar.ButtonData.OK_DONE
        );
        Parent root;
        try {
            root = loader.load();
            this.getDialogPane().setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.getDialogPane().getButtonTypes().add(buttonOK);

        this.label.setText(labelString);
        this.text.setText(text);
    }

}
