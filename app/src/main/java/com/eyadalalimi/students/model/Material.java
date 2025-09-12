package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

public class Material {
    @SerializedName("id") public long id;
    @SerializedName("name") public String name;

    // "global" | "university"
    @SerializedName("scope") public String scope;

    @SerializedName("university_id") public Long university_id;
    @SerializedName("college_id")     public Long college_id;
    @SerializedName("major_id")       public Long major_id;

    @SerializedName("level") public Integer level;

    @SerializedName("is_active") public Boolean is_active;


    // عرض سهل في القوائم
    public String displayScope() {
        if ("global".equalsIgnoreCase(scope)) return "عام";
        if ("university".equalsIgnoreCase(scope)) return "جامعي";
        return scope != null ? scope : "-";
    }
}
