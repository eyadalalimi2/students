package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

public class Visibility {
    @SerializedName("linked_to_university")
    public boolean linkedToUniversity;

    @SerializedName("allowed_sources")
    public String[] allowedSources; // ["assets","contents"]

    public Scope scope;

    public static class Scope {
        @SerializedName("university_id") public Long universityId;
        @SerializedName("college_id")    public Long collegeId;
        @SerializedName("major_id")      public Long majorId;
    }
}
