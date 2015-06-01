package com.usp.icmc.libraryController.model;

import java.util.ArrayList;

/**
 * The {@code User} class is a model representation of a user to be used in a
 * {@code Library}
 *
 * @see com.usp.icmc.libraryController.model.Library
 */
public class User {

    public static final int STUDENT = 1;
    public static final int PROFESSOR = 2;
    public static final int COMMUNITY_MEMBER = 3;
    private static long maxId = 0;

    private int maxBorrowBooks = 0;
    private int maxBorrowDays = 0;
    private String name;
    private long id;
    private boolean borrowExpired = false;
    private int type;
    private ArrayList<Book> borrowedBooks;

    /**
     * Constructs a new User with a name, type and automatic ID.
     * Types can be:
     * <ul>
     * <li>User.STUDENT</li>
     * <li>User.PROFESSOR</li>
     * <li>User.COMMUNITY_MEMBER</li>
     * </ul>
     *
     * @param name the name of the user
     * @param type the type of the user
     */
    public User(String name, int type) {
        this(name, maxId++, type);
    }

    /**
     * Constructs a new User with a name, type and forced ID.
     * Types can be:
     * <ul>
     * <li>User.STUDENT</li>
     * <li>User.PROFESSOR</li>
     * <li>User.COMMUNITY_MEMBER</li>
     * </ul>
     *
     * @param name the name of the user
     * @param id   the id to force upon this new user
     * @param type the type of the user
     */
    public User(String name, long id, int type) {
        this.name = name;
        if (id >= maxId) maxId = id + 1;
        this.id = id;
        this.borrowedBooks = new ArrayList<>();
        this.type = type;
        switch (type) {
            case STUDENT:
                maxBorrowBooks = 4;
                maxBorrowDays = 15;
                break;
            case PROFESSOR:
                maxBorrowBooks = 6;
                maxBorrowDays = 60;
                break;
            case COMMUNITY_MEMBER:
                maxBorrowBooks = 2;
                maxBorrowDays = 15;
                break;
            default:
                maxBorrowBooks = 0;
                maxBorrowDays = 0;
        }
    }

    /**
     * Checks if this {@code User} is blacklisted, which means that the user
     * did not return a {@code book} in time
     *
     * @return {@code true} if the user is blacklisted and {@code false}
     * otherwise
     */
    public boolean isBorrowExpired() {
        return borrowExpired;
    }

    /**
     * Get this {@code user}'s ID
     *
     * @return this user's ID
     */
    public long getId() {
        return id;
    }

    /**
     * Set this {@code User} to be blacklisted or not
     *
     * @param borrowExpired {@code true} if the user is blacklisted and
     *                      {@code false} otherwise
     */
    public void setBorrowExpired(boolean borrowExpired) {
        this.borrowExpired = borrowExpired;
    }

    /**
     * Get the amount of books this {@code User} can have at any time
     *
     * @return the maximum amount of books this user can have
     */
    public int getMaxBorrowBooks() {
        return maxBorrowBooks;
    }

    /**
     * Get the amount of time this {@code User} can have a book for
     *
     * @return the maximum amount of time this user can have a book for
     */
    public int getMaxBorrowDays() {
        return maxBorrowDays;
    }

    /**
     * Return if this {@code user} can borrow a book or not. Based on the
     * amount of books that he has and the maximum he can have
     *
     * @return {@code true} if this user can borrow another book and {@code
     * false} if not
     */
    public boolean canBorrowBook() {
        return borrowedBooks.size() < this.getMaxBorrowBooks();
    }

    /**
     * add a {@code Book} to this {@code user}'s book list
     *
     * @param book the book to be borrowed
     */
    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    /**
     * Check is this {@code User} has a specified book
     *
     * @param book the book to be checked
     * @return {@code true} if this user has the specified book and {@code
     * false} if not
     */
    public boolean hasBook(Book book) {
        return borrowedBooks.contains(book);
    }

    /**
     * Return a book that the user has to the library.
     *
     * @param book the book to be returned, if it is
     */
    public void returnBook(Book book) {

        borrowedBooks.remove(book);

    }

    /**
     * Get a list of books this {@code user} has
     *
     * @return a {@code ArrayList} of books that this user has
     */
    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    /**
     * Get the name of this {@code user}
     *
     * @return the name of this user
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of this user
     * Types can be:
     * <ul>
     * <li>User.STUDENT</li>
     * <li>User.PROFESSOR</li>
     * <li>User.COMMUNITY_MEMBER</li>
     * </ul>
     *
     * @return the type of this user
     */
    public int getType() {
        return type;
    }
}
