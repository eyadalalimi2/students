package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

public class Asset {
    public long id;

    // "youtube" | "file" | "reference" | ...
    public String category;

    public String title;
    public String description;

    @SerializedName("published_at")
    public String publishedAt;

    @SerializedName("video_url")
    public String videoUrl;

    @SerializedName("file_path")
    public String filePath;

    @SerializedName("external_url")
    public String externalUrl;

    @SerializedName("material_id")
    public Long materialId;

    @SerializedName("discipline_id")
    public Long disciplineId;

    @SerializedName("program_id")
    public Long programId;
}
