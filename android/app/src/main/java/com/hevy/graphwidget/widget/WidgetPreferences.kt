package com.hevy.graphwidget.widget

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class WidgetPreferences(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "hevy_widget_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun getApiKey(): String? = prefs.getString("api_key", null)
    
    fun setApiKey(key: String) {
        prefs.edit().putString("api_key", key).apply()
    }
    
    fun getColorTheme(): String = prefs.getString("color_theme", ColorTheme.BLUE.name) ?: ColorTheme.BLUE.name
    
    fun setColorTheme(theme: String) {
        prefs.edit().putString("color_theme", theme).apply()
    }
}
