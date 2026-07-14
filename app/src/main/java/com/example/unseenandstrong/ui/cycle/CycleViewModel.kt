package com.example.unseenandstrong.ui.cycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unseenandstrong.data.local.cycle.CycleLogDao
import com.example.unseenandstrong.data.local.cycle.CycleLogEntity
import com.example.unseenandstrong.data.local.cycle.CycleSettingsDao
import com.example.unseenandstrong.data.local.cycle.CycleSettingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CycleViewModel(
    private val cycleLogDao: CycleLogDao,
    private val cycleSettingsDao: CycleSettingsDao
) : ViewModel() {

    val trackingMode: StateFlow<String> =
        cycleSettingsDao.observeSettings()
            .map { settings -> settings?.trackingMode ?: "Standard" }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = "Standard"
            )

    val mostRecentCycleLog: StateFlow<CycleLogEntity?> =
        cycleLogDao.getAllCycleLogs()
            .map { logs -> logs.firstOrNull() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    fun logTodayPhase(phase: String, flowIntensity: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cycleLogDao.insertCycleLog(
                CycleLogEntity(
                    date = System.currentTimeMillis(),
                    phase = phase,
                    flowIntensity = flowIntensity
                )
            )
        }
    }

    fun updateTrackingMode(mode: String) {
        val trimmedMode = mode.trim()
        if (trimmedMode !in setOf("Standard", "TTC", "Avoidance")) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            cycleSettingsDao.insertSettings(
                CycleSettingsEntity(
                    id = 1,
                    trackingMode = trimmedMode
                )
            )
        }
    }

    class Factory(
        private val cycleLogDao: CycleLogDao,
        private val cycleSettingsDao: CycleSettingsDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CycleViewModel::class.java)) {
                return CycleViewModel(
                    cycleLogDao = cycleLogDao,
                    cycleSettingsDao = cycleSettingsDao
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
