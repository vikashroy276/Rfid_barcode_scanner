package com.mespl.emp_asset_mgmtapp.utils

import android.content.Context
import android.content.SharedPreferences

object CacheUtils {
    private const val PREFS_NAME = "SETTING_VALUE"
    private const val KEY_USER_ID = "USER_ID"
    private const val KEY_BASE_URL = "KEY_BASE_URL"
    const val KEY_TOKEN = "TOKEN"

    fun saveString(context: Context, key: String, data: String?) {
        val settings: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putString(key, data)
        editor.apply()
    }

    fun getString(context: Context, key: String): String? {
        val settings: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return settings.getString(key, null)
    }

    fun saveUserId(context: Context, userId: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getUserId(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_ID, null)  // Return null if not found
    }

    fun saveBASEURL(url: String?) {
        val settings: SharedPreferences =
            App.app!!.getSharedPreferences("KEY_BASE_URL", Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putString(KEY_BASE_URL, url)
        editor.apply()
    }

    fun getBASEURL(): String? {
        val settings: SharedPreferences =
            App.app!!.getSharedPreferences("KEY_BASE_URL", Context.MODE_PRIVATE)
        return settings.getString("KEY_BASE_URL", null)
    }
}

