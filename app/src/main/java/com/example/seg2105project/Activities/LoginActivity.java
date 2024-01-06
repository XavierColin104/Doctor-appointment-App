package com.example.seg2105project.Activities;

import static android.content.ContentValues.TAG;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.seg2105project.R;
import com.example.seg2105project.Users.Doctor;
import com.example.seg2105project.Users.Patient;
import com.example.seg2105project.Users.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LoginActivity extends AppCompatActivity{
    // View variables
    private TextView lbl_forgotPassword;
    private EditText txt_email, txt_password;
    private CheckBox chk_rememberMe;
    private Button btn_login, btn_signUp;

    // Firebase variables
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    public static FirebaseUser user;

    // User object to be passed between activities
    public static User sessionUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Instantiates firebase variables
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Assigns the views to their instance variables for use throughout the class
        lbl_forgotPassword = findViewById(R.id.lbl_forgotPassword);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        chk_rememberMe = findViewById(R.id.chk_rememberMe);
        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signUp);



        // Delete all shifts that have already passed even if the user did not log in
        // This is necessary because we don't have a server than can run this method
        // at all times and delete outdated shifts
        DatabaseHelper.deleteOutdatedShifts();
    }

    public void signupOnClick(View view) {
        // Opens the signup activity
        Intent intent = new Intent(view.getContext(), RegisterActivity.class);
        view.getContext().startActivity(intent);
    }

    public void loginOnClick(View view) {
        if (((txt_email.getText().toString().equals("")) || (txt_password.getText().toString().equals("")))) {
            txt_email.setHintTextColor(Color.RED);
            txt_email.setHint("Invalid e-mail password combination");
            txt_password.setHintTextColor(Color.RED);
            txt_password.setHint("Invalid e-mail password combination");
            return;
        }
        mAuth.signInWithEmailAndPassword(
                txt_email.getText().toString(),
                txt_password.getText().toString()
        ).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            // Will run upon the completion of the authentication
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    user = mAuth.getCurrentUser();
                    assert user != null;
                    DatabaseHelper.getUser(user.getUid());

                    // Use a handler to give sufficient time for the data to be retrieved
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sessionUser = DatabaseHelper.returnUser();
                            updateUI();
                        }
                    },1000);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    txt_email.setHintTextColor(Color.RED);
                    txt_email.setHint("Invalid e-mail password combination");
                    txt_email.setText("");
                    txt_password.setHintTextColor(Color.RED);
                    txt_password.setHint("Invalid e-mail password combination");
                    txt_password.setText("");
                }
            }
        });
    }

    private void updateUI (){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("sessionUser", sessionUser);
        intent.putExtra("isPending", sessionUser==null);
        if (sessionUser != null) {
            intent.putExtra("isRejected", false);
            LoginActivity.this.startActivity(intent);
        } else {
            DatabaseHelper.getRejected(user.getUid());
            // Use a handler to give sufficient time for the data to be retrieved
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    intent.putExtra("isRejected", DatabaseHelper.isRejected());
                    LoginActivity.this.startActivity(intent);
                }
            },1000);
        }
    }


}

