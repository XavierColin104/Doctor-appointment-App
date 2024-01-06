package com.example.seg2105project.Activities;

import static android.content.ContentValues.TAG;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.Arrays;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.seg2105project.R;
import com.example.seg2105project.Users.Doctor;
import com.example.seg2105project.Users.NonAdmin;
import com.example.seg2105project.Users.Patient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GenericListActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    private static MyRecyclerViewAdapter adapter;

    public static ArrayList<Listable> list;
    private static final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    private static RecyclerView recyclerView;
    public static Listable currentListable;

    public static String state;


    private int sYear, sMonth, sDay, sHour, sMinute;

    private int eYear, eMonth, eDay, eHour, eMinute;

    private int finalSYear, finalSMonth, finalSDay, finalSHour, finalSMinute;
    private int finalEYear, finalEMonth, finalEDay, finalEHour, finalEMinute;
    public static String searchType;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recyclerview);

        // data to populate the RecyclerView with

        list = new ArrayList<>();
        // set up the RecyclerView
        recyclerView = findViewById(R.id.requestList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, list);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        Button goBackButton = findViewById(R.id.btn_GoBackRequest);
        Button shiftButton = findViewById(R.id.btn_createShift);


        if (state.equals("Shifts")) shiftButton.setVisibility(View.VISIBLE);
        shiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShiftDialog();
            }
        });
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchType = HomeActivity.searchType;
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()) {
                    if (state.equals("User Requests") || state.equals("Refused User Requests")) {
                        for (DataSnapshot dataChild : dataSnapshot.child(state).getChildren()) {
                            NonAdmin temp = null;
                            if (dataChild.exists() &&
                                     dataChild.getValue(Patient.class) != null
                                    && dataChild.child("role").exists()
                                    && !(dataChild.child("role").getValue(String.class) == null)) {
                                if (dataChild.child("role").getValue(String.class).equals("Patient"))
                                    temp = (dataChild.getValue(Patient.class));

                                else if (dataChild.child("role").getValue(String.class).equals("Doctor"))
                                    temp = (dataChild.getValue(Doctor.class));
                                }
                                temp.setId(dataChild.getKey());
                                list.add(temp);
                            }
                        } else if (state.equals("Doctor Upcoming Appointments") || state.equals("Doctor Past Appointments")) {
                        for (DataSnapshot dataChild : dataSnapshot.child("Doctor Appointments").child(((Doctor) HomeActivity.user).getId()).getChildren()) {
                            if (dataChild.exists()
                                    && dataChild.getValue(Long.class) != null
                                    && ((state.equals("Doctor Upcoming Appointments")
                                    && new Date(dataChild.getValue(Long.class)).after(new Date()))
                                    ||(state.equals("Doctor Past Appointments")
                                    && new Date(dataChild.getValue(Long.class)).before(new Date())))
                                    && dataSnapshot.child("Appointments").child(dataChild.getKey()).exists()
                                    && dataSnapshot.child("Appointments").child(dataChild.getKey()).getValue(Appointment.class) != null) {
                                Appointment temp = dataSnapshot.child("Appointments").child(dataChild.getKey()).getValue(Appointment.class);
                                temp.setAppointmentID(dataChild.getKey());
                                list.add(temp);
                            }
                        }
                    } else if (state.equals("Patient Upcoming Appointments") || state.equals("Patient Past Appointments")) {
                        for (DataSnapshot dataChild : dataSnapshot.child("Patient Appointments").child(((Patient) HomeActivity.user).getId()).getChildren()) {
                            if (dataChild.exists()
                                    && dataChild.getValue(Long.class) != null
                                    && ((state.equals("Patient Upcoming Appointments")
                                    && new Date(dataChild.getValue(Long.class)).after(new Date()))
                                    || (state.equals("Patient Past Appointments")
                                    && new Date(dataChild.getValue(Long.class)).before(new Date())))
                                    && dataSnapshot.child("Appointments").child(dataChild.getKey()).exists()
                                    && dataSnapshot.child("Appointments").child(dataChild.getKey()).getValue(Appointment.class) != null) {
                                Appointment temp = dataSnapshot.child("Appointments").child(dataChild.getKey()).getValue(Appointment.class);
                                temp.setAppointmentID(dataChild.getKey());
                                list.add(temp);

                            }
                        }
                    } else if (state.equals("Shifts")) {
                        for (DataSnapshot dataChild : dataSnapshot.child("Shifts").child(((Doctor) HomeActivity.user).getId()).getChildren()) {
                            if (dataChild.exists()
                                    && dataChild.child("startTime").exists()
                                    && dataChild.child("endTime").exists()
                                    && dataChild.child("startTime").getValue(Long.class) != null
                                    && dataChild.child("endTime").getValue(Long.class) != null) {
                                Shift temp = new Shift(dataChild.child("startTime").getValue(Long.class),
                                        dataChild.child("endTime").getValue(Long.class),
                                        dataChild.getKey());
                                if(dataChild.child("Appointments").exists()){
                                    for(DataSnapshot appointment : dataChild.child("Appointments").getChildren()){
                                        if(dataSnapshot.child("Appointments").child(appointment.getKey()).getValue(Appointment.class) != null){
                                            temp.addAppointment(dataSnapshot.child("Appointments").child(appointment.getKey()).getValue(Appointment.class));
                                        }
                                    }
                                }
                                list.add(temp);
                            }
                        }
                    } else if (state.equals("Appointment Requests")) {
                        for (DataSnapshot appShot : dataSnapshot.child("Appointment Requests").child(((Doctor) HomeActivity.user).getId()).getChildren()) {
                            for(DataSnapshot request : appShot.getChildren()) {
                                if (request.exists()
                                        && request.getValue(Appointment.class) != null) {
                                    Appointment temp = request.getValue(Appointment.class);
                                    temp.setAppointmentID(request.getKey());
                                    list.add(temp);
                                }
                            }
                        }
                        if (HomeActivity.autoAccepts){
                            for(Listable app:list){
                                DatabaseHelper.approveAppointmentRequest((Appointment) app);
                            }
                        }
                    }
                    else if(state.startsWith("Request Appointment-")){
                        for (DataSnapshot doctor : dataSnapshot.child("Users").child("Doctors").getChildren()) {
                            if (doctor.exists()
                                    && doctor.child("specialties").exists()
                                    && doctor.child("specialties").getValue(String.class) != null
                                    && doctor.child("firstName").exists()
                                    && doctor.child("firstName").getValue(String.class) != null
                                    && doctor.child("lastName").exists()
                                    && doctor.child("lastName").getValue(String.class) != null) {
                                if(searchType.equals("Specialty")) {
                                    String[] specialties = doctor.child("specialties").getValue(String.class).split(",");
                                    for (String specialty : specialties) {
                                        String specialty1 = specialty.trim();
                                        if ((specialty1).equalsIgnoreCase((state.split("-")[1]).trim())) {
                                            for (DataSnapshot shift : dataSnapshot.child("Shifts").child(doctor.getKey()).getChildren()) {
                                                HashMap<Long, Boolean> appointmentTable = new HashMap<>();
                                                if (shift.child("startTime").exists()
                                                        && shift.child("startTime").getValue(Long.class) != null
                                                        && shift.child("endTime").exists()
                                                        && shift.child("endTime").getValue(Long.class) != null) {
                                                    //How many slots we can fit
                                                    long start = shift.child("startTime").getValue(Long.class);
                                                    long end = shift.child("endTime").getValue(Long.class);
                                                    int numAppSlots = Math.toIntExact((MILLISECONDS.toMinutes(end - start) / 30));
                                                    for (int i = 0; i < numAppSlots; i++) {
                                                        appointmentTable.put(MINUTES.toMillis(MILLISECONDS.toMinutes(start)
                                                                + (30L * i)), false);
                                                    }
                                                }
                                                for (DataSnapshot appointment : dataSnapshot.child("Shifts").child(shift.getKey()).child("Appointments").getChildren()) {
                                                    if (appointment.child("dateAndTime").exists()
                                                            && appointment.child("dateAndTime").getValue(Long.class) != null) {
                                                        appointmentTable.put(appointment.child("dateAndTime").getValue(Long.class), true);
                                                    }
                                                }
                                                //Check if there are any free slots
                                                for (Boolean taken : appointmentTable.values()) {
                                                    if (!taken) {
                                                        list.add(new Shift(shift.child("startTime").getValue(Long.class), shift.child("endTime").getValue(Long.class), doctor.getKey(), shift.getKey(),
                                                                doctor.child("specialties").getValue(String.class) + " Specialist, Doctor " + doctor.child("firstName").getValue(String.class) + " " + doctor.child("lastName").getValue(String.class)));
                                                        break;
                                                    }
                                                }

                                            }
                                        }

                                    }
                                }
                                else if(searchType.equals("Doctor") && doctor.getValue(Doctor.class) != null &&
                                        (((state.split("-")[1].split(",")).length == 2 &&(doctor.child("firstName").getValue(String.class).trim().equalsIgnoreCase((state.split("-")[1].split(",")[0]).trim())
                                                || doctor.child("lastName").getValue(String.class).trim().equalsIgnoreCase((state.split("-")[1].split(",")[1]).trim())))
                                        ||((state.split("-")[1].split(",")).length == 1 &&(doctor.child("firstName").getValue(String.class).trim().equalsIgnoreCase((state.split("-")[1]).trim())
                                                || doctor.child("lastName").getValue(String.class).trim().equalsIgnoreCase((state.split("-")[1]).trim()))))){
                                    list.add(doctor.getValue(Doctor.class));
                                }
                            }
                        }
                    }
                    else if(state.equals("Delete Patient")){
                        for(DataSnapshot patient: dataSnapshot.child("Users").child("Patients").getChildren()){
                            if (patient.getValue(Patient.class)!= null) list.add(patient.getValue(Patient.class));
                        }
                    }
                    else if(state.equals("Delete Doctor")){
                        for(DataSnapshot doctor: dataSnapshot.child("Users").child("Doctors").getChildren()){
                            if (doctor.getValue(Doctor.class)!= null) list.add(doctor.getValue(Doctor.class));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                if(state.startsWith("Request Appointment-") && searchType.equals("Doctor")&& list.size()== 1) open();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }
        };
        myRef.addValueEventListener(listener);


    }
    @Override
    public void onItemClick (View view,int position){
        currentListable = adapter.getItem(position);
        Intent intent;
        if(state.startsWith("Request Appointment-")){
            intent = new Intent(this, AppointmentPicker.class);
        }
        else {
            intent = new Intent(this, ListableInfoActivity.class);
        }
        this.startActivity(intent);
    }
    public void open(){
        currentListable = adapter.getItem(0);
        this.startActivity(new Intent(this, AppointmentPicker.class));

    }

    private void showShiftDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.shift_dialog, null);
        dialogBuilder.setView(dialogView);
        final Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);


        final Button btnDatePicker = dialogView.findViewById(R.id.btn_startDate);
        final Button btnTimePicker = dialogView.findViewById(R.id.btn_startTime);
        final EditText txtDate = dialogView.findViewById(R.id.enter_startDate);
        final EditText txtTime = dialogView.findViewById(R.id.enter_startTime);

        final Button btnDatePickerEnd = dialogView.findViewById(R.id.btn_endDate);
        final Button btnTimePickerEnd = dialogView.findViewById(R.id.btn_endTime);
        final EditText txtDateEnd = dialogView.findViewById(R.id.enter_endDate);
        final EditText txtTimeEnd = dialogView.findViewById(R.id.enter_endTime);



        dialogBuilder.setTitle("Shifts");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                sYear = c.get(Calendar.YEAR);
                sMonth = c.get(Calendar.MONTH);
                sDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(GenericListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        finalSYear = year;
                        finalSMonth = monthOfYear;
                        finalSDay = dayOfMonth;
                    }
                }, sYear, sMonth, sDay);
                datePickerDialog.show();
            }
        });
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                sHour = c.get(Calendar.HOUR_OF_DAY);
                sMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(GenericListActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        txtTime.setText(hourOfDay + ":" + minute);
                        finalSHour = hourOfDay;
                        finalSMinute = minute;

                    }
                }, sHour, sMinute, false);
                timePickerDialog.show();
            }
        });

        btnDatePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                eYear = c.get(Calendar.YEAR);
                eMonth = c.get(Calendar.MONTH);
                eDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(GenericListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        txtDateEnd.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        finalEYear = year;
                        finalEMonth = monthOfYear;
                        finalEDay = dayOfMonth;
                    }
                }, eYear, eMonth, eDay);
                datePickerDialog.show();
            }
        });

        btnTimePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                eHour = c.get(Calendar.HOUR_OF_DAY);
                eMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(GenericListActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        txtTimeEnd.setText(hourOfDay + ":" + minute);
                        finalEHour = hourOfDay;
                        finalEMinute = minute;
                    }
                }, eHour, eMinute, false);
                timePickerDialog.show();

            }
        });





        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((finalSMinute != 0 && finalSMinute != 30) || (finalEMinute != 0 && finalEMinute != 30)) {
                    Toast.makeText(GenericListActivity.this, "Please enter times in 30 minute increments",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"rn");
                }

                else {
                    Date startTimeAndDate = new Date(finalSYear-1900,finalSMonth,finalSDay,finalSHour,finalSMinute);
                    Date endTimeAndDate = new Date(finalEYear-1900,finalEMonth,finalEDay,finalEHour,finalEMinute);
                    boolean conflict = false;
                    for(Listable listable : list) {
                        Shift shiftToCheck = (Shift) listable;
                        if ((//Check if the start time we picked is inside of another shift
                                startTimeAndDate.after(new Date(shiftToCheck.getStartTime()))
                                        && startTimeAndDate.before(new Date(shiftToCheck.getEndTime())))
                                //Check if end time we picked is inside of another shift
                                || (endTimeAndDate.after(new Date(shiftToCheck.getStartTime()))
                                && endTimeAndDate.before(new Date(shiftToCheck.getEndTime())))
                                //Check if another shift is inside of the shift we picked
                                || (new Date(shiftToCheck.getStartTime()).after(startTimeAndDate)
                                && new Date(shiftToCheck.getEndTime()).before(endTimeAndDate))
                                || new Date(shiftToCheck.getStartTime()).equals(startTimeAndDate)
                                && new Date(shiftToCheck.getEndTime()).equals(endTimeAndDate))
                            conflict = true;
                    }
                    if(conflict) Toast.makeText(GenericListActivity.this, "The shift you entered conflicts with other existing shifts.", Toast.LENGTH_SHORT).show();
                    else if(startTimeAndDate.before(new Date())) Toast.makeText(GenericListActivity.this, "This date has already passed.", Toast.LENGTH_SHORT).show();
                    else if (startTimeAndDate.after(endTimeAndDate)) Toast.makeText(GenericListActivity.this, "Your end date and time is before your start date and time.", Toast.LENGTH_SHORT).show();
                    else if (startTimeAndDate.equals(endTimeAndDate))Toast.makeText(GenericListActivity.this, "Your start and end date and time are identical.", Toast.LENGTH_SHORT).show();
                    else{Shift shift = new Shift(startTimeAndDate.getTime(), endTimeAndDate.getTime());
                        DatabaseHelper.writeShift(shift);
                        b.dismiss();
                    }
                }
            }
        });
    }


}