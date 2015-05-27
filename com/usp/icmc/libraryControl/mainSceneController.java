package com.usp.icmc.libraryControl;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class mainSceneController implements Initializable {

    @FXML
    private ComboBox<String> userSelector;
    @FXML
    private ComboBox<String> bookSelector;

    private Library library;

    @Override
    public void initialize(
        URL location, ResourceBundle resources
    ) {
        userSelector.setOnKeyTyped(
            new AutoCompleteComboBoxListener<String>(userSelector)
        );
        bookSelector.setOnKeyTyped(
            new AutoCompleteComboBoxListener<String>(bookSelector)
        );

    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}
