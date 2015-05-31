package com.usp.icmc.libraryController.model;

import java.util.Date;

public class BorrowedLog {
    private User user;
    private Date borrowedDate;
    private Date returnDate;

    public BorrowedLog(User user, Date borrowedDate, Date returnDate) {
        this.user = user;
        this.borrowedDate = borrowedDate;
        this.returnDate = returnDate;
    }

    public User getUser() {
        return user;
    }

    public Date getBorrowedDate() {
        return borrowedDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

}
