package com.hoanganhdangcode.yahooooo.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "chat_app_pref";
    private static final String KEY_UID = "uid";
    private static final String KEY_LANG = "lang";

    private static SessionManager instance;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // 🔑 Lưu UID sau khi đăng nhập
    public void saveUid(String uid) {
        editor.putString(KEY_UID, uid).apply();
    }

    public String getUid() {
        return prefs.getString(KEY_UID, null);
    }

    public void saveLanguage(String langCode) {
        editor.putString(KEY_LANG, langCode).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANG, "en");
    }

    public void clearSession() {
        editor.clear().apply();
    }
}

