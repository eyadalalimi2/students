package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

public class Subscription {
    public long id;

    @SerializedName("user_id")
    public long userId;

    @SerializedName("plan_id")
    public long planId;

    public String status;            // "active" | ...
    @SerializedName("started_at")
    public String startedAt;
    @SerializedName("ends_at")
    public String endsAt;
}
