package com.eyadalalimi.students.core.data;

import android.content.Context;
import android.content.SharedPreferences;

public class FilterPrefs {

    private static final String PREFS = "filters_prefs";

    private static final String K_ASSETS_MATERIAL_ID = "assets_material_id";
    private static final String K_ASSETS_CATEGORY    = "assets_category";

    private static final String K_CONTENTS_MATERIAL_ID = "contents_material_id";
    private static final String K_CONTENTS_TYPE        = "contents_type";

    private final SharedPreferences sp;

    public FilterPrefs(Context ctx) {
        this.sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // Assets
    public void saveAssets(Long materialId, String category) {
        SharedPreferences.Editor e = sp.edit();
        if (materialId != null && materialId > 0) e.putLong(K_ASSETS_MATERIAL_ID, materialId);
        else e.remove(K_ASSETS_MATERIAL_ID);
        if (category != null && !category.isEmpty()) e.putString(K_ASSETS_CATEGORY, category);
        else e.remove(K_ASSETS_CATEGORY);
        e.apply();
    }

    public Long getAssetsMaterialId() {
        long v = sp.getLong(K_ASSETS_MATERIAL_ID, -1);
        return v > 0 ? v : null;
    }

    public String getAssetsCategory() {
        return sp.getString(K_ASSETS_CATEGORY, null);
    }

    public void clearAssets() {
        sp.edit().remove(K_ASSETS_MATERIAL_ID).remove(K_ASSETS_CATEGORY).apply();
    }

    // Contents
    public void saveContents(Long materialId, String type) {
        SharedPreferences.Editor e = sp.edit();
        if (materialId != null && materialId > 0) e.putLong(K_CONTENTS_MATERIAL_ID, materialId);
        else e.remove(K_CONTENTS_MATERIAL_ID);
        if (type != null && !type.isEmpty()) e.putString(K_CONTENTS_TYPE, type);
        else e.remove(K_CONTENTS_TYPE);
        e.apply();
    }

    public Long getContentsMaterialId() {
        long v = sp.getLong(K_CONTENTS_MATERIAL_ID, -1);
        return v > 0 ? v : null;
    }

    public String getContentsType() {
        return sp.getString(K_CONTENTS_TYPE, null);
    }

    public void clearContents() {
        sp.edit().remove(K_CONTENTS_MATERIAL_ID).remove(K_CONTENTS_TYPE).apply();
    }
}
