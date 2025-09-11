package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

public class Content {
    public long id;

    public String title;
    public String description;

    // "file" | "video" | "link"
    public String type;

    @SerializedName("source_url")
    public String sourceUrl;

    @SerializedName("file_path")
    public String filePath;

    @SerializedName("published_at")
    public String publishedAt;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("material_id")
    public Long materialId;

    @SerializedName("university_id")
    public Long universityId;

    @SerializedName("college_id")
    public Long collegeId;

    @SerializedName("major_id")
    public Long majorId;
}
