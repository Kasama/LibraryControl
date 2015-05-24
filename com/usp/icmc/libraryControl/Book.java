package com.usp.icmc.libraryControl;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Book {

    private static long maxId = 0;

    private String title;
    private String author;
    private long id;
    private boolean availableForBorrow = false;
    private boolean canBeBorrowedByAnyone = false;
    private Map<User, Map.Entry<Date, Date>> borrowLog;

    public Book(String title, String author, boolean canBeBorrowedByAnyone) {
        this(title, author, canBeBorrowedByAnyone, maxId++);
    }

    private Book(String title, String author, boolean canBeBorrowedByAnyone, long id){
        this.title = title;
        this.author = author;
        this.id = id;
        this.canBeBorrowedByAnyone = canBeBorrowedByAnyone;
        this.borrowLog = new HashMap<>();
    }

    public boolean canBeRentedByAnyone() {
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

    public void writeBorrowLog(User user){

        Date today;
        Date toReturn;

        today = TimeController.getDate();
        toReturn = new Date(
                today.getTime() +
                user.getMaxBorrowDays()*24*60*60*1000
        );

        Map.Entry<Date, Date> m =
                new AbstractMap.SimpleEntry<>(today, toReturn);
        borrowLog.put(user, m);

    }

    public Map<User, Map.Entry<Date, Date>> getBorrowLog(){
        return borrowLog;
    }

    @Override
    public String toString() {
        return "Book id: "+this.id+
               "\nBook Title: "+this.title+
               "\nBook Author: "+this.author+
               "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Book){
            return ((Book) obj).getTitle().equals(this.getTitle()) &
                   ((Book) obj).getAuthor().equals(this.getAuthor());
        }
        return false;
    }
}
