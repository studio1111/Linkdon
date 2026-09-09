package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.FontOption
import com.example.data.model.SortOption
import com.example.data.model.ThemeOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenceRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("linkdoon_preferences", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(
        ThemeOption.fromId(prefs.getString(KEY_THEME, ThemeOption.DARK.id) ?: ThemeOption.DARK.id)
    )
    val theme: StateFlow<ThemeOption> = _theme.asStateFlow()

    private val _font = MutableStateFlow(
        FontOption.fromId(prefs.getString(KEY_FONT, FontOption.DEFAULT.id) ?: FontOption.DEFAULT.id)
    )
    val font: StateFlow<FontOption> = _font.asStateFlow()

    private val _sortOption = MutableStateFlow(
        SortOption.fromId(prefs.getString(KEY_SORT, SortOption.DATE_NEWEST.id) ?: SortOption.DATE_NEWEST.id)
    )
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _textScale = MutableStateFlow(
        prefs.getFloat(KEY_TEXT_SCALE, 1.0f)
    )
    val textScale: StateFlow<Float> = _textScale.asStateFlow()

    private val _isGridLayout = MutableStateFlow(
        prefs.getBoolean(KEY_IS_GRID, true)
    )
    val isGridLayout: StateFlow<Boolean> = _isGridLayout.asStateFlow()

    private val _isRtl = MutableStateFlow(
        prefs.getBoolean(KEY_IS_RTL, true)
    )
    val isRtl: StateFlow<Boolean> = _isRtl.asStateFlow()

    // Authentication & Security state
    private val _userEmail = MutableStateFlow(prefs.getString(KEY_USER_EMAIL, "") ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPassword = MutableStateFlow(prefs.getString(KEY_USER_PASSWORD, "") ?: "")
    val userPassword: StateFlow<String> = _userPassword.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(prefs.getBoolean(KEY_IS_OFFLINE_MODE, false))
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    private val _appPin = MutableStateFlow(prefs.getString(KEY_APP_PIN, "") ?: "")
    val appPin: StateFlow<String> = _appPin.asStateFlow()

    private val _isSetupCompleted = MutableStateFlow(
        prefs.getBoolean(KEY_SETUP_COMPLETED, false) ||
        (prefs.getString(KEY_USER_NAME, "")?.isNotBlank() == true) ||
        prefs.getBoolean(KEY_IS_OFFLINE_MODE, false)
    )
    val isSetupCompleted: StateFlow<Boolean> = _isSetupCompleted.asStateFlow()

    fun setTheme(theme: ThemeOption) {
        _theme.value = theme
        prefs.edit().putString(KEY_THEME, theme.id).apply()
    }

    fun setFont(font: FontOption) {
        _font.value = font
        prefs.edit().putString(KEY_FONT, font.id).apply()
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
        prefs.edit().putString(KEY_SORT, option.id).apply()
    }

    fun setTextScale(scale: Float) {
        val clamped = scale.coerceIn(0.80f, 1.35f)
        _textScale.value = clamped
        prefs.edit().putFloat(KEY_TEXT_SCALE, clamped).apply()
    }

    fun toggleLayoutMode() {
        val newMode = !_isGridLayout.value
        _isGridLayout.value = newMode
        prefs.edit().putBoolean(KEY_IS_GRID, newMode).apply()
    }

    fun setRtl(isRtl: Boolean) {
        _isRtl.value = isRtl
        prefs.edit().putBoolean(KEY_IS_RTL, isRtl).apply()
    }

    fun toggleRtl() {
        val newRtl = !_isRtl.value
        _isRtl.value = newRtl
        prefs.edit().putBoolean(KEY_IS_RTL, newRtl).apply()
    }

    fun setCredentials(username: String, email: String, password: String = "") {
        _userName.value = username.trim()
        _userEmail.value = email.trim()
        if (password.isNotBlank()) {
            _userPassword.value = password.trim()
        }
        _isOfflineMode.value = false
        _isSetupCompleted.value = true
        val editor = prefs.edit()
            .putString(KEY_USER_NAME, username.trim())
            .putString(KEY_USER_EMAIL, email.trim())
            .putBoolean(KEY_IS_OFFLINE_MODE, false)
            .putBoolean(KEY_SETUP_COMPLETED, true)
        if (password.isNotBlank()) {
            editor.putString(KEY_USER_PASSWORD, password.trim())
        }
        editor.apply()
    }

    fun logoutUser() {
        _isSetupCompleted.value = false
        prefs.edit().putBoolean(KEY_SETUP_COMPLETED, false).apply()
    }

    fun enterOfflineMode() {
        _isOfflineMode.value = true
        _isSetupCompleted.value = true
        prefs.edit()
            .putBoolean(KEY_IS_OFFLINE_MODE, true)
            .putBoolean(KEY_SETUP_COMPLETED, true)
            .apply()
    }

    fun setAppPin(pin: String) {
        val cleanPin = pin.trim()
        _appPin.value = cleanPin
        prefs.edit().putString(KEY_APP_PIN, cleanPin).apply()
    }

    fun removeAppPin() {
        _appPin.value = ""
        prefs.edit().remove(KEY_APP_PIN).apply()
    }

    fun resetSecurityAccount() {
        _userName.value = ""
        _userEmail.value = ""
        _userPassword.value = ""
        _appPin.value = ""
        _isOfflineMode.value = false
        _isSetupCompleted.value = false
        prefs.edit()
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_PASSWORD)
            .remove(KEY_APP_PIN)
            .putBoolean(KEY_IS_OFFLINE_MODE, false)
            .putBoolean(KEY_SETUP_COMPLETED, false)
            .apply()
    }

    companion object {
        private const val KEY_THEME = "pref_theme"
        private const val KEY_FONT = "pref_font"
        private const val KEY_SORT = "pref_sort_option"
        private const val KEY_TEXT_SCALE = "pref_text_scale"
        private const val KEY_IS_GRID = "pref_is_grid"
        private const val KEY_IS_RTL = "pref_is_rtl"
        private const val KEY_USER_EMAIL = "pref_user_email"
        private const val KEY_USER_NAME = "pref_user_name"
        private const val KEY_USER_PASSWORD = "pref_user_password"
        private const val KEY_IS_OFFLINE_MODE = "pref_is_offline_mode"
        private const val KEY_APP_PIN = "pref_app_pin"
        private const val KEY_SETUP_COMPLETED = "pref_setup_completed"
    }
}
