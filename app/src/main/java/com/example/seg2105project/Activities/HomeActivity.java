package com.example.seg2105project.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.seg2105project.R;
import com.example.seg2105project.Users.Administrator;
import com.example.seg2105project.Users.Doctor;
import com.example.seg2105project.Users.Patient;
import com.example.seg2105project.Users.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {
    public static boolean autoAccepts;
    // View variables
    private Button  btn_logout, btn_primary,btn_secondary,btn_tertiary,btn_fourth ;
    private TextView lbl_welcome , txt_patientReq, txt_requestDisplay;
    private ImageButton btn_search;
    private RadioGroup rbg_searchType;
    public static Switch swt_autoaccept;
    public static String searchType = "Specialty";

    // User object to be passed between activities
    public static User user;
    private boolean isRejected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rbg_searchType = findViewById(R.id.rbg_searchType);
        lbl_welcome = findViewById(R.id.lbl_welcome);
        btn_logout = findViewById(R.id.btn_logout);
        btn_primary = findViewById(R.id.btn_primary);
        btn_secondary = findViewById(R.id.btn_secondary);
        btn_tertiary = findViewById(R.id.btn_tertiary);
        btn_fourth = findViewById(R.id.btn_fourth);
        swt_autoaccept = findViewById(R.id.swt_autoaccept);
        user = (User) getIntent().getSerializableExtra("sessionUser");
        isRejected = getIntent().getExtras().getBoolean("isRejected");
        txt_patientReq = findViewById(R.id.txt_patientReq);
        txt_requestDisplay = findViewById(R.id.txt_requestDisplay);
        btn_search = findViewById(R.id.btn_search);




        if (user != null) {
            String msg = "";
            btn_primary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(user.getRole().equals("Doctor")) seeDoctorUpcomingAppointments();
                    else if(user.getRole().equals("Admin")) seeRequests();
                    else if (user.getRole().equals("Patient")) seePatientUpcomingAppointments();
                }
            });
            btn_secondary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(user.getRole().equals("Doctor")) seeDoctorPastAppointments();
                    else if(user.getRole().equals("Admin")) seeRejectedRequests();
                    else if (user.getRole().equals("Patient")) seePatientPastAppointments();
                }
            });
            btn_fourth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(user.getRole().equals("Doctor")) seeAppointmentRequests();

                }
            });
            if (user.getRole().equals("Admin")) {
                btn_primary.setVisibility(View.VISIBLE);
                btn_secondary.setVisibility(View.VISIBLE);
                btn_primary.setText("See Requests");
                btn_secondary.setText("See Rejected Requests");
                btn_tertiary.setVisibility(View.VISIBLE);
                btn_tertiary.setText("Delete Patient");
                btn_fourth.setVisibility(View.VISIBLE);
                btn_fourth.setText("Delete Doctor");
                btn_tertiary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePatient();
                    }
                });
                btn_fourth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDoctor();
                    }
                });
                msg = "Welcome, you are an Administrator";
            }
            else if (user.getRole().equals("Doctor")){
                btn_primary.setVisibility(View.VISIBLE);
                btn_secondary.setVisibility(View.VISIBLE);
                Doctor doctor = (Doctor) user;
                btn_primary.setText("See Upcoming Appointments");
                btn_secondary.setText("See Past Appointments");
                btn_tertiary.setVisibility(View.VISIBLE);
                btn_tertiary.setText("Manage Shifts");
                btn_fourth.setVisibility(View.VISIBLE);
                btn_fourth.setText("See Appointment Requests");
                swt_autoaccept.setVisibility(View.VISIBLE);
                DatabaseHelper.autoAccepts(((Doctor) user).getId());
                btn_tertiary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       seeShifts();
                    }
                });
                swt_autoaccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (swt_autoaccept.isChecked())
                            DatabaseHelper.setAutoAccept(true);
                        else DatabaseHelper.setAutoAccept(false);

                    }
                });


                msg = "Welcome " + doctor.getFirstName() + " " + doctor.getLastName() + ", you are a Doctor.";

            }
            else if(user.getRole().equals("Patient")){
                btn_primary.setVisibility(View.VISIBLE);
                btn_secondary.setVisibility(View.VISIBLE);
                Patient patient = (Patient) user;
                btn_primary.setText("See Upcoming Appointments");
                btn_secondary.setText("See Past Appointments");
                txt_patientReq.setVisibility(View.VISIBLE);
                txt_requestDisplay.setVisibility(View.VISIBLE);
                btn_search.setVisibility(View.VISIBLE);
                rbg_searchType.setVisibility(View.VISIBLE);
                btn_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestAppointment();

                    }
                });
                rbg_searchType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                        if (checkedID == R.id.rb_specialty) {
                            searchType = "Specialty";
                            txt_patientReq.setHint("Specialty");

                        } else {
                            searchType = "Doctor";
                            txt_patientReq.setHint("First Name,Last Name");
                        }
                    }
                });
                msg = "Welcome " + patient.getFirstName() + " " + patient.getLastName() + ", you are a Patient.";
            }

            lbl_welcome.setText(msg);

        } else {
            if (isRejected) {
                lbl_welcome.setText("Rejected, sorry. Call 123-456-7890.");
            } else {
                lbl_welcome.setText("Pending approval.");
            }
        }
    }

    private void deleteDoctor() {openList("Delete Doctor");}

    private void deletePatient() {openList("Delete Patient");}

    private void requestAppointment() {openList("Request Appointment-"+txt_patientReq.getText().toString());}

    private void seePatientUpcomingAppointments() {openList("Patient Upcoming Appointments");}

    private void seePatientPastAppointments() { openList("Patient Past Appointments");}

    private void seeShifts() {
        openList("Shifts");
    }
    private void seeDoctorPastAppointments() {
        openList("Doctor Past Appointments");
    }

    private void seeDoctorUpcomingAppointments() {
        openList("Doctor Upcoming Appointments");
    }

    private void seeRequests() {
        openList("User Requests");
    }
    private void seeRejectedRequests() {
        openList("Refused User Requests");
    }

    private void seeAppointmentRequests(){openList("Appointment Requests");}
    public static void switchVal(boolean status){
        swt_autoaccept.setChecked(status);
    }
    public void onLogoutClick(View view){
        this.user = null;
        LoginActivity.user = null;
        DatabaseHelper.deleteUser();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        HomeActivity.this.startActivity(intent);
    }
    private void openList(String state){
        GenericListActivity.state = state;
        Intent intent = new Intent(HomeActivity.this, GenericListActivity.class);
        HomeActivity.this.startActivity(intent);
    }
}