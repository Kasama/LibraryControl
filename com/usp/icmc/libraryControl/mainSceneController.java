package com.usp.icmc.libraryControl;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
    private void addNewUser() {
        Dialog<User> dialog = new Dialog<>();
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("newUserDialog.fxml")
        );
        Parent root;
        ButtonType buttonCancel = new ButtonType(
            "Cancel", ButtonBar.ButtonData.CANCEL_CLOSE
        );
        ButtonType buttonOK = new ButtonType(
            "Confirm", ButtonBar.ButtonData.OK_DONE
        );
        try {
            root = loader.load();
            newUserDialogController controller = loader.getController();
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().addAll(
                buttonCancel, buttonOK
            );
            dialog.setResultConverter(
                param ->
                    param.equals(buttonOK) ?
                    new User(
                        controller.getUserName(), controller.getOption()
                    )
                    : null
            );
            Optional<User> user;
            user = dialog.showAndWait();
            if (!user.isPresent())
                return;

            library.addUser(user.get());
            library.storeToDataDirectory(library.getDataDirectory());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeUser() {

    }

    @FXML
    private void borrowToUser() {

    }

    @FXML
    private void addNewBook() {
        Dialog<Book> dialog = new Dialog<>();
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("newBookDialog.fxml")
        );
        Parent root;
        ButtonType buttonCancel = new ButtonType(
            "Cancel", ButtonBar.ButtonData.CANCEL_CLOSE
        );
        ButtonType buttonOK = new ButtonType(
            "Confirm", ButtonBar.ButtonData.OK_DONE
        );
        try {
            root = loader.load();
            newBookDialogController controller = loader.getController();
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().addAll(
                buttonCancel, buttonOK
            );
            dialog.setResultConverter(
                param ->
                    param.equals(buttonOK) ?
                    new Book(
                        controller.getTitle(), controller.getAuthor(), controller.getOption()
                    )
                   : null
            );
            Optional<Book> book;
            book = dialog.showAndWait();
            if (!book.isPresent())
                return;

            library.addBook(book.get());
            library.storeToDataDirectory(library.getDataDirectory());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeBook() {

    }

    @FXML
    private void returnBook() {

    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}
