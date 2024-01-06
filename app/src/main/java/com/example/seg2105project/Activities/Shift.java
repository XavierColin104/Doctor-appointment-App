package com.example.seg2105project.Activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Shift implements Listable{
    private  String display;
    private String shiftID;

    private long startTime;
    private long endTime;
    //We will calculate the number of shifts that can fit between start time and end time then initialize the array
    //to contain that number of slots
    private ArrayList<Appointment> shiftAppointments = new ArrayList<>();;
    private String doctorID;
    public Shift(){
        this.startTime = 0;
        this.endTime = 0;
        this.doctorID = "";
        this.shiftID = "";
        shiftAppointments = null;
    }

    public Shift(Date startTimeAndDate, Date endTimeAndDate) {
        this.startTime = startTimeAndDate.getTime() ;
        this.endTime = endTimeAndDate.getTime();
    }
    public Shift(Date startTimeAndDate, Date endTimeAndDate, String doctorID, String shiftID) {
        this.startTime = startTimeAndDate.getTime() ;
        this.endTime = endTimeAndDate.getTime();
        this.doctorID = doctorID;
        this.shiftID = shiftID;
    }
    public Shift(long startTime, long endTime, String doctorID, String shiftID,String display) {
        this.startTime = startTime ;
        this.endTime = endTime;
        this.doctorID = doctorID;
        this.shiftID = shiftID;
        this.display = display;
    }
    public Shift(long startTime, long endTime, String shiftID) {
        this.startTime = startTime ;
        this.endTime = endTime;
        this.shiftID = shiftID;
    }
    public Shift(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }



    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public ArrayList<Appointment> getShiftAppointment() {
        return shiftAppointments;
    }

    public void setShiftAppointment(Collection<Appointment> shiftAppointments) {
        this.shiftAppointments = new ArrayList<Appointment>(shiftAppointments);
    }
    public void addAppointment(Appointment appointment){shiftAppointments.add(appointment);}

    public String getShiftID() {
        return shiftID;
    }

    public void setShiftID(String shiftID) {
        this.shiftID = shiftID;
    }


    public String getDisplay() {
        return display;
    }
}
