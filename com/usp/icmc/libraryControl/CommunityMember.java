package com.usp.icmc.libraryControl;

public class CommunityMember extends User{

    private static final int maxRentalBooks = 2;
    private static final int maxRentalDays = 15;

    public CommunityMember(String name, int id) {
        super(name, id);
    }

}
