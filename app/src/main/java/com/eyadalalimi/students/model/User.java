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
    public String gender; // male|female
    public String status; // active|suspended|graduated
    public boolean has_active_subscription;
    public String profile_photo_path;
}
