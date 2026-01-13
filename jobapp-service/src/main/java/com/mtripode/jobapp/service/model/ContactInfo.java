package com.mtripode.jobapp.service.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Embeddable
public class ContactInfo {

    @Email
    private String email;

    @Pattern(regexp = "\\+?[0-9\\- ]{7,15}", message = "Invalid phone number")
    private String phone;

    private String linkedIn;
    private String github;

    // --- Constructors ---
    public ContactInfo() {
    }

    public ContactInfo(String email, String phone, String linkedIn, String github) {
        this.email = email;
        this.phone = phone;
        this.linkedIn = linkedIn;
        this.github = github;
    }

    // --- Getters & Setters ---
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    @Override
    public String toString() {
        return "ContactInfo{email='" + email + "', phone='" + phone
                + "', linkedIn='" + linkedIn + "', github='" + github + "'}";
    }
}
