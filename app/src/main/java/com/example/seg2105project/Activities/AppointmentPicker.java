package com.example.seg2105project.Activities;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.seg2105project.R;
import com.example.seg2105project.Users.Doctor;
import com.example.seg2105project.Users.Patient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AppointmentPicker extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener{
    private static MyRecyclerViewAdapter adapter;

    public static ArrayList<Listable> list;
    private static final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    private static RecyclerView recyclerView;

    public static Appointment currentAppointment;
    public static Shift doctorShift ;
    public static boolean doctorSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);
        list = new ArrayList<>();
        // set up the RecyclerView
        recyclerView = findViewById(R.id.requestList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, list);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        Button goBackButton = findViewById(R.id.btn_GoBackRequest);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Doctor doctor = null;
        if(GenericListActivity.searchType.equals("Specialty") && !doctorSearch){doctorShift = (Shift) GenericListActivity.currentListable; }
        else if (GenericListActivity.searchType.equals("Doctor")) {doctor = (Doctor) GenericListActivity.currentListable;}

        Doctor finalDoctor = doctor;
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (GenericListActivity.state.startsWith("Request Appointment-") && snapshot.exists()){
                    if(GenericListActivity.searchType.equals("Specialty")) {
                        DataSnapshot shiftSnapshot = snapshot.child("Shifts").child(doctorShift.getDoctorID()).child(doctorShift.getShiftID());
                        HashMap<Long, Boolean> appointmentTable = new HashMap<>();
                        long start = doctorShift.getStartTime();
                        long end = doctorShift.getEndTime();
                        int numAppSlots = Math.toIntExact((MILLISECONDS.toMinutes(end - start) / 30));
                        for (int i = 0; i < numAppSlots; i++)
                            appointmentTable.put(MINUTES.toMillis(MILLISECONDS.toMinutes(start)
                                    + (30L * i)), false);
                        for (DataSnapshot appointmentSlot : shiftSnapshot.child("Appointments").getChildren()) {
                            if (appointmentSlot.exists()
                                    && appointmentSlot.getValue(Long.class) != null) {
                                appointmentTable.put(appointmentSlot.getValue(Long.class), true);
                            }
                        }
                        for (int j = 0; j < numAppSlots; j++) {
                            if (appointmentTable.get(MINUTES.toMillis(MILLISECONDS.toMinutes(start)
                                    + (30L * j))) == false) {
                                Appointment temp = new Appointment(((Patient) HomeActivity.user).getId(),
                                        doctorShift.getDoctorID(), start + MINUTES.toMillis(30L * j));
                                temp.setShiftID(doctorShift.getShiftID());
                                list.add(temp);
                            }
                        }
                    }
                    else if(GenericListActivity.searchType.equals("Doctor")){

                        for (DataSnapshot shift : snapshot.child("Shifts").child(finalDoctor.getId()).getChildren()) {
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
                            for (DataSnapshot appointment : snapshot.child("Shifts").child(shift.getKey()).child("Appointments").getChildren()) {
                                if (appointment.child("dateAndTime").exists()
                                        && appointment.child("dateAndTime").getValue(Long.class) != null) {
                                    appointmentTable.put(appointment.child("dateAndTime").getValue(Long.class), true);
                                }
                            }
                            //Check if there are any free slots
                            for (Boolean taken : appointmentTable.values()) {
                                if (!taken) {
                                    list.add(new Shift(shift.child("startTime").getValue(Long.class), shift.child("endTime").getValue(Long.class), finalDoctor.getId(), shift.getKey(),
                                            finalDoctor.getSpecialties() + " Specialist, Doctor " + finalDoctor.getFirstName() + " " + finalDoctor.getLastName()));
                                    break;
                                }
                            }

                        }

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myRef.addValueEventListener(listener);
    }

    @Override
    public void onItemClick(View view, int position) {
        if(GenericListActivity.searchType.equals("Specialty")) {
            currentAppointment = (Appointment) adapter.getItem(position);
            Intent intent = new Intent(this, AppointmentRequest.class);
            this.startActivity(intent);
        }
        else if(GenericListActivity.searchType.equals("Doctor")){
            GenericListActivity.searchType = "Specialty";
            doctorShift = (Shift) adapter.getItem(position);
            doctorSearch = true;
            Intent intent = new Intent(this, AppointmentPicker.class);
            finish();
            this.startActivity(intent);
        }
    }
}