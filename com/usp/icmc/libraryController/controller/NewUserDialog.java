package com.usp.icmc.libraryController.controller;

import com.usp.icmc.libraryController.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;

public class NewUserDialog extends Dialog<User> {

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

    public NewUserDialog() {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource(
                "/com/usp/icmc/libraryController/view/newUserDialog.fxml"
            )
        );
        loader.setController(this);
        this.getDialogPane().getStylesheets()
            .add("/com/usp/icmc/libraryController/view/style.css");

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
            param ->
                param.equals(buttonOK) ?
                new User(
                    this.getUserName(), this.getOption()
                ) : null
        );
    }

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
