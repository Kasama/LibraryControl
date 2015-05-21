package com.usp.icmc.libraryControl;

import java.util.Calendar;
import java.util.Date;

public class TimeController {

    private static Calendar calendar;

    private TimeController(){}

    public static Date getDate(){
        return calendar.getTime();
    }

}
