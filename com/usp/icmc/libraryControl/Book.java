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
    private boolean availableForRental = false;
    private boolean canBeRentedByAnyone = false;
    private Map<User, Map.Entry<Date, Date>> rentalLog;

    public Book(String title, String author, boolean canBeRentedByAnyone) {
        this(title, author, canBeRentedByAnyone, maxId++);
    }

    private Book(String title, String author, boolean canBeRentedByAnyone, long id){
        this.title = title;
        this.author = author;
        this.id = id;
        this.canBeRentedByAnyone = canBeRentedByAnyone;
        this.rentalLog = new HashMap<>();
    }

    public boolean canBeRentedByAnyone() {
        return canBeRentedByAnyone;
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

    public long getId() {
        return id;
    }

    public void writeRentalLog(User user){

        Date today;
        Date toReturn;

        today = TimeController.getDate();
        toReturn = new Date(
                today.getTime() +
                user.getMaxRentalDays()*24*60*60*1000
        );

        Map.Entry<Date, Date> m =
                new AbstractMap.SimpleEntry<>(today, toReturn);
        rentalLog.put(user, m);

    }

    public Map<User, Map.Entry<Date, Date>> getRentalLog(){
        return rentalLog;
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
