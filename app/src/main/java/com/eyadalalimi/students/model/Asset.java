package com.eyadalalimi.students.model;

@SuppressWarnings("unused")
public class Asset {
    public Long id;
    public String category;          // book | curriculum | question_bank | reference | file | youtube ...
    public String title;
    public String description;
    public String status;            // "published" ...
    public String published_at;

    public Long material_id;
    public Long discipline_id;
    public Long program_id;
    public Long doctor_id;

    public Media media;              // الروابط أصبحت داخل media
    public String created_at;
    public String updated_at;

    public static class Media {
        public String video_url;     // قد يكون null
        public String file_path;     // قد يكون null
        public String external_url;  // قد يكون null
    }

    // Helpers اختيارية
    public String videoUrl()   { return media != null ? media.video_url   : null; }
    public String filePath()   { return media != null ? media.file_path   : null; }
    public String externalUrl(){ return media != null ? media.external_url: null; }
}
