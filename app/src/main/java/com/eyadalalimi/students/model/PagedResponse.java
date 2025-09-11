package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
public class PagedResponse<T> {
    public List<T> data;

    public Meta meta;
    public Links links;

    public static class Meta {
        @SerializedName("current_page") public Integer currentPage;
        @SerializedName("per_page")     public Integer perPage;
        @SerializedName("total")        public Integer total;
        @SerializedName("last_page")    public Integer lastPage;
    }

    public static class Links {
        public String first;
        public String last;
        public String prev;
        public String next;
    }
}
