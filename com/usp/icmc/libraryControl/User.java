package com.usp.icmc.libraryControl;

import java.util.ArrayList;

public class User {

    public static final int STUDENT = 1;
    public static final int PROFESSOR = 2;
    public static final int COMMUNITY_MEMBER = 3;

    private int maxRentalBooks = 0;
    private int maxRentalDays = 0;
    private String name;
    private int id;
    private boolean rentalExpired = false;
    private int rentalExpiredDays = 0;
    private int type = User.COMMUNITY_MEMBER;
    private ArrayList<Book> rentedBooks;

    public User(String name, int id, int type) {
        this.name = name;
        this.id = id;
        this.rentedBooks = new ArrayList<>();
        this.type = type;
        switch (type) {
            case STUDENT:
                maxRentalBooks = 4;
                maxRentalDays = 15;
                break;
            case PROFESSOR:
                maxRentalBooks = 6;
                maxRentalDays = 60;
                break;
            case COMMUNITY_MEMBER:
                maxRentalBooks = 2;
                maxRentalDays = 15;
                break;
            default:
                maxRentalBooks = 0;
                maxRentalDays = 0;
        }
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
        return rentedBooks.size() < this.getMaxRentalBooks();
    }

    public void rentBook(Book book) {
        rentedBooks.add(book);
    }

    public boolean hasBook(Book book) {
        return rentedBooks.contains(book);
    }

    public void returnBook(Book book) throws noBookFoundException{

        throw new noBookFoundException(book);

    }
}
