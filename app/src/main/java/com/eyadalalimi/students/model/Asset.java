package com.eyadalalimi.students.model;

@SuppressWarnings("unused")
public class Asset {
    public Long id;
    public String category;          // youtube | file | reference | book | curriculum | question_bank ...
    public String title;
    public String description;
    public String status;            // "published"
    public String published_at;

    public Long material_id;
    public Long discipline_id;
    public Long program_id;
    public Long doctor_id;

    public com.eyadalalimi.students.model.Media media;              // { video_url, file_path, external_url }
    public String created_at;
    public String updated_at;

    public static class Media {
        public String video_url;
        public String file_path;
        public String external_url;
    }

    // Helpers
    public String videoUrl()    { return media != null ? media.video_url : null; }
    public String filePath()    { return media != null ? media.file_path : null; }
    public String externalUrl() { return media != null ? media.external_url : null; }

    public String kind() {
        if (videoUrl() != null) return "video";
        if (externalUrl() != null) return "link";
        if (filePath() != null) return "file";
        return category != null ? category : "—";
    }
}
