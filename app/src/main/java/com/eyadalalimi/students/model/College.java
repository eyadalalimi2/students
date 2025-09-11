package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

public class College {
    public long id;
    public String name;

    @SerializedName("university_id")
    public long universityId;
}
