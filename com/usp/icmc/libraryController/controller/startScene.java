package com.usp.icmc.libraryController.controller;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.usp.icmc.libraryController.model.Library;
import com.usp.icmc.libraryController.model.TimeController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class startScene extends Parent implements Initializable {

    @FXML
    Button add;
    @FXML
    Button remove;
    @FXML
    Button loadLibrary;
    @FXML
    ListView<String> libraryList;
    @FXML
    DatePicker datePicker;

    @Override
    public void initialize(
        URL location, ResourceBundle resources
    ) {
        ObservableList<String> libraryListElements = FXCollections
            .observableArrayList();
        File file = new File("libraryList.csv");
        if (file.exists()) {
            try {
                CSVReader csvReader = new CSVReader(new FileReader(file));
                String[] tokens;
                while ((tokens = csvReader.readNext()) != null)
                    libraryListElements.add(tokens[0]);
            } catch (IOException e) {
                com.usp.icmc.libraryController.controller.Alert
                    alert = new com.usp.icmc.libraryController.controller.Alert(
                    com.usp.icmc.libraryController.controller.Alert.AlertType
                        .ERROR
                );
                alert.setTitle("Fatal error!");
                alert.setHeaderText("A file read exception was reach");
                alert.setContentText("Check your read/write permissions");
                alert.showAndWait();
                e.printStackTrace();
                System.exit(1);
            }
        }
        libraryList.setItems(libraryListElements);
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void addNewLibrary(Event e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder containing a model");
        directoryChooser.setInitialDirectory(new File("."));
        File selectedDirectory;
        selectedDirectory = directoryChooser.showDialog(
            (((Button) e.getSource()).getScene()).getWindow()
        );
        if (selectedDirectory == null)
            return;
        libraryList.getItems().add(selectedDirectory.getPath());
        updateLibrariesFile();
    }

    private void updateLibrariesFile() {
        File file = new File("libraryList.csv");
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
            for (String s : libraryList.getItems()) {
                String[] nextLine = new String[1];
                nextLine[0] = s;
                csvWriter.writeNext(nextLine);
                csvWriter.flush();
            }
        } catch (IOException e) {
            com.usp.icmc.libraryController.controller.Alert
                alert = new com.usp.icmc.libraryController.controller.Alert(
                com.usp.icmc.libraryController.controller.Alert.AlertType.ERROR
            );
            alert.setTitle("Fatal error!");
            alert.setHeaderText("A file read exception was reach");
            alert.setContentText("Check your read/write permissions");
            alert.showAndWait();
            e.printStackTrace();
            System.exit(1);
        }
    }

    @FXML
    private void removeSelectedLibrary() {
        libraryList.getItems()
            .remove(libraryList.getSelectionModel().getSelectedItem());
        updateLibrariesFile();
    }

    @FXML
    private void loadLibrary(Event e) {
        String path;
        path = libraryList.getSelectionModel().getSelectedItem();
        File file;
        if (path == null)
            return;
        file = new File(path);

        if (file.exists()) {
            if (file.isDirectory()) {
                File[] filesArr = file.listFiles();
                ArrayList<File> files;
                if (filesArr != null) {
                    files = new ArrayList<>(Arrays.asList(filesArr));
                    long l = files
                        .stream()
                        .map(File::getAbsolutePath)
                        .distinct()
                        .parallel()
                        .filter(
                            f ->
                                f.contains("book.csv") ||
                                f.contains("users.csv") ||
                                f.contains("blacklist.csv") ||
                                f.contains("/book") ||
                                f.contains("/users")
                        )
                        .count();
                    if (l != 5) {
                        com.usp.icmc.libraryController.controller.Alert
                            confirmCreateNewLibrary
                            = new com.usp.icmc.libraryController.controller
                            .Alert(
                            com.usp.icmc.libraryController.controller.Alert
                                .AlertType.CONFIRMATION,
                            "Do you want to create a new one?",
                            ButtonType.NO,
                            ButtonType.YES
                        );
                        confirmCreateNewLibrary
                            .setTitle("Library does not exists");
                        confirmCreateNewLibrary
                            .setHeaderText("Library does not exists!");
                        Optional<ButtonType> selection
                            = confirmCreateNewLibrary.showAndWait();
                        if (!selection.isPresent() ||
                            selection.get().equals(ButtonType.NO))
                            return;
                    }
                    java.sql.Date d = java.sql.Date.valueOf(
                        datePicker.getValue()
                    );
                    Date date = new Date(d.getTime());
                    TimeController timeController = TimeController
                        .getInstance();
                    timeController.setDate(date);
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("../view/mainScene.fxml")
                    );
                    Parent root;
                    Stage stage;
                    try {
                        root = loader.load();
                        stage = (
                            (Stage) ((Button) e.getSource()).getScene()
                                .getWindow()
                        );
                        mainScene controller = loader
                            .getController();
                        controller.setLibrary(new Library(path));
                        controller.initComponents();
                        stage.setScene(new Scene(root));
                        stage.setHeight(600);
                        stage.setWidth(800);
                    } catch (IOException ex) {
                        com.usp.icmc.libraryController.controller.Alert
                            alert
                            = new com.usp.icmc.libraryController.controller
                            .Alert(
                            com.usp.icmc.libraryController.controller.Alert
                                .AlertType.ERROR
                        );
                        alert.setTitle("Fatal error!");
                        alert.setHeaderText("Could not find GUI file");
                        alert.setContentText(
                            "Check your read/write permissions"
                        );
                        alert.showAndWait();
                        ex.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }

    }
}
