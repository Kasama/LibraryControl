package com.usp.icmc.libraryController.model;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TimeController extends Observable {

    private Calendar calendar;
    private static TimeController instance = null;
    private Timer timer;

    private TimeController() {
        calendar = Calendar.getInstance();
        timer = new Timer(true);
    }

    public static TimeController getInstance(){
        if (instance == null)
            instance = new TimeController();
        return instance;
    }

    public void setDate(Date date) {
        calendar.setTime(date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);


        long time = c.getTimeInMillis() - date.getTime();

        Thread t = new Thread(
            () -> {
                timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            calendar.add(Calendar.DATE, 1);
                            notifyObservers();
                            timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        calendar.add(Calendar.DATE, 1);
                                        notifyObservers();
                                    }
                                }, 24 * 60 * 60 * 1000 // 24h time
                            );
                        }
                    }, time
                );
            }
        );
        t.setDaemon(true);
        t.start();
    }

    public Date getDate() {
        return calendar.getTime();
    }

    public long getTimeInDays(Date date){
        return TimeUnit.DAYS.toDays(date.getTime());
    }

    public Date decrementDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);

        return c.getTime();
    }

    public Date addTime(Date date, long time) {
        return new Date(date.getTime() + time);
    }
}
