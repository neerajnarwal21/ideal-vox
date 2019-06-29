package com.pugtools.fcalendar.data;

public class DayStatus {

    private DayEnum day = DayEnum.AVAILABLE;
    private NightEnum night = NightEnum.AVAILABLE;

    public void setDay(DayEnum day) {
        this.day = day;
    }

    public DayEnum getDay() {
        return day;
    }

    public void setNight(NightEnum night) {
        this.night = night;
    }

    public NightEnum getNight() {
        return night;
    }

    public enum DayEnum {
        AVAILABLE,
        UNAVAILABLE,
        PENDING,
        CONFIRMED,
        BOOKED,
        REJECT
    }

    public enum NightEnum {
        AVAILABLE,
        UNAVAILABLE,
        PENDING,
        CONFIRMED,
        BOOKED,
        REJECT
    }
}