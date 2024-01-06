package com.example.seg2105project.Users;


import java.io.Serializable;

public class Administrator extends User implements Serializable {

    // Constructor(s)
    public Administrator(String email, String password) {
        super(email, password);
    }

    public Administrator() {
        super("","");
    }

    public String getRole(){
        return "Admin";
    }



}