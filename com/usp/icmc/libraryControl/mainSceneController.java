package com.usp.icmc.libraryControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class mainSceneController implements Initializable {

    @FXML
    private TableView<ObservableUser> usersTable;
    @FXML
    private TableView<ObservableBook> booksTable;
    @FXML
    private TableColumn<ObservableUser, String> tableUserStatus;
    @FXML
    private TableColumn<ObservableUser, String> tableUserType;
    @FXML
    private TableColumn<ObservableUser, Number> tableUserBorrowed;
    @FXML
    private TableColumn<ObservableUser, String> tableUserName;
    @FXML
    private TableColumn<ObservableUser, Number> tableUserID;
    @FXML
    private TableColumn<ObservableBook, String> tableBookStatus;
    @FXML
    private TableColumn<ObservableBook, String> tableBookTitle;
    @FXML
    private TableColumn<ObservableBook, String> tableBookAuthor;
    @FXML
    private TableColumn<ObservableBook, Number> tableBookID;
    @FXML
    private TableColumn<ObservableBook, String> tableBookType;
    @FXML
    private ComboBox<String> userSelector;
    @FXML
    private ComboBox<String> bookSelector;
    @FXML
    private TextField searchBox;

    private Library library;
    ObservableList<ObservableUser> userList;
    ObservableList<ObservableBook> bookList;

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
        Optional<User> user;

        newUserDialog dialog = new newUserDialog();
        user = dialog.showAndWait();
        if (!user.isPresent())
            return;

        library.addUser(user.get());
        this.addObservableUser(user.get());
        library.storeToDataDirectory(library.getDataDirectory());

    }

    @FXML
    private void removeUser() {
        ObservableUser user =
            usersTable.getSelectionModel().getSelectedItem();
        if (library.getUser(user.getID()).isBorrowExpired())
            library.removeUserFromBlacklist(library.getUser(user.getID()));
        this.removeObservableUser(library.getUser(user.getID()));
        library.removeUser(user.getID());
        library.storeToDataDirectory(library.getDataDirectory());
    }

    @FXML
    private void borrowToUser() {

    }

    @FXML
    private void addNewBook() {
        Optional<Book> book;
        newBookDialog dialog = new newBookDialog();
        book = dialog.showAndWait();
        if (!book.isPresent())
            return;

        library.addBook(book.get());
        library.storeToDataDirectory(library.getDataDirectory());
        this.addObservableBooks(book.get());
    }

    @FXML
    private void removeBook() {
        ObservableBook book =
            booksTable.getSelectionModel().getSelectedItem();
        if (!library.getBook(book.getID()).isAvailableForBorrow()) {
            // TODO alert user that a borrowed book can't be removed
            return;
        }
        this.removeObservableBook(library.getBook(book.getID()));
        library.removeBook(book.getID());
        library.storeToDataDirectory(library.getDataDirectory());
    }

    @FXML
    private void returnBook() {

    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public void addObservableBooks(Book book) {
        bookList.add(new ObservableBook(book));
    }

    public void removeObservableBook(Book book) {
        bookList.remove(library.getBooks().indexOf(book));
    }

    public void addObservableUser(User user) {
        userList.add(new ObservableUser(user));
    }

    public void removeObservableUser(User user) {
        userList.remove(library.getUsers().indexOf(user));
    }

    public void initTables() {
        userList = FXCollections.observableArrayList();

        userList.addAll(
            library.getUsers()
                .stream()
                .map(ObservableUser::new)
                .collect(Collectors.toList())
        );

        usersTable.setItems(userList);

        tableUserID.setCellValueFactory(
            cellData -> cellData.getValue().IDProperty()
        );
        tableUserName.setCellValueFactory(
            cellData -> cellData.getValue().nameProperty()
        );
        tableUserBorrowed.setCellValueFactory(
            cellData -> cellData.getValue().borrowedProperty()
        );
        tableUserType.setCellValueFactory(
            cellData -> cellData.getValue().typeProperty()
        );
        tableUserStatus.setCellValueFactory(
            cellData -> cellData.getValue().statusProperty()
        );

        bookList = FXCollections.observableArrayList();

        bookList.addAll(
            library.getBooks()
                .stream()
                .map(ObservableBook::new)
                .collect(Collectors.toList())
        );

        booksTable.setItems(bookList);

        tableBookID.setCellValueFactory(
            cellData -> cellData.getValue().IDProperty()
        );
        tableBookTitle.setCellValueFactory(
            cellData -> cellData.getValue().titleProperty()
        );
        tableBookAuthor.setCellValueFactory(
            cellData -> cellData.getValue().authorProperty()
        );
        tableBookType.setCellValueFactory(
            cellData -> cellData.getValue().typeProperty()
        );
        tableBookStatus.setCellValueFactory(
            cellData -> cellData.getValue().statusProperty()
        );
    }
}
