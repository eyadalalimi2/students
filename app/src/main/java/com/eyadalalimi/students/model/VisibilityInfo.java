package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisibilityInfo {

    @SerializedName("linked_to_university")
    public boolean linkedToUniversity;

    @SerializedName("allowed_sources")
    public List<String> allowedSources;

    @SerializedName("scope")
    public Scope scope;

    public static class Scope {
        @SerializedName("university_id") public Long universityId;
        @SerializedName("college_id")    public Long collegeId;
        @SerializedName("major_id")      public Long majorId;
    }

    // مساعدات اختيارية
    public boolean canSeeContents() {
        return allowedSources != null && allowedSources.contains("contents");
    }
}
