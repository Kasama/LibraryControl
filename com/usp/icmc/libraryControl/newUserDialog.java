package com.usp.icmc.libraryControl;

import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class newUserDialog extends Dialog {

    @FXML
    private ToggleGroup typeSelection;
    @FXML
    private TextField userName;
    @FXML
    private RadioButton radioProfessor;
    @FXML
    private RadioButton radioStudent;
    @FXML
    private RadioButton radioCommunityMember;

    public String getUserName() {
        return userName.getText();
    }

    public int getOption() {
        RadioButton selected = (
            (RadioButton) typeSelection.getSelectedToggle()
        );
        if (selected.equals(radioProfessor))
            return User.PROFESSOR;
        else if (selected.equals(radioStudent))
            return User.STUDENT;
        else if (selected.equals(radioCommunityMember))
            return User.COMMUNITY_MEMBER;
        else
            return -1;
    }
}