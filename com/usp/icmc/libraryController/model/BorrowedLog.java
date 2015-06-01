package com.usp.icmc.libraryController.model;

import java.util.Date;

/**
 * Class {@code BorrowedLog} represents an entry of a {@link Book}'s borrow log.
 *
 * @see com.usp.icmc.libraryController.model.Book
 */
public class BorrowedLog {
    private User user;
    private Date borrowedDate;
    private Date returnDate;

    /**
     * Constructs a new {@code BorrowLog} entry for the {@code User}, with
     * the current {@code Date} being {@code borrowedDate} and the return
     * date being {@code returnDate}
     *
     * @param user         the user that will be represented on this log entry
     * @param borrowedDate the date when the user borrowed the book
     * @param returnDate   the date when the user is supposed to return the book
     */
    public BorrowedLog(User user, Date borrowedDate, Date returnDate) {
        this.user = user;
        this.borrowedDate = borrowedDate;
        this.returnDate = returnDate;
    }

    /**
     * Get the {@code User} this {@code BorrowLog} represents
     *
     * @return the user this {@code BorrowLog} represents
     */
    public User getUser() {
        return user;
    }

    /**
     * Get the {@code Date} when the {@code User} borrowed the {@code Book}.
     *
     * @return the date when the user borrowed the book
     */
    public Date getBorrowedDate() {
        return borrowedDate;
    }

    /**
     * Get the {@code Date} when the {@code User} should return the {@code
     * Book}.
     *
     * @return the date when the user should return the book
     */
    public Date getReturnDate() {
        return returnDate;
    }

}
