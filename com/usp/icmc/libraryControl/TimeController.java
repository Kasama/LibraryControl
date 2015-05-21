package com.usp.icmc.libraryControl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeController {

    private static Calendar calendar = new GregorianCalendar();

    private TimeController(){}

    public static void setDate(Date date){
        calendar.setTime(date);
    }

    public static Date getDate(){
        return calendar.getTime();
    }

}
