package com.example.hscheduler;

public class Appointment {

    private String DateAppointment;
    private String TimeAppointment;

    public Appointment(){

    }

    public String getDateAppointment() {
        return DateAppointment;
    }

    public void setDateAppointment(String dateAppointment) {
        DateAppointment = dateAppointment;
    }

    public String getTimeAppointment() {
        return TimeAppointment;
    }

    public void setTimeAppointment(String timeAppointment) {
        TimeAppointment = timeAppointment;
    }

    public Appointment(String dateAppointment, String timeAppointment) {
        DateAppointment = dateAppointment;
        TimeAppointment = timeAppointment;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "DateAppointment='" + DateAppointment + '\'' +
                ", TimeAppointment='" + TimeAppointment + '\'' +
                '}';
    }
}
