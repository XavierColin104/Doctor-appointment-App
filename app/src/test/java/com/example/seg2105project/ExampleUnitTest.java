package com.example.seg2105project;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.seg2105project.ExternalAssistance.GMail;
import com.example.seg2105project.Users.Administrator;
import com.example.seg2105project.Users.Doctor;
import com.example.seg2105project.Users.Patient;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testAdministrator_getRole() {
        Administrator testAdmin = new Administrator();
        assertEquals("Admin", testAdmin.getRole());
    }
    @Test
    public void testDoctor_Specialties() {
        Doctor testDoctor = new Doctor();
        testDoctor.setSpecialties("Testology");
        assertEquals("Testology", testDoctor.getSpecialties());
    }

    @Test
    public void testPatient_getEmail() {
        Patient testPatient = new Patient();
        testPatient.setEmail("testEmail@gmail.com");
        assertEquals("testEmail@gmail.com", testPatient.getEmail());
    }

    @Test
    public void testEmptyPatient_getHealthCardNumber() {
        Patient testPatient = new Patient();
        assertEquals("", testPatient.getHealthcardnumber());
    }

}