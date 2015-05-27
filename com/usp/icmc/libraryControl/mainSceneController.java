package com.usp.icmc.libraryControl;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class mainSceneController implements Initializable {

    @FXML
    private ComboBox<String> userSelector;
    @FXML
    private ComboBox<String> bookSelector;
    @FXML
    private TextField searchBox;

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

    @FXML
    private void addNewUser(){
        String name = searchBox.getText();
        User user = new User(name, User.PROFESSOR);
        library.addUser(user);
        System.out.println(library.getUser(user.getId()).getName());
        library.storeToDataDirectory(library.getDataDirectory());
    }

    @FXML
    private void removeUser(){

    }

    @FXML
    private void borrowToUser(){

    }

    @FXML
    private void addNewBook(){

    }

    @FXML
    private void removeBook(){

    }

    @FXML
    private void returnBook(){

    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}
