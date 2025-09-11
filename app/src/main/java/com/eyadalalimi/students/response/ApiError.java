package com.eyadalalimi.students.response;

import java.util.Map;

public class ApiError {
    public String code;
    public String message;
    public Map<String, String[]> fields;
    public String trace_id;
}
