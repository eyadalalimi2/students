package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

public class Major {
    public long id;
    public String name;

    @SerializedName("college_id")
    public long collegeId;
}
