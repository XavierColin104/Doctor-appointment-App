package com.example.seg2105project.Users;

import com.example.seg2105project.Activities.Listable;

import java.io.Serializable;
import java.util.Objects;

public class Patient extends NonAdmin implements Serializable, Listable {
    private String healthcardnumber;

    // Constructor(s)
    public Patient(String email, String password, String firstname, String lastName, String phoneNumber, String address,
                   String healthcardnumber, String id) {
        super( email, password, firstname, lastName, phoneNumber, address, id);
        this.healthcardnumber = healthcardnumber;

    }
    public Patient(){
        super();
        this.healthcardnumber = "";
    }

    //Getters and setters

    public String getHealthcardnumber() {
        return healthcardnumber;
    }

    public void setHealthcardnumber(String healthcardnumber) {
        this.healthcardnumber = healthcardnumber;
    }
    @Override
    public String getRole(){
        return "Patient";
    }

    // Overrides


    // Methods




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return email.equals(patient.getEmail());
    }
}
