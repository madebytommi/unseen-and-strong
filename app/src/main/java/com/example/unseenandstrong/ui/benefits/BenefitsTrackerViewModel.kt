package com.example.unseenandstrong.ui.benefits

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unseenandstrong.data.local.UnseenDatabase
import com.example.unseenandstrong.data.local.benefits.BenefitsStageEntity
import com.example.unseenandstrong.reminder.FollowUpReminderCoordinator
import com.example.unseenandstrong.reminder.NoOpFollowUpReminderCoordinator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BenefitsTrackerViewModel(
    application: Application,
    private val reminderCoordinator: FollowUpReminderCoordinator = NoOpFollowUpReminderCoordinator
) : AndroidViewModel(application) {
    private val dao = UnseenDatabase.getDatabase(application).benefitsStageDao()

    val stages: StateFlow<List<BenefitsStageEntity>> = dao.getAllStages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateStage(stage: BenefitsStageEntity) {
        viewModelScope.launch {
            dao.updateStage(stage)
            reminderCoordinator.syncBenefitsDeadline(stage)
        }
    }

    class Factory(
        private val application: Application,
        private val reminderCoordinator: FollowUpReminderCoordinator
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BenefitsTrackerViewModel::class.java)) {
                return BenefitsTrackerViewModel(application, reminderCoordinator) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
