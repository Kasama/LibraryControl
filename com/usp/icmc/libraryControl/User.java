package com.usp.icmc.libraryControl;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public class User {

    private static final int maxRentalBooks = 0;
    private static final int maxRentalDays = 0;

    private String name;
    private int id;
    private boolean rentalExpired = false;
    private int rentalExpiredDays = 0;
    private Map<Book, Date> returnDates;

    public User(String name, int id) {
        this.name = name;
        this.id = id;
        this.returnDates = new Hashtable<>();
    }

    public boolean isRentalExpired() {
        return rentalExpired;
    }

    public void setRentalExpired(boolean rentalExpired) {
        this.rentalExpired = rentalExpired;
    }

    public int getRentalExpiredDays() {
        return rentalExpiredDays;
    }

    public void setRentalExpiredDays(int rentalExpiredDays) {
        this.rentalExpiredDays = rentalExpiredDays;
    }

    public int getMaxRentalBooks() {
        return maxRentalBooks;
    }

    public int getMaxRentalDays() {
        return maxRentalDays;
    }

    public boolean canRentBook(){
        return false;
    }

    public void rentBook(Book book) throws noBookFoundException{
        throw new noBookFoundException(book);
    }

    public boolean hasBook(Book book){
        return false;
    }

    public void returnBook(Book book) throws noBookFoundException{

        throw new noBookFoundException(book);

    }
}
