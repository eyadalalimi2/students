package com.eyadalalimi.students.model;

import com.google.gson.annotations.SerializedName;

/**
 * عنصر موحّد لدفق الصفحة الرئيسية (assets/contents).
 * الحقول مرنة لتقبّل أي من النوعين.
 */
public class FeedItem {

    // "asset" | "content"
    @SerializedName("kind")
    public String kind;

    public long id;

    public String title;

    // وصف موجز إن توفر
    public String description;

    // فئة/نوع: لأصول "category"، وللمحتوى "type"
    public String tag;

    @SerializedName("published_at")
    public String publishedAt;

    // روابط/ملفات إن لزم
    public Media media;

    // مرجع مادة
    @SerializedName("material_id")
    public Long materialId;

    // لتمييز الجامعة/الكلية/التخصص إن لزم
    @SerializedName("university_id")
    public Long universityId;
    @SerializedName("college_id")
    public Long collegeId;
    @SerializedName("major_id")
    public Long majorId;

    public String type;
    public String published_at;


    public Asset asset;
    public Content content;


    /** نص قابل للعرض لنوع العنصر */
    public String displayType() {
        if (content != null && content.type != null) return content.type;
        if (asset != null   && asset.category != null) return asset.category;
        if (type != null) return type;
        return "item";
    }
}
