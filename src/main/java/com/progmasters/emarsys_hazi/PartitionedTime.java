package com.progmasters.emarsys_hazi;

public class PartitionedTime {

    private int weeks, days, hours;

    public PartitionedTime(int time) {
        this.weeks = time / 40;
        this.days = (time % 40) / 8;
        this.hours = (time % 40) % 8;
    }

    public int getWeeks() {
        return weeks;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }
}
