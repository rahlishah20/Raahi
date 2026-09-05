package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class ProfilePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("raahi_profile_prefs", Context.MODE_PRIVATE)

    fun getProfileName(): String {
        return prefs.getString(KEY_PROFILE_NAME, DEFAULT_NAME) ?: DEFAULT_NAME
    }

    fun setProfileName(name: String) {
        prefs.edit().putString(KEY_PROFILE_NAME, name).apply()
    }

    fun getProfilePhotoPath(): String? {
        return prefs.getString(KEY_PROFILE_PHOTO_PATH, null)
    }

    fun setProfilePhotoPath(path: String?) {
        prefs.edit().putString(KEY_PROFILE_PHOTO_PATH, path).apply()
    }

    companion object {
        private const val KEY_PROFILE_NAME = "profile_name"
        private const val KEY_PROFILE_PHOTO_PATH = "profile_photo_path"
        private const val DEFAULT_NAME = "Haseeb"
    }
}
