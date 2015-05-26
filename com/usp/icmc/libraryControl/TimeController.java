package com.usp.icmc.libraryControl;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TimeController {

    private Calendar calendar;
    private static TimeController instance = null;
    private List<TimeEventListener> listeners;

    private TimeController() {
        calendar = Calendar.getInstance();
        listeners = new ArrayList<>();
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

        // TODO find a better approach
        new Thread(
            () -> {
                Timer timer = new Timer();
                timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            calendar.add(Calendar.DATE, 1);
                            notifyTimeEventListeners();
                            timer.cancel();
                            timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        calendar.add(Calendar.DATE, 1);
                                        notifyTimeEventListeners();
                                    }
                                }, 24*60*60*1000
                            );
                        }
                    }, time
                );
            }
        ).start();

    }

    private void notifyTimeEventListeners() {
        listeners.forEach(TimeEventListener::handleTimeEvent);
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
}
