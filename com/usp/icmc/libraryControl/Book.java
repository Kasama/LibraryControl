package com.usp.icmc.libraryControl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Book {

    private String title;
    private String author;
    private int id;
    private boolean availableForRental = false;
    private Map<User, Map<Date, Date>> rentalLog;

    public Book(String title, String author, int id) {
        this.title = title;
        this.author = author;
        this.id = id;
        this.rentalLog = new HashMap<>();
    }

    public boolean isAvailableForRental() {
        return availableForRental;
    }

    public void setAvailableForRental(boolean availableForRental) {
        this.availableForRental = availableForRental;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Book id: "+this.id+
               "\nBook Title: "+this.title+
               "\nBook Author: "+this.author+
               "\n";
    }
}
