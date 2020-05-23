package com.example.devicesilencingapp.time.modal;

public class ScheduleModal {
    long id;
    String start_time;
    String end_time;
    String dates;
    boolean status;

    public ScheduleModal() {

    }
    public ScheduleModal(long id, String start_time, String end_time, String dates, boolean status) {
        this.id = id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.dates = dates;
        this.status = status;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }



}
