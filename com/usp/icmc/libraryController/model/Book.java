package com.usp.icmc.libraryController.model;

import java.util.ArrayList;
import java.util.Date;

public class Book {

    private static long maxId = 0;

    private String title;
    private String author;
    private long id;
    private boolean availableForBorrow;
    private boolean canBeBorrowedByAnyone = false;
    private ArrayList<BorrowedLog> borrowLog;

    public Book(String title, String author, boolean canBeBorrowedByAnyone) {
        this(title, author, canBeBorrowedByAnyone, maxId++, true);
    }

    public Book(
        String title, String author, boolean canBeBorrowedByAnyone, long id,
        boolean availableForBorrow
    ) {
        this.title = title;
        this.author = author;
        if (id >= maxId) maxId = id + 1;
        this.id = id;
        this.canBeBorrowedByAnyone = canBeBorrowedByAnyone;
        this.availableForBorrow = availableForBorrow;
        this.borrowLog = new ArrayList<>();
    }

    public boolean canBeBorrowedByAnyone() {
        return canBeBorrowedByAnyone;
    }

    public boolean isAvailableForBorrow() {
        return availableForBorrow;
    }

    public void setAvailableForBorrow(boolean availableForBorrow) {
        this.availableForBorrow = availableForBorrow;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public long getId() {
        return id;
    }

    public void writeBorrowLog(User user) {

        Date today;
        Date toReturn;

        today = TimeController.getInstance().getDate();
        toReturn = new Date(
            today.getTime() +
            ((long) user.getMaxBorrowDays()) * 24l * 60l * 60l * 1000l
        );

        borrowLog.add(new BorrowedLog(user, today, toReturn));

    }

    public ArrayList<BorrowedLog> getBorrowLog() {
        return borrowLog;
    }

    @Override
    public String toString() {
        return "Book id:\t\t" + this.id +
               "\nBook Title:\t" + this.title +
               "\nBook Author:\t" + this.author;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Book &&
               ((Book) obj).getTitle().equals(this.getTitle()) &&
               ((Book) obj).getAuthor().equals(this.getAuthor());
    }

}
