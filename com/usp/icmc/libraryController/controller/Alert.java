package com.usp.icmc.libraryController.controller;

import javafx.scene.control.ButtonType;

public class Alert extends javafx.scene.control.Alert {
    public Alert(AlertType alertType) {
        super(alertType);
        this.getDialogPane().getStylesheets()
            .add("com/usp/icmc/libraryController/view/style.css");
    }

    public Alert(
        AlertType alertType, String contentText,
        ButtonType... buttons
    ) {
        super(alertType, contentText, buttons);
        this.getDialogPane().getStylesheets()
            .add("com/usp/icmc/libraryController/view/style.css");
    }
}
