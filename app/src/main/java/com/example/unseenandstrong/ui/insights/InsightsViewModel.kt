package com.example.unseenandstrong.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unseenandstrong.data.local.checkin.DailyCheckInDao
import com.example.unseenandstrong.data.local.checkin.DailyCheckInEntity
import com.example.unseenandstrong.data.local.cycle.CycleLogDao
import com.example.unseenandstrong.data.local.cycle.CycleLogEntity
import com.example.unseenandstrong.data.local.medication.MedLogDao
import com.example.unseenandstrong.data.local.medication.MedLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InsightsViewModel(
    private val dailyCheckInDao: DailyCheckInDao,
    private val medLogDao: MedLogDao,
    private val cycleLogDao: CycleLogDao
) : ViewModel() {

    private val _last7DaysDailyCheckIns = MutableStateFlow<List<DailyCheckInEntity>>(emptyList())
    val last7DaysDailyCheckIns: StateFlow<List<DailyCheckInEntity>> = _last7DaysDailyCheckIns.asStateFlow()

    private val _medicationAdherencePercentage = MutableStateFlow(0f)
    val medicationAdherencePercentage: StateFlow<Float> = _medicationAdherencePercentage.asStateFlow()

    private val _last7DaysCycleLogs = MutableStateFlow<List<CycleLogEntity>>(emptyList())
    val last7DaysCycleLogs: StateFlow<List<CycleLogEntity>> = _last7DaysCycleLogs.asStateFlow()

    init {
        observeDailyCheckIns()
        observeMedicationAdherence()
        observeCycleLogs()
    }

    private fun observeDailyCheckIns() {
        viewModelScope.launch(Dispatchers.IO) {
            dailyCheckInDao.getAllCheckIns().collect { checkIns ->
                val startDate = LocalDate.now().minusDays(6).format(DateTimeFormatter.ISO_DATE)
                _last7DaysDailyCheckIns.value = checkIns.filter { it.date >= startDate }
            }
        }
    }

    private fun observeMedicationAdherence() {
        viewModelScope.launch(Dispatchers.IO) {
            medLogDao.getAllMedLogs().collect { logs ->
                val cutoff = System.currentTimeMillis() - SEVEN_DAYS_MILLIS
                val recentLogs = logs.filter { it.scheduledTime >= cutoff }
                _medicationAdherencePercentage.value = calculateAdherence(recentLogs)
            }
        }
    }

    private fun observeCycleLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            cycleLogDao.getAllCycleLogs().collect { logs ->
                val cutoff = System.currentTimeMillis() - SEVEN_DAYS_MILLIS
                _last7DaysCycleLogs.value = logs.filter { it.date >= cutoff }
            }
        }
    }

    private fun calculateAdherence(logs: List<MedLogEntity>): Float {
        if (logs.isEmpty()) return 0f
        val takenCount = logs.count { it.status == "Taken" }
        return (takenCount.toFloat() / logs.size.toFloat()) * 100f
    }

    class Factory(
        private val dailyCheckInDao: DailyCheckInDao,
        private val medLogDao: MedLogDao,
        private val cycleLogDao: CycleLogDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InsightsViewModel::class.java)) {
                return InsightsViewModel(
                    dailyCheckInDao = dailyCheckInDao,
                    medLogDao = medLogDao,
                    cycleLogDao = cycleLogDao
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    private companion object {
        const val SEVEN_DAYS_MILLIS = 7L * 24L * 60L * 60L * 1000L
    }
}
