package com.eyadalalimi.students.request.auth;

public class VerifyEmailRequest {
    public String email;
    public String code;
    public VerifyEmailRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
