package com.usp.icmc.libraryController.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class {@code Book} is a model representation of a book to be used in a
 * {@code Library}
 *
 * @see com.usp.icmc.libraryController.model.Library
 */
public class Book {

    private static long maxId = 0;

    private String title;
    private String author;
    private long id;
    private boolean availableForBorrow;
    private boolean canBeBorrowedByAnyone = false;
    private ArrayList<BorrowedLog> borrowLog;

    /**
     * Constructs a {@code Book} with the specified title, author and type.
     *
     * @param title                 the title of the book to be created
     * @param author                the author of the book to be created
     * @param canBeBorrowedByAnyone the type of the book ({@code true} for
     *                              General type and {@code false} for Text
     *                              type)
     */
    public Book(String title, String author, boolean canBeBorrowedByAnyone) {
        this(title, author, canBeBorrowedByAnyone, maxId++, true);
    }

    /**
     * Constructs a {@code Book} with the specified title, author and type.
     * Forces the ID to be at least the specified one.
     * Should never be called to create a new book.
     *
     * @param title                 the title of the book to be created
     * @param author                the author of the book to be created
     * @param canBeBorrowedByAnyone the type of the book (true for General
     *                              type and false for Text type)
     * @param id                    the id to be forced upon the book
     */
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

    /**
     * Returns if this {@code Book} can be borrowed by anyone (General type)
     * or not (Text type)
     *
     * @return {@code true} if the book is of type General and {@code false}
     * if it is a Text
     */
    public boolean canBeBorrowedByAnyone() {
        return canBeBorrowedByAnyone;
    }

    /**
     * Set the {@code Book} status to borrowed or available
     *
     * @return {@code false} if the book is borrowed and {@code true} if it's
     * not
     */
    public boolean isAvailableForBorrow() {
        return availableForBorrow;
    }

    /**
     * Check if this {@code Book} is available or not
     *
     * @param availableForBorrow {@code false} if the book is borrowed and
     *                           {@code true} if
     *                           it's not
     */
    public void setAvailableForBorrow(boolean availableForBorrow) {
        this.availableForBorrow = availableForBorrow;
    }

    /**
     * Get this {@code Book} title
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get this {@code Book} author
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get this {@code Book} ID
     *
     * @return the ID of the book
     */
    public long getId() {
        return id;
    }

    /**
     * Adds a entry to the book's borrow log, the entry contains the user
     * that borrowed it, the date in which he did and the date he's supposed
     * to return it
     *
     * @param user the user that borrowed the book
     */
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

    /**
     * Get this {@code Book}'s borrow log
     *
     * @return a arrayList of entries in the borrow log
     * @see com.usp.icmc.libraryController.model.BorrowedLog
     */
    public ArrayList<BorrowedLog> getBorrowLog() {
        return borrowLog;
    }

    /**
     * returns a string that "textually represents" the book object
     * containing its ID, title and author
     *
     * @return a string that represents this book
     */
    @Override
    public String toString() {
        return "Book id:\t\t" + this.id +
               "\nBook Title:\t" + this.title +
               "\nBook Author:\t" + this.author;
    }


    /**
     * compare this book object with {@code obj} to see if they are the same
     * book. As the ID is unique for every book object, it does not influence
     * the comparison.
     *
     * @param obj the reference object with which to compare
     * @return {@code true} if both books are the same and {@code false} if
     * they are different
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Book &&
               ((Book) obj).getTitle().equals(this.getTitle()) &&
               ((Book) obj).getAuthor().equals(this.getAuthor());
    }

}
