package com.eyadalalimi.students.model;

@SuppressWarnings("unused")
public class Content {
    public Long id;
    public String title;
    public String description;
    public String type;              // link | file | video
    public String status;            // "published"
    public String published_at;

    public Long material_id;
    public Long university_id;
    public Long college_id;
    public Long major_id;
    public Long doctor_id;

    public Media media;              // { source_url, file_path }
    public String created_at;
    public String updated_at;

    public static class Media {
        public String source_url;    // للروابط والفيديو
        public String file_path;     // للملفات (قد تكون مسارًا نسبيًا)
    }

    // Helpers
    public String sourceUrl() { return media != null ? media.source_url : null; }
    public String filePath()  { return media != null ? media.file_path  : null; }

    public boolean isLink()  { return "link".equalsIgnoreCase(type); }
    public boolean isFile()  { return "file".equalsIgnoreCase(type); }
    public boolean isVideo() { return "video".equalsIgnoreCase(type); }
}
