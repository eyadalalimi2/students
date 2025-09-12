package com.eyadalalimi.students.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListResponse<T> {
    @SerializedName("data") public List<T> data;
    @SerializedName("meta") public Meta meta;
    @SerializedName("links") public Links links;

    public static class Meta {
        @SerializedName("count") public Integer count;
        @SerializedName("total") public Integer total;
        @SerializedName("next_cursor") public String next_cursor;
    }
    public static class Links {
        @SerializedName("next") public String next;
    }
}
