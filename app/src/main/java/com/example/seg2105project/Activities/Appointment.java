package com.example.seg2105project.Activities;

import com.example.seg2105project.Users.Doctor;
import com.example.seg2105project.Users.Patient;
import com.example.seg2105project.Users.User;

public class Appointment implements Listable {
    private String doctorID,patientID,appointmentID, shiftID;
    private long dateAndTime;

    public Appointment(String patientID, String doctorID, long dateAndTime){
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.dateAndTime = dateAndTime;
    }
    public Appointment(){
        this.patientID ="";
        this.dateAndTime =0;
        this.doctorID = "";
    }


    public long getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(long dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public void setAppointmentID(String key) {
        appointmentID = key;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getShiftID() {
        return shiftID;
    }

    public void setShiftID(String shiftID) {
        this.shiftID = shiftID;
    }
}


