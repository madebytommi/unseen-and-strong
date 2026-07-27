package com.example.unseenandstrong.ui.speakstrong

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.unseenandstrong.data.local.UnseenDatabase
import com.example.unseenandstrong.data.local.advocacy.AdvocacySessionEntity
import com.example.unseenandstrong.data.local.interaction.InteractionEntity
import com.example.unseenandstrong.data.local.script.ScriptEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdvocacyPreparationInput(
    val personName: String,
    val organization: String,
    val desiredOutcome: String,
    val smallGoal: String,
    val preparationNote: String,
    val mayNeedFollowUp: Boolean
)

data class AdvocacyReflectionInput(
    val conversationHappened: String,
    val outcomeSummary: String,
    val emotionalReflection: String,
    val goalResult: String,
    val needsFollowUp: Boolean,
    val followUpDate: Long?,
    val reflectionNote: String,
    val reflectionComplete: Boolean,
    val exportToInteractionLog: Boolean
)

@OptIn(ExperimentalCoroutinesApi::class)
class AdvocacySupportViewModel(
    private val database: UnseenDatabase
) : ViewModel() {
    private val sessionDao = database.advocacySessionDao()
    private val interactionDao = database.interactionDao()

    val sessions: StateFlow<List<AdvocacySessionEntity>> =
        sessionDao.observeAllSessions().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val selectedSessionId = MutableStateFlow<Long?>(null)

    val selectedSession: StateFlow<AdvocacySessionEntity?> =
        selectedSessionId.flatMapLatest { id ->
            if (id == null) flowOf(null) else sessionDao.observeSession(id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun selectSession(id: Long) {
        selectedSessionId.value = id
    }

    fun clearSelection() {
        selectedSessionId.value = null
    }

    fun beginPreparation(
        script: ScriptEntity,
        tone: SpeakStrongViewModel.Tone,
        scriptText: String,
        onCreated: () -> Unit
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val id = sessionDao.insertSession(
                AdvocacySessionEntity(
                    scriptId = script.id,
                    scriptTitle = script.title,
                    scriptCategory = script.category,
                    selectedTone = tone.name,
                    scriptTextSnapshot = scriptText,
                    createdAt = now,
                    updatedAt = now
                )
            )
            selectedSessionId.value = id
            onCreated()
        }
    }

    fun savePreparation(
        session: AdvocacySessionEntity,
        input: AdvocacyPreparationInput,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            sessionDao.updateSession(
                session.copy(
                    personName = input.personName.trim(),
                    organization = input.organization.trim(),
                    desiredOutcome = input.desiredOutcome.trim(),
                    smallGoal = input.smallGoal.trim(),
                    preparationNote = input.preparationNote.trim(),
                    mayNeedFollowUp = input.mayNeedFollowUp,
                    updatedAt = System.currentTimeMillis()
                )
            )
            onSaved()
        }
    }

    fun saveReflection(
        session: AdvocacySessionEntity,
        input: AdvocacyReflectionInput,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            database.withTransaction {
                val now = System.currentTimeMillis()
                val normalizedFollowUpDate = if (input.needsFollowUp) input.followUpDate else null
                val exportAction = AdvocacyExportPolicy.decide(
                    exportRequested = input.exportToInteractionLog,
                    linkedInteractionId = session.linkedInteractionId
                )
                var linkedInteractionId = session.linkedInteractionId

                if (exportAction != InteractionExportAction.NONE) {
                    val interaction = buildInteraction(
                        session = session,
                        input = input,
                        timestamp = now,
                        followUpDate = normalizedFollowUpDate
                    )

                    linkedInteractionId = when (exportAction) {
                        InteractionExportAction.CREATE -> interactionDao.insertInteraction(interaction)
                        InteractionExportAction.UPDATE -> {
                            val existing = session.linkedInteractionId?.let {
                                interactionDao.getInteractionById(it)
                            }
                            if (existing == null) {
                                interactionDao.insertInteraction(interaction)
                            } else {
                                interactionDao.updateInteraction(
                                    interaction.copy(
                                        id = existing.id,
                                        timestamp = existing.timestamp
                                    )
                                )
                                existing.id
                            }
                        }
                        InteractionExportAction.NONE -> linkedInteractionId
                    }
                }

                sessionDao.updateSession(
                    session.copy(
                        conversationHappened = input.conversationHappened,
                        outcomeSummary = input.outcomeSummary.trim(),
                        emotionalReflection = input.emotionalReflection.trim(),
                        goalResult = input.goalResult,
                        needsFollowUp = input.needsFollowUp,
                        followUpDate = normalizedFollowUpDate,
                        reflectionNote = input.reflectionNote.trim(),
                        reflectionComplete = input.reflectionComplete,
                        updatedAt = now,
                        linkedInteractionId = linkedInteractionId
                    )
                )
            }
            onSaved()
        }
    }

    private fun buildInteraction(
        session: AdvocacySessionEntity,
        input: AdvocacyReflectionInput,
        timestamp: Long,
        followUpDate: Long?
    ): InteractionEntity {
        val personName = session.personName.ifBlank {
            session.organization.ifBlank { "Advocacy conversation" }
        }
        val notes = buildList {
            if (input.outcomeSummary.isNotBlank()) add("Outcome: ${input.outcomeSummary.trim()}")
            if (input.goalResult.isNotBlank()) add("Goal result: ${input.goalResult}")
            if (input.emotionalReflection.isNotBlank()) {
                add("Reflection: ${input.emotionalReflection.trim()}")
            }
            if (input.reflectionNote.isNotBlank()) add(input.reflectionNote.trim())
        }.joinToString("\n\n")

        return InteractionEntity(
            timestamp = timestamp,
            needsFollowUp = input.needsFollowUp,
            followUpDate = followUpDate,
            category = session.scriptCategory,
            personName = personName,
            organization = session.organization,
            notes = notes
        )
    }

    class Factory(
        private val database: UnseenDatabase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdvocacySupportViewModel::class.java)) {
                return AdvocacySupportViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
