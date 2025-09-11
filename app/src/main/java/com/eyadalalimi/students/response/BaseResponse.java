package com.eyadalalimi.students.response;

public class BaseResponse {
    public Data data;
    public static class Data {
        public String message;
        public Boolean verified;
    }
}
