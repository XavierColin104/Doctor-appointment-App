package com.example.seg2105project.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.seg2105project.R;
import com.example.seg2105project.Users.Doctor;
import com.example.seg2105project.Users.Patient;
import com.example.seg2105project.Users.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private DatabaseReference myRef;
    private Button btn_back, btn_submitRequest;
    private RadioGroup rbg_userType;
    private EditText txt_name, txt_lastName, txt_phoneNumber, txt_emailRegister, txt_address,
            txt_number, txt_specialties, txt_passwordRegister;
    private String role = "Patient";
    private boolean filled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        btn_back = findViewById(R.id.btn_back);
        rbg_userType = findViewById(R.id.rbg_userType);
        txt_name = findViewById(R.id.txt_name);
        txt_lastName = findViewById(R.id.txt_lastName);
        txt_phoneNumber = findViewById(R.id.txt_phoneNumber);
        txt_emailRegister = findViewById(R.id.txt_emailRegister);
        txt_address = findViewById(R.id.txt_address);
        txt_number = findViewById(R.id.txt_number);
        txt_specialties = findViewById(R.id.txt_specialties);
        btn_submitRequest = findViewById(R.id.btn_submitRequest);
        txt_passwordRegister = findViewById(R.id.txt_passwordRegister);
        rbg_userType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                if (((RadioButton) findViewById(rbg_userType.getCheckedRadioButtonId())).getText().toString()
                        .equals("Doctor")) {
                    txt_specialties.setVisibility(View.VISIBLE);
                    txt_number.setHint("Employee number");
                    role = "Doctor";
                } else {
                    txt_specialties.setVisibility(View.GONE);
                    txt_number.setHint("Health card number");
                    role = "Patient";
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        btn_submitRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


            filled = true;
            checkAllEditTexts((ViewGroup)txt_name.getParent());

            if (filled) {
                mAuth.createUserWithEmailAndPassword(txt_emailRegister.getText().toString(),
                                txt_passwordRegister.getText().toString())
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {

                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();


                                    if (role.equals("Doctor"))
                                        DatabaseHelper.writeRequest(new Doctor(txt_emailRegister.getText().toString(),
                                                txt_passwordRegister.getText().toString(), txt_name.getText().toString(),
                                                txt_lastName.getText().toString(), txt_phoneNumber.getText().toString(),
                                                txt_address.getText().toString(), txt_number.getText().toString(),
                                                txt_specialties.getText().toString(),user.getUid()));
                                    else
                                        DatabaseHelper.writeRequest(new Patient(txt_emailRegister.getText().toString(),
                                                txt_passwordRegister.getText().toString(), txt_name.getText().toString(),
                                                txt_lastName.getText().toString(), txt_phoneNumber.getText().toString(),
                                                txt_address.getText().toString(), txt_number.getText().toString(),user.getUid()));
                                    Log.d(TAG, "createUserWithEmail:success");

                                    DatabaseHelper.getUser(user.getUid());
                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateUI(user.getUid());


                                        }
                                    },1000);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
                }
            else {
                Toast.makeText(RegisterActivity.this,"All information must be provided.",Toast.LENGTH_LONG).show();
            }
            }
        });

    }



    public void checkAllEditTexts(ViewGroup layout){
        for(int i =0; i< layout.getChildCount(); i++){
            View v =layout.getChildAt(i);
            if(v instanceof AppCompatEditText && ((AppCompatEditText) v).getText().toString().equals("")){
                if(role =="Patient"&& v.getId() == (R.id.txt_specialties)) continue;
                filled = false;
                ((AppCompatEditText) v).setHintTextColor(Color.RED);
                ((AppCompatEditText) v).setHint("Must be filled.");
            }
        }
    }
    private void updateUI (String ID){
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.putExtra("sessionUser", (User) null);
        intent.putExtra("isRejected", false);
        intent.putExtra("UserID", ID);
        startActivity(intent);
    }
}