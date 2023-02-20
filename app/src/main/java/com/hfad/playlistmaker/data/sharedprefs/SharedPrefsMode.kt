package com.hfad.playlistmaker.data.sharedprefs
// Заготовка для реализации SharedPrefs в отдельном классе
enum class SharedPrefsMode {
        MODE_PRIVATE,
        MODE_APPEND,
        MODE_ENABLE_WRITE_AHEAD_LOGGING,
        MODE_MULTI_PROCESS,
        MODE_WORLD_READABLE,
        MODE_WORLD_WRITEABLE
}