package com.usp.icmc.controller;

import com.usp.icmc.library.Book;
import com.usp.icmc.library.Library;
import com.usp.icmc.library.User;
import com.usp.icmc.extra.ObservableBook;
import com.usp.icmc.extra.ObservableUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

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
    private ComboBox<ObservableUser> userSelector;
    @FXML
    private ComboBox<ObservableBook> bookSelector;
    @FXML
    private TextField searchBox;

    private Library library;
    ObservableList<ObservableUser> userList;
    ObservableList<ObservableBook> bookList;

    @Override
    public void initialize(
        URL location, ResourceBundle resources
    ) {
        //        userSelector.setOnKeyTyped(
        //            new AutoCompleteComboBoxListener<String>(userSelector)
        //        );
        //        bookSelector.setOnKeyTyped(
        //            new AutoCompleteComboBoxListener<String>(bookSelector)
        //        );
    }

    @FXML
    private void addNewUser() {
        Optional<User> user;

        NewUserDialog dialog = new NewUserDialog();
        user = dialog.showAndWait();
        if (!user.isPresent())
            return;

        library.addUser(user.get());
        this.addObservableUser(user.get());
        new Thread(
            () -> library.storeToDataDirectory(library.getDataDirectory())
        ).start();

    }

    @FXML
    private void removeUser() {
        ObservableUser user =
            usersTable.getSelectionModel().getSelectedItem();
        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Select a user");
            alert.setHeaderText("No user selected!");
            alert.setContentText("Please select a user to remove");
            alert.show();
            return;
        }
        User u = library.getUser(user.getID());
        if (u.getBorrowedBooks().size() != 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("User has books");
            alert.setHeaderText("The selected user cannot be removed!");
            alert.setContentText("The user has one or more books");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm user removal");
        alert.setHeaderText("Really want to remove user?");
        alert.setContentText("User: " + user.getName() + " ID:" + user.getID());
        Optional<ButtonType> o;
        o = alert.showAndWait();
        if (!o.isPresent() || !o.get().equals(ButtonType.OK))
            return;
        if (u.isBorrowExpired())
            library.removeUserFromBlacklist(library.getUser(user.getID()));
        this.removeObservableUser(library.getUser(user.getID()));
        library.removeUser(user.getID());
        new Thread(
            () -> library.storeToDataDirectory(library.getDataDirectory())
        ).start();
    }

    @FXML
    private void borrowToUser() {
        Book book;
        User user;
        ObservableBook b =
            booksTable.getSelectionModel().getSelectedItem();
        ObservableUser u = userSelector.getValue();
        if (u == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Select a user");
            alert.setHeaderText("No user selected!");
            alert.setContentText("Please select a user from whom to borrow");
            alert.show();
            return;
        } else if (b == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Select a book");
            alert.setHeaderText("No book selected!");
            alert.setContentText("Please select a book to borrow");
            alert.show();
            return;
        }

        book = library.getBook(b.getID());
        user = library.getUser(u.getID());

        if (!library.borrowBook(user, book)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Not permitted");
            alert.setHeaderText("Invalid operation!");
            alert.setContentText("This user cannot borrow this book");
            alert.show();
            return;
        }
        u.setBorrowed(u.getBorrowed() + 1);
        syncObservableBooks(book);
        new Thread(
            () -> library.storeToDataDirectory(library.getDataDirectory())
        ).start();
    }

    @FXML
    private void addNewBook() {
        Optional<Book> book;
        NewBookDialog dialog = new NewBookDialog();
        book = dialog.showAndWait();
        if (!book.isPresent())
            return;

        library.addBook(book.get());
        this.addObservableBooks(book.get());
        new Thread(
            () -> library.storeToDataDirectory(library.getDataDirectory())
        ).start();
    }

    @FXML
    private void removeBook() {
        ObservableBook book =
            booksTable.getSelectionModel().getSelectedItem();
        if (!library.getBook(book.getID()).isAvailableForBorrow()) {
            Alert cannotRemoveDialog = new Alert(Alert.AlertType.ERROR);
            cannotRemoveDialog.setTitle("Could not remove selected Book");
            cannotRemoveDialog.setHeaderText("Failed to remove Book");
            cannotRemoveDialog.setContentText("The selected book is borrowed!");
            cannotRemoveDialog.show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm user removal");
        alert.setHeaderText("Really want to remove user?");
        alert.setContentText(
            "Title: " + book.getTitle() + " ID:" + book.getID()
        );
        Optional<ButtonType> o;
        o = alert.showAndWait();
        if (!o.isPresent() || !o.get().equals(ButtonType.OK))
            return;
        this.removeObservableBook(library.getBook(book.getID()));
        library.removeBook(book.getID());
        new Thread(
            () -> library.storeToDataDirectory(library.getDataDirectory())
        ).start();
    }

    @FXML
    private void returnBook() {
        Book book;
        User user;
        ObservableUser u =
            usersTable.getSelectionModel().getSelectedItem();
        ObservableBook b = bookSelector.getValue();
        if (u == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Select a user");
            alert.setHeaderText("No user selected!");
            alert.setContentText("Please select a user from whom to return");
            alert.show();
            return;
        } else if (b == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Select a book");
            alert.setHeaderText("No book selected!");
            alert.setContentText("Please select a book to return");
            alert.show();
            return;
        }

        book = library.getBook(b.getID());
        user = library.getUser(u.getID());

        if (!library.returnBook(user, book)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Not permitted");
            alert.setHeaderText("Invalid operation!");
            alert.setContentText("This user cannot return this book");
            alert.show();
            return;
        }
        u.setBorrowed(u.getBorrowed() - 1);
        syncObservableBooks(book);
        filterComboBox();
        new Thread(
            () -> library.storeToDataDirectory(library.getDataDirectory())
        ).start();
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public void syncObservableBooks(Book book) {
        Optional<ObservableBook> o;
        o = bookList
            .stream()
            .filter(
                b -> b.getID() == book.getId()
            )
            .findFirst();
        bookList.remove(o.get());
        bookList.add(new ObservableBook(book));

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

        userSelector.setItems(userList);
        userSelector.setConverter(
            new StringConverter<ObservableUser>() {
                @Override
                public String toString(ObservableUser user) {
                    return user.toString();
                }

                @Override
                public ObservableUser fromString(String string) {
                    String id;
                    int sep;
                    Optional<ObservableUser> ret;
                    sep = string.indexOf(":");
                    id = string.substring(0, sep);
                    ret = userList
                        .stream()
                        .filter(
                            u -> u.getID() == Long.parseLong(id)
                        )
                        .findFirst();
                    if (ret.isPresent())
                        return ret.get();
                    else
                        return null;
                }
            }
        );
        bookSelector.setItems(bookList);
        bookSelector.setConverter(
            new StringConverter<ObservableBook>() {
                @Override
                public String toString(ObservableBook book) {
                    //                    if (book == null)
                    //                        return "";
                    //                    else
                    return book.toString();
                }

                @Override
                public ObservableBook fromString(String string) {
                    String id;
                    int sep;
                    Optional<ObservableBook> ret;
                    sep = string.indexOf(":");
                    id = string.substring(0, sep);
                    ret = bookList
                        .stream()
                        .filter(
                            b -> b.getID() == Long.parseLong(id)
                        )
                        .findFirst();
                    if (ret.isPresent())
                        return ret.get();
                    else
                        return null;
                }
            }
        );
    }

    public void viewBorrowedBooks() {
        StringBuilder sb = new StringBuilder();

        ObservableUser user =
            usersTable.getSelectionModel().getSelectedItem();
        User u = library.getUser(user.getID());
        u.getBorrowedBooks().forEach(
            book -> {
                sb.append(book.toString());
                sb.append("\nBorrowed on:\t");
                sb.append(
                    book
                        .getBorrowLog()
                        .get(book.getBorrowLog().size() - 1)
                        .getBorrowedDate()
                );
                sb.append("\nReturn until:\t");
                sb.append(
                    book
                        .getBorrowLog()
                        .get(book.getBorrowLog().size() - 1)
                        .getReturnDate()
                );
                sb.append("\n\n");
            }
        );

        Viewer view = new Viewer(u.getName() + "'s books", sb.toString());
        view.setTitle("Borrowed Books");
        view.show();

    }

    public void viewLog() {
        StringBuilder sb = new StringBuilder();

        ObservableBook book =
            booksTable.getSelectionModel().getSelectedItem();
        Book b = library.getBook(book.getID());
        b.getBorrowLog().forEach(
            logEntry -> {
                User u = logEntry.getUser();
                sb.append("Borrowed by ");
                sb.append(u.getName());
                sb.append(" [ID: ");
                sb.append(u.getId());
                sb.append("]\n");
                sb.append("On:\t\t\t");
                sb.append(logEntry.getBorrowedDate());
                sb.append("\nReturn until:\t");
                sb.append(logEntry.getReturnDate());
                sb.append("\n\n");
            }
        );

        Viewer view = new Viewer(b.getTitle() + "'s log", sb.toString());
        view.setTitle("Book Log");
        view.show();
    }

    public void filterComboBox() {
        ObservableUser user =
            usersTable.getSelectionModel().getSelectedItem();
        if (user == null) return;
        User u = library.getUser(user.getID());
        ObservableList<ObservableBook> boxList;
        boxList = FXCollections.observableArrayList();
        boxList.addAll(
            bookList.stream().filter(
                observableBook -> u
                    .hasBook(library.getBook(observableBook.getID()))
            ).collect(Collectors.toList())
        );
        bookSelector.setItems(boxList);
    }
}
