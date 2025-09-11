package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

public class Material {
    public long id;
    public String name;

    // "global" | "university"
    public String scope;

    @SerializedName("university_id")
    public Long universityId;

    @SerializedName("college_id")
    public Long collegeId;

    @SerializedName("major_id")
    public Long majorId;

    public Integer level;

    @SerializedName("is_active")
    public Boolean isActive;
}
