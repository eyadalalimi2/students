package com.eyadalalimi.students.request.auth;

public class RegisterRequest {
    public String name;
    public String email;
    public String password;
    public String password_confirmation;
    public String login_device;

    public Integer country_id;    // مطلوب
    public Integer university_id; // اختياري
    public Integer college_id;    // اختياري
    public Integer major_id;      // اختياري
    public Integer level;         // اختياري
    public String  gender;        // "male" | "female" (اختياري)

    public RegisterRequest(String name, String email, String password, String password_confirmation,
                           String login_device,
                           Integer country_id, Integer university_id, Integer college_id, Integer major_id,
                           Integer level, String gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.login_device = login_device;

        this.country_id = country_id;
        this.university_id = university_id;
        this.college_id = college_id;
        this.major_id = major_id;
        this.level = level;
        this.gender = gender;
    }
}
