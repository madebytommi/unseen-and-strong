package com.example.unseenandstrong

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class AppViewModel(
    private val flareDayPreferences: FlareDayPreferenceStore
) : ViewModel() {
    constructor() : this(InMemoryFlareDayPreferenceStore())

    val isFlareDayActive = MutableStateFlow(flareDayPreferences.isEnabled)

    fun toggleFlareDayMode() {
        isFlareDayActive.value = !isFlareDayActive.value
        flareDayPreferences.isEnabled = isFlareDayActive.value
    }

    class Factory(
        private val flareDayPreferences: FlareDayPreferenceStore
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
                return AppViewModel(flareDayPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private class InMemoryFlareDayPreferenceStore : FlareDayPreferenceStore {
        override var isEnabled: Boolean = false
    }
}
