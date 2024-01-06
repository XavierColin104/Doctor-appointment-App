package com.example.seg2105project.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seg2105project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class AppointmentRequest extends AppCompatActivity {
    TextView first,second,third;
    Button button;
    private static final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_request);
        first = findViewById(R.id.textView);
        second = findViewById(R.id.textView2);
        third = findViewById(R.id.textView3);
        button = findViewById(R.id.button);
        Appointment appointment = AppointmentPicker.currentAppointment;
        first.setText(new Date(appointment.getDateAndTime()).toString());
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot doctor =dataSnapshot.child("Users").child("Doctors").child(appointment.getDoctorID());
                    String fullName = "";
                    if (doctor.child("firstName").getValue(String.class)!= null && doctor.child("lastName").getValue(String.class)!= null) fullName = doctor.child("firstName").getValue(String.class) +" " +  doctor.child("lastName").getValue(String.class);
                    second.setText("Doctor " + fullName);
                    int count = 0;
                    long total = 0;
                    boolean exists = false;
                    for(DataSnapshot appointmentShot : dataSnapshot.child("Doctor Appointments").child(appointment.getDoctorID()).getChildren()){
                        if(dataSnapshot.child("Appointments").child(appointmentShot.getKey()).child("rating").exists()){
                            exists = true;
                            count++;
                            total += dataSnapshot.child("Appointments").child(appointmentShot.getKey()).child("rating").getValue(Float.class);
                        }
                    }
                    if (exists){
                        total /= count;
                        third.setText("Doctor rating: " + total);
                    }
                    else third.setText("Doctor has not been rated yet.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "DID NOT READ DATA");
            }
        };
        myRef.addValueEventListener(listener);


    }
    public void requestAppointment(View view){
        DatabaseHelper.writeAppointmentRequest(AppointmentPicker.currentAppointment);
        Toast.makeText(this,"Request submitted!",Toast.LENGTH_LONG).show();
        finish();
    }
}