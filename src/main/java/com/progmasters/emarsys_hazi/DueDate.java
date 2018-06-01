package com.progmasters.emarsys_hazi;

import java.time.*;

public class DueDate {

    private LocalDateTime submissionTime;
    private int turnaroundTime;
    private boolean error = false;
    private final int SECONDS_PER_HOUR = 3600;
    private final int SECONDS_PER_DAY = 86400;
    private final int SECONDS_PER_WEEK = 604800;
    private final int SECONDS_PER_WEEKEND = 172800;
    private final int SECONDS_TO_BE_WORKING_HOUR = 57600;

    public DueDate () {
    }

    public LocalDateTime calculateDueDate(DateAndTime dateAndTime, int turnaround) {
        setSubmissionTime(dateAndTime);
        setTurnaroundTime(turnaround);
        LocalDateTime dueDateTime = null;
        if (!error) {
            PartitionedTime partitionedTime = new PartitionedTime(this.turnaroundTime);
            long submissionEpoch = getEpochTime(this.submissionTime);
            long dueEpoch = submissionEpoch + partitionedTime.getWeeks() * SECONDS_PER_WEEK +
                    partitionedTime.getDays() * SECONDS_PER_DAY +
                    partitionedTime.getHours() * SECONDS_PER_HOUR;
            dueEpoch = adjustForOutOfWorkTime(dueEpoch);
            dueEpoch = adjustForWeekend(dueEpoch);
            dueDateTime = getDateTimeFromEpoch(dueEpoch);

        }
        return dueDateTime;
    }

    long getEpochTime(LocalDateTime time) {
        ZoneId zoneId = ZoneId.systemDefault();
        return time.atZone(zoneId).toEpochSecond();
    }


    void setSubmissionTime(DateAndTime dateAndTime) {
        int year = dateAndTime.getYear();
        int month = dateAndTime.getMonth();
        int day = dateAndTime.getDay();
        int hour = dateAndTime.getHour();
        int minute = dateAndTime.getMinute();
        try {
            this.submissionTime = LocalDateTime.of(year, month, day, hour, minute);
            if (hour < 9 || hour > 16 ||
                    this.submissionTime.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    this.submissionTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
                throw new DateTimeException("Please submit your query during working hours: Monday to Friday, 09:00 - 17:00");
            }
        } catch (DateTimeException e) {
            System.out.println(e.getMessage());
            this.error = true;
        }
    }

    long adjustForOutOfWorkTime(long epoch) {
        LocalDateTime time = getDateTimeFromEpoch(epoch);
        long adjustment = 0;
        long toReturn = epoch;
        if (time.getHour() > 17) {
            adjustment = SECONDS_TO_BE_WORKING_HOUR;
        }
        toReturn += adjustment;
        return (toReturn);
    }

    long adjustForWeekend(long epoch) {
        LocalDateTime time = getDateTimeFromEpoch(epoch);
        long adjustment = 0;
        long toReturn = epoch;
        int daySubmitted = this.submissionTime.getDayOfWeek().getValue();
        int dueDay = time.getDayOfWeek().getValue();
        if (dueDay == 6 || dueDay == 7 || dueDay < daySubmitted) {
            adjustment = SECONDS_PER_WEEKEND;
        }
        toReturn += adjustment;
        return toReturn;
    }

    LocalDateTime getDateTimeFromEpoch(long epoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.systemDefault());
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        if (turnaroundTime < 0) {
            System.out.println("Please enter a positive integer");
            this.error = true;
        } else this.turnaroundTime = turnaroundTime;
    }

    public boolean isError() {
        return error;
    }

}
