package com.example.seg2105project.Activities;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.seg2105project.Users.Administrator;
import com.example.seg2105project.Users.Doctor;
import com.example.seg2105project.Users.NonAdmin;
import com.example.seg2105project.Users.Patient;
import com.example.seg2105project.Users.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DatabaseHelper {
    private static FirebaseAuth mAuth;

    private static final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    public static User sessionUser;
    public static boolean userRejected;




    public static void approveUserRequest(String ID) {
        DatabaseReference requestRef = myRef.child("User Requests").child(ID);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NonAdmin temp;
                    if(dataSnapshot.child("role").getValue(String.class).equals("Patient")){
                        temp = (dataSnapshot.getValue(Patient.class));
                        myRef.child("Users").child("Patients").child(ID).setValue(temp);
                    }
                    else{
                        temp = (dataSnapshot.getValue(Doctor.class));
                        myRef.child("Users").child("Doctors").child(ID).setValue(temp);
                    }
                    myRef.child("User Requests").child(ID).removeValue();


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }
        };
        requestRef.addValueEventListener(listener);


    }
    public static void approveRefusedUserRequest(String ID) {
        DatabaseReference requestRef = myRef.child("Refused User Requests").child(ID);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NonAdmin temp = null;
                    if(dataSnapshot.child("role").getValue(String.class).equals("Patient")){
                        temp = (dataSnapshot.getValue(Patient.class));
                        myRef.child("Users").child("Patients").child(ID).setValue(temp);
                    }
                    else{
                        temp = (dataSnapshot.getValue(Doctor.class));
                        myRef.child("Users").child("Doctors").child(ID).setValue(temp);
                    }
                    myRef.child("Refused User Requests").child(ID).removeValue();


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }
        };
        requestRef.addValueEventListener(listener);


    }
    public static void refuseUserRequest(String ID) {
        DatabaseReference requestRef = myRef.child("User Requests").child(ID);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NonAdmin temp;
                    if(dataSnapshot.child("role").getValue(String.class).equals("Patient")){
                        temp = dataSnapshot.getValue(Patient.class);
                    }
                    else{
                        temp = dataSnapshot.getValue(Doctor.class);
                    }
                    myRef.child("User Requests").child(ID).removeValue();
                    myRef.child("Refused User Requests").child(ID).setValue(temp);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }
        };
        requestRef.addValueEventListener(listener);


    }

    public static void getUser(String ID) {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if(dataSnapshot.child("Patients").child(ID).exists()
                                && dataSnapshot.child("Patients").child(ID).getValue(Patient.class) != null){
                            sessionUser = dataSnapshot.child("Patients").child(ID).getValue(Patient.class);
                        }
                        else if(dataSnapshot.child("Doctors").child(ID).exists()
                        && dataSnapshot.child("Doctors").child(ID).getValue(Doctor.class) != null){
                            sessionUser = dataSnapshot.child("Doctors").child(ID).getValue(Doctor.class);
                        }
                        else if(dataSnapshot.child("Admins").child(ID).exists()
                        && dataSnapshot.child("Admins").child(ID).getValue(Administrator.class) != null){
                            sessionUser = dataSnapshot.child("Admins").child(ID).getValue(Administrator.class);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "DID NOT READ DATA");
                }

            };
        myRef.child("Users").addValueEventListener(listener);
    }


    public static User returnUser() {
        return sessionUser;
    }


    public static void deleteUser(){
        sessionUser = null;

    }

    public static void writeRequest(NonAdmin user) {
        myRef.child("User Requests").child(user.getId()).setValue(user);
    }

    public static void getRejected(String ID) {
        userRejected = false;
        DatabaseReference userRef = myRef.child("Refused User Requests").child(ID);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    userRejected = true;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }

        };
        userRef.addValueEventListener(listener);
    }

    public static boolean isRejected() {
        return userRejected;
    }

    public static void approveAppointmentRequest(Appointment appointmentToApprove) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()
                        && dataSnapshot.child("Appointment Requests").child(appointmentToApprove.getDoctorID()).child(appointmentToApprove.getPatientID()).child(appointmentToApprove.getAppointmentID()).exists()) {

                    myRef.child("Appointment Requests").child(appointmentToApprove.getDoctorID()).child(appointmentToApprove.getPatientID()).child(appointmentToApprove.getAppointmentID()).removeValue();
                    appointmentToApprove.setAppointmentID(null);
                    String key = myRef.child("Appointments").push().getKey();
                    myRef.child("Appointments").child(key).setValue(appointmentToApprove);
                    myRef.child("Patient Appointments").child(appointmentToApprove.getPatientID()).child(key).setValue(appointmentToApprove.getDateAndTime());
                    myRef.child("Doctor Appointments").child(appointmentToApprove.getDoctorID()).child(key).setValue(appointmentToApprove.getDateAndTime());
                    myRef.child("Shifts").child(appointmentToApprove.getDoctorID()).child(appointmentToApprove.getShiftID()).child("Appointments").child(key).setValue(appointmentToApprove.getDateAndTime());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }

        };
        myRef.addListenerForSingleValueEvent(listener);

    }

    public static void refuseAppointmentRequest(Appointment appointmentToRefuse) {
        myRef.child("Appointment Requests").child(appointmentToRefuse.getDoctorID()).child(appointmentToRefuse.getPatientID()).child(appointmentToRefuse.getAppointmentID()).removeValue();
    }

    public static void deleteShift(Shift shiftToDelete) {
        myRef.child("Shifts").child(((Doctor) HomeActivity.user).getId()).child(shiftToDelete.getShiftID()).removeValue();
    }
    public static void writeShift(Shift shiftToWrite){
       myRef.child("Shifts").child(((Doctor) HomeActivity.user).getId()).push().setValue(shiftToWrite);
    }

    public static void cancelAppointment(Appointment appointmentToCancel) {
        myRef.child("Appointments").child(appointmentToCancel.getAppointmentID()).removeValue();
        myRef.child("Patient Appointments").child(appointmentToCancel.getPatientID()).child(appointmentToCancel.getAppointmentID()).removeValue();
        myRef.child("Doctor Appointments").child(appointmentToCancel.getDoctorID()).child(appointmentToCancel.getAppointmentID()).removeValue();
        myRef.child("Shifts").child(appointmentToCancel.getDoctorID()).child(appointmentToCancel.getShiftID()).child("Appointments").child(appointmentToCancel.getAppointmentID()).removeValue();
    }

    public static void autoAccepts(String ID) {
        DatabaseReference appointmentsRef = myRef.child("Auto Accept Requests").child(ID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HomeActivity.autoAccepts = dataSnapshot.getValue(Boolean.class);

                }
                else{
                    HomeActivity.autoAccepts =false;
                }
                HomeActivity.switchVal(HomeActivity.autoAccepts);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }

        };
        appointmentsRef.addValueEventListener(listener);
    }
    public static void setAutoAccept(boolean preference) {
        myRef.child("Auto Accept Requests").child(((Doctor) HomeActivity.user).getId()).setValue(preference);
    }
    public static void writeAppointmentRequest(Appointment appointmentToWrite){
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean good = true;
                    for(DataSnapshot request : dataSnapshot.child("Appointment Requests").child(appointmentToWrite.getDoctorID()).child(appointmentToWrite.getPatientID()).getChildren()){
                        if(request.child("dateAndTime").exists()
                        && request.child("dateAndTime").getValue(Long.class) != null
                        && request.child("dateAndTime").getValue(Long.class) == appointmentToWrite.getDateAndTime()){
                            good = false;
                            break;
                        }
                    }
                    if (good) {
                        String key = myRef.child("Appointment Requests").child(appointmentToWrite.getDoctorID()).child(appointmentToWrite.getPatientID()).push().getKey();
                        myRef.child("Appointment Requests").child(appointmentToWrite.getDoctorID()).child(appointmentToWrite.getPatientID()).child(key).setValue(appointmentToWrite);
                        if(dataSnapshot.child("Auto Accept Requests").child(appointmentToWrite.getDoctorID()).exists() && (dataSnapshot.child("Auto Accept Requests").child(appointmentToWrite.getDoctorID()).getValue(Boolean.class) == true)){
                            appointmentToWrite.setAppointmentID(key);
                            DatabaseHelper.approveAppointmentRequest(appointmentToWrite);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }
        };
        myRef.addListenerForSingleValueEvent(listener);
    }

    public static void deleteOutdatedShifts() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot doctorShifts: dataSnapshot.getChildren()){
                        for(DataSnapshot shift : doctorShifts.getChildren()){
                                if(shift.child("startTime").exists()
                                        && shift.child("startTime").getValue(Long.class) != null
                                && new Date(shift.child("startTime").getValue(Long.class)).before(new Date()))
                                    myRef.child("Shifts").child(doctorShifts.getKey()).child(shift.getKey()).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }
        };
        myRef.child("Shifts").addListenerForSingleValueEvent(listener);
    }

    public static void deletePatient(Patient patient) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot appointment: dataSnapshot.child("Patient Appointments").child(patient.getId()).getChildren()){
                        Appointment appointmentRef = dataSnapshot.child("Appointments").child(appointment.getKey()).getValue(Appointment.class);
                        if(appointmentRef != null){
                            appointmentRef.setAppointmentID(appointment.getKey());
                            cancelAppointment(appointmentRef);
                        }
                    }
                    myRef.child("Users").child("Patients").child(patient.getId()).removeValue();
                    for(DataSnapshot doctorAppReq: dataSnapshot.child("Appointment Requests").getChildren()){
                        if(dataSnapshot.child("Appointment Requests").child(doctorAppReq.getKey()).child(patient.getId()).exists())
                            myRef.child("Appointment Requests").child(doctorAppReq.getKey()).child(patient.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }
        };
        myRef.addListenerForSingleValueEvent(listener);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(patient.getEmail(),patient.getPassword()).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuth.getCurrentUser().delete();
                } else {

                }
            }
        });
    }

    public static void deleteDoctor(Doctor doctor) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot appointment: dataSnapshot.child("Doctor Appointments").child(doctor.getId()).getChildren()){
                        Appointment appointmentRef = dataSnapshot.child("Appointments").child(appointment.getKey()).getValue(Appointment.class);
                        if(appointmentRef != null){
                            appointmentRef.setAppointmentID(appointment.getKey());
                            cancelAppointment(appointmentRef);
                        }
                    }
                    myRef.child("Users").child("Doctors").child(doctor.getId()).removeValue();
                    myRef.child("Auto Accept Requests").child(doctor.getId()).removeValue();
                    myRef.child("Appointment Requests").child(doctor.getId()).removeValue();
                    myRef.child("Shifts").child(doctor.getId()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }
        };
        myRef.addListenerForSingleValueEvent(listener);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(doctor.getEmail(),doctor.getPassword()).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuth.getCurrentUser().delete();
                } else {

                }
            }
        });
    }

    public static void submitRating(Appointment appointmentToRate,float rating) {
        myRef.child("Appointments").child(appointmentToRate.getAppointmentID()).child("rating").setValue(rating);
    }
}

