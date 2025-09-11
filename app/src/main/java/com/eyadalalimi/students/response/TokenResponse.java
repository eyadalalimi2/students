package com.eyadalalimi.students.response;

import com.eyadalalimi.students.model.User;

public class TokenResponse {
    public String token;
    public User user; // الخادم يعيد user ضمن login
}
