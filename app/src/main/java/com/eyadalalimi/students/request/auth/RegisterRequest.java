package com.eyadalalimi.students.request.auth;

public class RegisterRequest {
    public String name;
    public String email;
    public String password;
    public String password_confirmation;
    public String login_device;

    public Integer country_id;
    public Integer university_id;
    public Integer college_id;
    public Integer major_id;
    public Integer level;
    public String gender;

    public RegisterRequest(String name, String email, String password, String password_confirmation, String login_device) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.login_device = login_device;
    }
}
