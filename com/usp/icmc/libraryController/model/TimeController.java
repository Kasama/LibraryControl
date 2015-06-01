package com.usp.icmc.libraryController.model;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The {@code TimeController} class is meant to have a instance of calendar
 * and do some operations with time and date.
 * It does also extend {@link java.util.Observable} so it can notify time
 * events to all interested {@link java.util.Observer}s
 *
 * @see java.util.Date
 * @see java.util.Observable
 * @see java.util.Observer
 */
public class TimeController extends Observable {

    private Calendar calendar;
    private static TimeController instance = null;
    private Timer timer;

    /**
     * Constructs a new {@code TimeController}. this should only be called by
     * {@link TimeController#getInstance()}, because there should only be one
     * instance of {@code TimeController} at any given time
     */
    private TimeController() {
        calendar = Calendar.getInstance();
        timer = new Timer(true);
    }

    /**
     * Gets a instance of {@code TimeController}.
     * If no instance exists a new one is created, otherwise the existing
     * instance is passed
     *
     * @return a TimeController instance
     */
    public static TimeController getInstance() {
        if (instance == null)
            instance = new TimeController();
        return instance;
    }

    /**
     * Sets the initial calendar date and start a timer to notify all {@code
     * Observers} when the day changes
     *
     * @param date the date to be set
     */
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

    /**
     * Get the current {@code Date} in the calendar
     *
     * @return the current {@code Date}
     */
    public Date getDate() {
        return calendar.getTime();
    }

    /**
     * Get the day represented by a {@code Date} object
     *
     * @param date the date object
     * @return the day represented by the date object
     */
    public long getTimeInDays(Date date) {
        return TimeUnit.DAYS.toDays(date.getTime());
    }

    /**
     * Calculates the date before the specified one
     *
     * @param date the base date
     * @return the day before date
     */
    public Date decrementDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);

        return c.getTime();
    }

    /**
     * Calculates the time addition to a date
     *
     * @param date the base date
     * @param time the time to be added
     * @return a new Date object that represents the new time
     */
    public Date addTime(Date date, long time) {
        return new Date(date.getTime() + time);
    }
}
