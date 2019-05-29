package com.pugtools.fcalendar.data;

public class Day {
    private int mYear;
    private int mMonth;
    private int mDay;
    private DayStatus dayStatus = new DayStatus();

    public Day(int year, int month, int day) {
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
    }

    public Day(int year, int month, int day, DayStatus dayStatus) {
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.dayStatus = dayStatus;
    }

    public DayStatus getDayStatus() {
        return dayStatus;
    }

    public void setDayStatus(DayStatus dayStatus) {
        this.dayStatus = dayStatus;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public int getDay() {
        return mDay;
    }


}
