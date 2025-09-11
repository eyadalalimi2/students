package com.eyadalalimi.students.request.auth;

public class LoginRequest {
    public String email;
    public String password;
    public String login_device;

    public LoginRequest(String email, String password, String device) {
        this.email = email;
        this.password = password;
        this.login_device = device;
    }
}
