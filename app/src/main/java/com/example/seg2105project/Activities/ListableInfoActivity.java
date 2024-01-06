package com.example.seg2105project.Activities;

import static android.content.ContentValues.TAG;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seg2105project.ExternalAssistance.GMail;
import com.example.seg2105project.R;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ListableInfoActivity extends AppCompatActivity {
    TextView txt_first, txt_second, txt_third, txt_fourth, txt_fifth, txt_sixth, txt_seventh;
    ImageView img_approve, img_refuse;
    Listable toDisplay;
    String state = GenericListActivity.state;
    RatingBar ratingBar;
    private static final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listable_info);
        txt_first = findViewById(R.id.txt_first);
        txt_second = findViewById(R.id.txt_second);
        txt_third =findViewById(R.id.txt_third);
        txt_fourth = findViewById(R.id.txt_fourth);
        txt_fifth = findViewById(R.id.txt_fifth);
        txt_sixth = findViewById(R.id.txt_sixth);
        txt_seventh  = findViewById(R.id.txt_seventh);
        img_approve = findViewById(R.id.img_approve);
        img_refuse = findViewById(R.id.img_refuse);
        toDisplay = GenericListActivity.currentListable;
        ratingBar = findViewById(R.id.ratingBar);
        switch (state) {
            case "Delete Doctor":
            case "Delete Patient":
            case "User Requests":
            case "Refused User Requests":
                NonAdmin user = (NonAdmin) toDisplay ;
                txt_first.setVisibility(View.VISIBLE);
                txt_first.setText(user.getEmail());
                txt_second.setVisibility(View.VISIBLE);
                txt_second.setText(user.getFirstName());
                txt_third.setVisibility(View.VISIBLE);
                txt_third.setText(user.getLastName());
                txt_fourth.setVisibility(View.VISIBLE);
                txt_fourth.setText(user.getPhoneNumber());
                txt_fifth.setVisibility(View.VISIBLE);
                txt_fifth.setText(user.getAddress());
                txt_sixth.setVisibility(View.VISIBLE);
                img_approve.setVisibility(View.VISIBLE);
                if (user.getRole().equals("Doctor")) {
                    txt_sixth.setText(((Doctor) user).getEmployeenumber());
                    txt_seventh.setVisibility(View.VISIBLE);
                    txt_seventh.setText(((Doctor) user).getSpecialties());
                } else {
                    txt_sixth.setText(((Patient) user).getHealthcardnumber());
                }
                if (state.equals("User Requests")) {
                    img_refuse.setVisibility(View.VISIBLE);
                }
                if(state.startsWith("Delete")){
                    img_refuse.setVisibility(View.VISIBLE);
                    img_approve.setVisibility(View.GONE);
                }
                break;
            case "Doctor Upcoming Appointments":
            case "Doctor Past Appointments": {
                Appointment appointmentToDisplay = (Appointment) toDisplay;
                if (state.equals("Doctor Upcoming Appointments")) img_refuse.setVisibility(View.VISIBLE);
                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue(Patient.class) != null){
                            Patient patient = snapshot.getValue(Patient.class);
                            txt_first.setVisibility(View.VISIBLE);
                            txt_first.setText(patient.getEmail());
                            txt_second.setVisibility(View.VISIBLE);
                            txt_second.setText(patient.getFirstName());
                            txt_third.setVisibility(View.VISIBLE);
                            txt_third.setText(patient.getLastName());
                            txt_fourth.setVisibility(View.VISIBLE);
                            txt_fourth.setText(patient.getPhoneNumber());
                            txt_fifth.setVisibility(View.VISIBLE);
                            txt_fifth.setText(patient.getAddress());
                            txt_sixth.setVisibility(View.VISIBLE);
                            txt_sixth.setText(patient.getHealthcardnumber());
                            txt_seventh.setVisibility(View.VISIBLE);
                            txt_seventh.setText(new Date(appointmentToDisplay.getDateAndTime()).toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                myRef.child("Users").child("Patients").child(appointmentToDisplay.getPatientID()).addValueEventListener(listener);

                break;
            }
            case "Patient Upcoming Appointments":
            case "Patient Past Appointments": {
                Appointment appointmentToDisplay = (Appointment) toDisplay;
                if (state.equals("Patient Upcoming Appointments")) img_refuse.setVisibility(View.VISIBLE);
                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("Users").child("Doctors").child(appointmentToDisplay.getDoctorID()).exists()
                                && snapshot.child("Users").child("Doctors").child(appointmentToDisplay.getDoctorID()).getValue(Doctor.class) != null){
                            Doctor doctor = snapshot.child("Users").child("Doctors").child(appointmentToDisplay.getDoctorID()).getValue(Doctor.class);
                            txt_first.setVisibility(View.VISIBLE);
                            txt_first.setText(doctor.getEmail());
                            txt_second.setVisibility(View.VISIBLE);
                            txt_second.setText(doctor.getFirstName());
                            txt_third.setVisibility(View.VISIBLE);
                            txt_third.setText(doctor.getLastName());
                            txt_fourth.setVisibility(View.VISIBLE);
                            txt_fourth.setText(doctor.getPhoneNumber());
                            txt_fifth.setVisibility(View.VISIBLE);
                            txt_fifth.setText(doctor.getAddress());
                            txt_sixth.setVisibility(View.VISIBLE);
                            txt_sixth.setText(doctor.getEmployeenumber());
                            txt_seventh.setVisibility(View.VISIBLE);
                            txt_seventh.setText(new Date(appointmentToDisplay.getDateAndTime()).toString());
                            if(state.equals("Patient Past Appointments")){
                                Button btn_rating = findViewById(R.id.btn_rating);
                                ratingBar.setVisibility(View.VISIBLE);
                                btn_rating.setVisibility(View.VISIBLE);
                                if(snapshot.child("Appointments").child(appointmentToDisplay.getAppointmentID()).child("rating").exists())
                                    ratingBar.setRating(snapshot.child("Appointments").child(appointmentToDisplay.getAppointmentID()).child("rating").getValue(Float.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                myRef.addListenerForSingleValueEvent(listener);

                break;
            }
            case "Shifts":
                Shift shiftToDisplay = (Shift) toDisplay;
                txt_first.setVisibility(View.VISIBLE);
                txt_first.setText(new Date(shiftToDisplay.getStartTime()).toString());
                txt_second.setVisibility(View.VISIBLE);
                txt_second.setText(new Date(shiftToDisplay.getEndTime()).toString());
                if (!shiftToDisplay.getShiftAppointment().isEmpty()) {
                    txt_third.setVisibility(View.VISIBLE);
                    txt_third.setText("Appointment booked during shift.");
                } else {
                    img_refuse.setVisibility(View.VISIBLE);
                }
                break;
            case "Appointment Requests": {
                Appointment appointmentToDisplay = (Appointment) toDisplay;
                img_approve.setVisibility(View.VISIBLE);
                img_refuse.setVisibility(View.VISIBLE);
                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue(Patient.class) != null){
                            Patient patient = snapshot.getValue(Patient.class);
                            txt_first.setVisibility(View.VISIBLE);
                            txt_first.setText(patient.getEmail());
                            txt_second.setVisibility(View.VISIBLE);
                            txt_second.setText(patient.getFirstName());
                            txt_third.setVisibility(View.VISIBLE);
                            txt_third.setText(patient.getLastName());
                            txt_fourth.setVisibility(View.VISIBLE);
                            txt_fourth.setText(patient.getPhoneNumber());
                            txt_fifth.setVisibility(View.VISIBLE);
                            txt_fifth.setText(patient.getAddress());
                            txt_sixth.setVisibility(View.VISIBLE);
                            txt_sixth.setText(patient.getHealthcardnumber());
                            txt_seventh.setVisibility(View.VISIBLE);
                            txt_seventh.setText(new Date(appointmentToDisplay.getDateAndTime()).toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                myRef.child("Users").child("Patients").child(appointmentToDisplay.getPatientID()).addValueEventListener(listener);
                break;
            }
            default:
                finish();
                break;
        }
    }
    public void approve(View view){
        if(state.equals("User Requests")|| state.equals("Refused User Requests")){
            if(state.equals("User Requests")){
                DatabaseHelper.approveUserRequest(((NonAdmin) toDisplay).getId());
            }
            else DatabaseHelper.approveRefusedUserRequest(((NonAdmin) toDisplay).getId());
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        ArrayList<String> emailToSend = new ArrayList<>();
                        emailToSend.add(((User)toDisplay).getEmail());
                        GMail email = new GMail("seg210514@gmail.com", "wmvkwvajbjgbanla", emailToSend, "Accepted", "Your registration request has been approved");
                        email.createEmailMessage();
                        email.sendEmail();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
        else if (state.equals("Appointment Requests")) DatabaseHelper.approveAppointmentRequest((Appointment)toDisplay);
        finish();
    }
    public void refuse(View view){
        if(state.equals("User Requests")){
            DatabaseHelper.refuseUserRequest(((NonAdmin) toDisplay).getId());
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ArrayList<String> emailToSend = new ArrayList<>();
                    emailToSend.add(((User)toDisplay).getEmail());
                    GMail email = new GMail("seg210514@gmail.com", "wmvkwvajbjgbanla", emailToSend, "Refused", "Your registration request has been refused.");
                    email.createEmailMessage();
                    email.sendEmail();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        }
        else if (state.equals("Appointment Requests")) DatabaseHelper.refuseAppointmentRequest((Appointment)toDisplay);
        else if (state.equals("Doctor Upcoming Appointments")) DatabaseHelper.cancelAppointment((Appointment)toDisplay);
        else if (state.equals("Patient Upcoming Appointments") && MILLISECONDS.toMinutes(((Appointment)toDisplay).getDateAndTime()-System.currentTimeMillis()) > 60) DatabaseHelper.cancelAppointment((Appointment)toDisplay);
        else if (state.equals("Patient Upcoming Appointments") && MILLISECONDS.toMinutes(((Appointment)toDisplay).getDateAndTime()-System.currentTimeMillis()) < 60) Toast.makeText(this,"Cancellation period expired.",Toast.LENGTH_LONG).show();
        else if (state.equals("Shifts")) DatabaseHelper.deleteShift((Shift)toDisplay);
        else if(state.equals("Delete Patient")) DatabaseHelper.deletePatient((Patient)toDisplay);
        else if(state.equals("Delete Doctor")) DatabaseHelper.deleteDoctor((Doctor)toDisplay);
        finish();
    }
    public void rate(View view){
        DatabaseHelper.submitRating(((Appointment)toDisplay),ratingBar.getRating());
        Toast.makeText(this,"Rating submitted!",Toast.LENGTH_LONG).show();
    }
}