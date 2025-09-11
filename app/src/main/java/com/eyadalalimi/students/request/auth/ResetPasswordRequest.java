package com.eyadalalimi.students.request.auth;

public class ResetPasswordRequest {
    public String email;
    public String token;
    public String password;
    public String password_confirmation;

    public ResetPasswordRequest(String email, String token, String password, String password_confirmation) {
        this.email = email;
        this.token = token;
        this.password = password;
        this.password_confirmation = password_confirmation;
    }
}
