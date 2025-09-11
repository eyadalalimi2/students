package com.eyadalalimi.students.request.auth;

public class LoginRequest {
    private String email;
    private String password;
    private String login_device;

    public LoginRequest(String email, String password, String login_device) {
        this.email = email;
        this.password = password;
        this.login_device = login_device;
    }

    public String getEmail() { return email; }
}
