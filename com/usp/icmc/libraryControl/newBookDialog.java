package com.usp.icmc.libraryControl;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;

public class newBookDialog extends Dialog<Book> {

    @FXML
    public TextField title;
    @FXML
    public TextField author;
    @FXML
    public RadioButton radioGeneral;
    @FXML
    public RadioButton radioText;
    @FXML
    public ToggleGroup typeSelection;

    public newBookDialog() {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("newBookDialog.fxml")
        );
        loader.setController(this);

        ButtonType buttonCancel = new ButtonType(
            "Cancel", ButtonBar.ButtonData.CANCEL_CLOSE
        );
        ButtonType buttonOK = new ButtonType(
            "Confirm", ButtonBar.ButtonData.OK_DONE
        );
        Parent root;
        try {
            root = loader.load();
            this.getDialogPane().setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.getDialogPane().getButtonTypes().addAll(
            buttonCancel, buttonOK
        );
        this.setResultConverter(
            param -> param.equals(buttonOK) ?
                     new Book(
                         newBookDialog.this.getBookTitle(),
                         newBookDialog.this.getBookAuthor(),
                         newBookDialog.this.getOption()
                     ) : null
        );
    }

    public String getBookTitle() {
        return title.getText();
    }

    public String getBookAuthor() {
        return author.getText();
    }

    public boolean getOption() {
        RadioButton selected = (
            (RadioButton) typeSelection.getSelectedToggle()
        );
        return selected.equals(radioGeneral);
    }
}
