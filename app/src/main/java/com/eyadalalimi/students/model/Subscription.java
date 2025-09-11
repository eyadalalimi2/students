package com.eyadalalimi.students.model;

public class Subscription {
    public long id;
    public long user_id;
    public long plan_id;
    public String status;      // active|...
    public String started_at;
    public String ends_at;
}
