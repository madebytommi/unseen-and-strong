package com.example.unseenandstrong.ui.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unseenandstrong.data.local.medication.MedLogDao
import com.example.unseenandstrong.data.local.medication.MedLogEntity
import com.example.unseenandstrong.data.local.medication.MedicationDao
import com.example.unseenandstrong.data.local.medication.MedicationEntity
import com.example.unseenandstrong.data.local.medication.PRNLogDao
import com.example.unseenandstrong.data.local.medication.PRNLogEntity
import com.example.unseenandstrong.data.local.medication.ReactionDao
import com.example.unseenandstrong.data.local.medication.ReactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicationViewModel(
    private val medicationDao: MedicationDao,
    private val medLogDao: MedLogDao,
    private val prnLogDao: PRNLogDao,
    private val reactionDao: ReactionDao
) : ViewModel() {

    val activeDailyMedications: StateFlow<List<MedicationEntity>> =
        medicationDao.getActiveMedications()
            .map { medications -> medications.filter { !it.isPRN } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val activePRNMedications: StateFlow<List<MedicationEntity>> =
        medicationDao.getActiveMedications()
            .map { medications -> medications.filter { it.isPRN } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun addMedication(
        name: String,
        dosage: String,
        frequency: String,
        instructions: String,
        isPRN: Boolean,
        isActive: Boolean = true
    ) {
        val trimmedName = name.trim()
        val trimmedDosage = dosage.trim()
        val trimmedFrequency = frequency.trim()
        val trimmedInstructions = instructions.trim()

        if (
            trimmedName.isBlank() ||
            trimmedDosage.isBlank() ||
            trimmedFrequency.isBlank() ||
            trimmedInstructions.isBlank()
        ) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            medicationDao.insertMedication(
                MedicationEntity(
                    name = trimmedName,
                    dosage = trimmedDosage,
                    frequency = trimmedFrequency,
                    instructions = trimmedInstructions,
                    isPRN = isPRN,
                    isActive = isActive
                )
            )
        }
    }

    fun logDailyMedStatus(
        medId: Long,
        scheduledTime: Long,
        actualTakenTime: Long? = null,
        status: String
    ) {
        val trimmedStatus = status.trim()
        if (trimmedStatus !in setOf("Taken", "Skipped", "Delayed")) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            medLogDao.insertMedLog(
                MedLogEntity(
                    medId = medId,
                    scheduledTime = scheduledTime,
                    actualTakenTime = actualTakenTime,
                    status = trimmedStatus
                )
            )
        }
    }

    fun logPRNUsage(
        medId: Long,
        timeTaken: Long,
        reason: String,
        reliefDurationHours: Int,
        effectivenessRating: Int
    ) {
        val trimmedReason = reason.trim()
        if (trimmedReason.isBlank()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            prnLogDao.insertPRNLog(
                PRNLogEntity(
                    medId = medId,
                    timeTaken = timeTaken,
                    reason = trimmedReason,
                    reliefDurationHours = reliefDurationHours,
                    effectivenessRating = effectivenessRating
                )
            )
        }
    }

    fun logReaction(
        medId: Long,
        date: Long,
        description: String,
        severity: Int
    ) {
        val trimmedDescription = description.trim()
        if (trimmedDescription.isBlank()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            reactionDao.insertReaction(
                ReactionEntity(
                    medId = medId,
                    date = date,
                    description = trimmedDescription,
                    severity = severity
                )
            )
        }
    }

    class Factory(
        private val medicationDao: MedicationDao,
        private val medLogDao: MedLogDao,
        private val prnLogDao: PRNLogDao,
        private val reactionDao: ReactionDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MedicationViewModel::class.java)) {
                return MedicationViewModel(
                    medicationDao = medicationDao,
                    medLogDao = medLogDao,
                    prnLogDao = prnLogDao,
                    reactionDao = reactionDao
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
