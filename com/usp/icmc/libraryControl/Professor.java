package com.usp.icmc.libraryControl;

public class Professor extends User {

    private static final int maxRentalBooks = 6;
    private static final int maxRentalDays = 60;

    public Professor(String name, int id) {
        super(name, id);
    }

}
