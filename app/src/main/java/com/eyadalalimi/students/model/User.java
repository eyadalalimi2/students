package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

public class User {
    public long id;

    @SerializedName("student_number")
    public String studentNumber;

    public String name;
    public String email;
    public String phone;

    @SerializedName("country_id")
    public long countryId;

    @SerializedName("university_id")
    public Long universityId;

    @SerializedName("college_id")
    public Long collegeId;

    @SerializedName("major_id")
    public Long majorId;

    public Integer level;            // nullable

    public String gender;            // "male" | "female"
    public String status;            // "active" | "suspended" | "graduated"

    @SerializedName("has_active_subscription")
    public boolean hasActiveSubscription;

    @SerializedName("profile_photo_path")
    public String profilePhotoPath;
}
