package com.example.simplecamera

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object HostageManager {
    var isHostageMode by mutableStateOf(false)
    var isPhotoCompleted by mutableStateOf(false)

    fun isHostageActive(): Boolean {
        return isHostageMode && !isPhotoCompleted
    }

    fun releaseHostage() {
        isPhotoCompleted = true
        isHostageMode = false
    }

    fun reset() {
        isHostageMode = false
        isPhotoCompleted = false
    }
}
