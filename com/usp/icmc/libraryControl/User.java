package com.usp.icmc.libraryControl;

import java.util.ArrayList;

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

    public User(String name, int type){
        this(name, maxId++, type);
    }

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

    public boolean isBorrowExpired() {
        return borrowExpired;
    }

    public long getId() {
        return id;
    }

    public void setBorrowExpired(boolean borrowExpired) {
        this.borrowExpired = borrowExpired;
    }

    public int getMaxBorrowBooks() {
        return maxBorrowBooks;
    }

    public int getMaxBorrowDays() {
        return maxBorrowDays;
    }

    public boolean canBorrowBook(){
        return borrowedBooks.size() < this.getMaxBorrowBooks();
    }

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    public boolean hasBook(Book book) {
        return borrowedBooks.contains(book);
    }

    public void returnBook(Book book) {

        borrowedBooks.remove(book);

    }
    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
}
