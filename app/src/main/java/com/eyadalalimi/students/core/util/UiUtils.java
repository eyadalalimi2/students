package com.eyadalalimi.students.core.util;

import android.content.Context;
import android.widget.Toast;

public class UiUtils {
    public static void toast(Context c, String msg) { Toast.makeText(c, msg, Toast.LENGTH_SHORT).show(); }
}
