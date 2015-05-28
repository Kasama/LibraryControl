package com.usp.icmc.libraryControl;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class newBookDialogController {

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

    public String getTitle() {
        return title.getText();
    }

    public String getAuthor() {
        return author.getText();
    }

    public boolean getOption() {
        RadioButton selected = (
            (RadioButton) typeSelection.getSelectedToggle()
        );
        return selected.equals(radioGeneral);
    }
}
