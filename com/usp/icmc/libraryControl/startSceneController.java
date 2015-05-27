package com.usp.icmc.libraryControl;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
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

public class startSceneController implements Initializable {

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
            } catch (IOException ignored) {
                // TODO proper catching
            }
        }
        libraryList.setItems(libraryListElements);
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void addNewLibrary(Event e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder containing a library");
        directoryChooser.setInitialDirectory(new File("."));
        File selectedDirectory;
        selectedDirectory = directoryChooser.showDialog(
            (((Button) e.getSource()).getScene()).getWindow()
        );
        System.out.println(selectedDirectory.getPath());
        try {
            libraryList.getItems().add(selectedDirectory.getPath());
        } catch (Exception e1) {
            System.out.println("fodeu");
        }
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
            e.printStackTrace();
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
        String path = null;
        path = libraryList.getSelectionModel().getSelectedItem();
        File file = null;
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
                        Alert confirmCreateNewLibrary = new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "The selected library does not exist, do you want" +
                            " to create a new one?",
                            ButtonType.NO,
                            ButtonType.YES
                        );
                        confirmCreateNewLibrary.setTitle("Library exists");
                        confirmCreateNewLibrary
                            .setHeaderText("Library exists!");
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
                        getClass().getResource("mainScene.fxml")
                    );
                    Parent root;
                    Stage stage;
                    try {
                        root = loader.load();
                        stage = (
                            (Stage) ((Button) e.getSource()).getScene()
                                .getWindow()
                        );
                        mainSceneController controller = loader
                            .getController();
                        controller.setLibrary(new Library(path));
                        stage.setScene(new Scene(root));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

    }

}
