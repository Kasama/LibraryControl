package com.usp.icmc.libraryControl;

import com.usp.icmc.library.Book;
import javafx.beans.property.*;

public class ObservableBook {
    LongProperty ID;
    StringProperty title;
    StringProperty author;
    StringProperty type;
    StringProperty status;

    public ObservableBook(Book book) {
        this.ID = new SimpleLongProperty(book.getId());
        this.title = new SimpleStringProperty(book.getTitle());
        this.author = new SimpleStringProperty(book.getAuthor());
        this.type = new SimpleStringProperty(
            book.canBeBorrowedByAnyone() ? "General" : "Text"
        );
        this.status = new SimpleStringProperty(
            book.isAvailableForBorrow() ? "Available" : "Borrowed"
        );
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public long getID() {
        return ID.get();
    }

    public LongProperty IDProperty() {
        return ID;
    }

    public void setID(long ID) {
        this.ID.set(ID);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
