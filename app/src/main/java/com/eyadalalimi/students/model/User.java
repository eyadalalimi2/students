package com.eyadalalimi.students.model;

public class User {
    public long id;
    public String student_number;
    public String name;
    public String email;
    public String phone;
    public Long country_id;
    public Long university_id;
    public Long college_id;
    public Long major_id;
    public Integer level;
    public String gender; // "male" | "female"
    public String status; // "active" | ...
    public Boolean has_active_subscription;
    public String profile_photo_path;
    public String email_verified_at; // أهم حقل للتوجيه
    public String created_at;
    public String updated_at;

    public boolean isEmailVerified() {
        return email_verified_at != null && !email_verified_at.trim().isEmpty();
    }

    public boolean isActivated() {
        return has_active_subscription != null && has_active_subscription;
    }
}
