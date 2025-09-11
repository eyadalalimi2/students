package com.eyadalalimi.students.model;

import java.util.List;

public class PagedResponse<T> {
    public List<T> data;
    public Meta meta;
    public Links links;

    public static class Meta {
        public Integer count;
        public Integer total;
        public String next_cursor;
    }

    public static class Links {
        public String next;
    }
}
