package com.example.seg2105project.Users;

import com.example.seg2105project.Activities.Listable;

import java.io.Serializable;

public class Doctor extends NonAdmin implements Serializable, Listable {
    private String employeenumber, specialties;

    // Constructor(s)
    public Doctor(String email, String password, String firstname, String lastname,
                  String phonenumber, String address, String employeenumber, String specialties, String id) {
        super(email, password, firstname, lastname, phonenumber, address, id);
        this.employeenumber = employeenumber;
        this.specialties = specialties;
    }
    public Doctor(){
        super();
        this.employeenumber = "";
        this.specialties = "";
    }


    // Getters and setters
    @Override
    public String getRole(){
        return "Doctor";
    }
    public String getEmployeenumber() {
        return employeenumber;
    }

    public void setEmployeenumber(String employeenumber) {
        this.employeenumber = employeenumber;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

}