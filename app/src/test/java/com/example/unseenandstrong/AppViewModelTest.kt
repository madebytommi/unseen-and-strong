package com.example.unseenandstrong

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppViewModelTest {
    @Test
    fun flareDayDefaultsOffAndRestoresAfterEnableDisable() {
        val preferences = FakeFlareDayPreferenceStore()

        val initialViewModel = AppViewModel(preferences)
        assertFalse(initialViewModel.isFlareDayActive.value)
        assertFalse(preferences.isEnabled)

        initialViewModel.toggleFlareDayMode()
        assertTrue(initialViewModel.isFlareDayActive.value)
        assertTrue(preferences.isEnabled)

        val restoredEnabledViewModel = AppViewModel(preferences)
        assertTrue(restoredEnabledViewModel.isFlareDayActive.value)

        restoredEnabledViewModel.toggleFlareDayMode()
        assertFalse(restoredEnabledViewModel.isFlareDayActive.value)
        assertFalse(preferences.isEnabled)

        val restoredDisabledViewModel = AppViewModel(preferences)
        assertFalse(restoredDisabledViewModel.isFlareDayActive.value)
    }

    private class FakeFlareDayPreferenceStore : FlareDayPreferenceStore {
        override var isEnabled: Boolean = false
    }
}
