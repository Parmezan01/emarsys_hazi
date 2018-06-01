package com.progmasters.emarsys_hazi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DueDateTest {

    private DueDate dueDate;
    private LocalDateTime time;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PartitionedTime partitionedTime;
    private LocalDateTime finalDate;
    private DateAndTime dateAndTime;

    @Before
    public void init() {
        this.dueDate = new DueDate();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void settingDateAndTimeTest() {
        dueDate.setSubmissionTime(new DateAndTime(2018, 2, 22, 22, 22));
        time = LocalDateTime.of(2018, 2, 22, 22, 22);
        assertTrue(time.isEqual(dueDate.getSubmissionTime()));
    }

    @Test
    public void settingInvalidDateAndTimeTest() {
        dueDate.setSubmissionTime(new DateAndTime(1999, 22, 22, 22, 22));
        assertEquals("Invalid value for MonthOfYear (valid values 1 - 12): 22" + System.lineSeparator(), outContent.toString());
        assertTrue(dueDate.isError());
    }

    @Test
    public void settingQueryOutOfWorkingHours() {
        dueDate.setSubmissionTime(new DateAndTime(2018, 6, 1, 6, 22));
        assertEquals("Please submit your query during working hours: Monday to Friday, 09:00 - 17:00" + System.lineSeparator(), outContent.toString());
        assertTrue(dueDate.isError());
    }

    @Test
    public void settingQueryOutOfWorkingDays() {
        dueDate.setSubmissionTime(new DateAndTime(2018, 6, 2, 9, 22));
        assertEquals("Please submit your query during working hours: Monday to Friday, 09:00 - 17:00" + System.lineSeparator(), outContent.toString());
        assertTrue(dueDate.isError());
    }

    @Test
    public void settingTurnaroundTimeTest() {
        dueDate.setTurnaroundTime(8);
        assertEquals(8, dueDate.getTurnaroundTime());
    }

    @Test
    public void settingWrongTurnaroundTimeTest() {
        dueDate.setTurnaroundTime(-8);
        assertEquals("Please enter a positive integer" + System.lineSeparator(), outContent.toString());
        assertTrue(dueDate.isError());
    }

    @Test
    public void timePartitioningTest() {
        partitionedTime = new PartitionedTime(50);
        assertEquals(1, partitionedTime.getWeeks());
        assertEquals(1, partitionedTime.getDays());
        assertEquals(2, partitionedTime.getHours());
    }

    @Test
    public void checkDateAdjustmentForSaturday() {
        dueDate.setSubmissionTime(new DateAndTime(2018, 6, 1, 15, 22));
        time = LocalDateTime.of(2018, 6, 2, 15, 22);
        long timeEpoch = dueDate.getEpochTime(time);
        assertEquals((timeEpoch + 172800), dueDate.adjustForWeekend(timeEpoch));
    }

    @Test
    public void checkDateAdjustmentForSunday() {
        dueDate.setSubmissionTime(new DateAndTime(2018, 6, 1, 15, 22));
        time = LocalDateTime.of(2018, 6, 3, 15, 22);
        long timeEpoch = dueDate.getEpochTime(time);
        assertEquals((timeEpoch + 172800), dueDate.adjustForWeekend(timeEpoch));
    }

    @Test
    public void checkDateAdjustmentForOutOfWorkingHours() {
        time = LocalDateTime.of(2018, 6, 4, 17, 22);
        long timeEpoch = dueDate.getEpochTime(time);
        assertEquals((timeEpoch + 57600), dueDate.adjustForOutOfWorkTime(timeEpoch));
    }

    @Test
    public void checkDateAdjustmentForPassingWeekend() {
        dueDate.setSubmissionTime(new DateAndTime(2018, 6, 1, 15, 22));
        time = LocalDateTime.of(2018, 6, 4, 15, 22);
        long timeEpoch = dueDate.getEpochTime(time);
        assertEquals((timeEpoch + 172800), dueDate.adjustForWeekend(timeEpoch));
    }

    @Test
    public void testDueDateForSameDay() {
        time = LocalDateTime.of(2018, 6, 1, 12, 0);
        finalDate = dueDate.calculateDueDate(new DateAndTime(2018, 6, 1, 10, 0), 2);
        assertEquals(time, finalDate);
    }

    @Test
    public void testDueDateForNextDayWithoutPassingWeekends() {
        time = LocalDateTime.of(2018, 6, 1, 12, 0);
        finalDate = dueDate.calculateDueDate(new DateAndTime(2018, 5, 31, 10, 0), 10);
        assertEquals(time, finalDate);
    }

    @Test
    public void testDueDateForAnotherDayPassingOneWeekend() {
        time = LocalDateTime.of(2018, 6, 6, 12, 0);
        finalDate = dueDate.calculateDueDate(new DateAndTime(2018, 6, 1, 10, 0), 26);
        assertEquals(time, finalDate);
    }

    @Test
    public void testDueDateForLandingOnWeekend() {
        time = LocalDateTime.of(2018, 6, 4, 12, 0);
        finalDate = dueDate.calculateDueDate(new DateAndTime(2018, 6, 1, 10, 0), 10);
        assertEquals(time, finalDate);
    }

    @Test
    public void testDueDateForExactlyFortyHours() {
        time = LocalDateTime.of(2018, 6, 8, 10, 0);
        finalDate = dueDate.calculateDueDate(new DateAndTime(2018, 6, 1, 10, 0), 40);
        assertEquals(time, finalDate);
    }

    @Test
    public void testDueDateTimeWhenCrossingMultipleWeekends() {
        time = LocalDateTime.of(2018, 6, 22, 12, 0);
        finalDate = dueDate.calculateDueDate(new DateAndTime(2018, 6, 1, 10, 0), 122);
        assertEquals(time, finalDate);
    }

    @Test
    public void testDueDateTimeWhenCrossingToNextMonth() {
        time = LocalDateTime.of(2018, 7, 2, 12, 0);
        finalDate = dueDate.calculateDueDate(new DateAndTime(2018, 6, 1, 10, 0), 170);
        assertEquals(time, finalDate);
    }


    @After
    public void cleanup() throws IOException {
        System.setOut(null);
        outContent.close();
    }

}
