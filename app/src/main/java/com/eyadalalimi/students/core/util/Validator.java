package com.eyadalalimi.students.core.util;

import android.text.TextUtils;
import android.util.Patterns;

public class Validator {
    public static boolean isEmail(String s) { return !TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches(); }
    public static boolean isOtp(String s) { return s != null && s.matches("\\d{6}"); }
    public static boolean isStrongPassword(String s) { return s != null && s.length() >= 6; } // baseline
}
