package com.example.seg2105project.Users;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class User implements Serializable {
    // Instance variables

    protected String email, password;

    // Constructor(s)
    public User(String email, String password) {

        this.email = email;
        this.password = password;
    }

    public User() {
        this.email = "";
        this.password = "";
    }
    // Getters and setters



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    // Overrides

    // Getters and setters




    // Methods



    public String getPhoneNumber(){return"";}

    public String getFirstName() {
        return"";
    }

    public String getRole() {
        return"";
    }

    public String getLastName() {
        return"";
    }
    public String getAddress() {
        return"";
    }
    
}