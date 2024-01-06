package com.example.seg2105project.Users;

import com.example.seg2105project.Activities.Listable;

import java.io.Serializable;

public abstract class NonAdmin extends User implements Serializable, Listable {

    protected String firstname,lastname,phonenumber,address, id;

    // Constructor(s)
    public NonAdmin( String email, String password,String firstname,String lastname, String phonenumber,String address, String id) {
        super( email, password);
        this.firstname = firstname;
        this.lastname = lastname;
        this.phonenumber = phonenumber;
        this.address = address;
        this.id = id;

    }
    public NonAdmin(){
        super();
        this.firstname = "";
        this.lastname = "";
        this.phonenumber = "";
        this.address = "";
        this.id = "";
    }

    // Getters and setters

    @Override
    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }
@Override
    public String getPhoneNumber() {
        return phonenumber;
    }

    public void setPhoneNumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
@Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

// Overrides

    // Methods

}