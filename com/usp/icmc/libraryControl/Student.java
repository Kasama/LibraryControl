package com.usp.icmc.libraryControl;

public class Student extends User{

    private static final int maxRentalBooks = 4;
    private static final int maxRentalDays = 15;

    public Student(String name, int id) {
        super(name, id);
    }
}
