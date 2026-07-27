package com.example.unseenandstrong.data.local.advocacy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "advocacy_sessions")
data class AdvocacySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scriptId: Long?,
    val scriptTitle: String,
    val scriptCategory: String,
    val selectedTone: String,
    val scriptTextSnapshot: String,
    val personName: String = "",
    val organization: String = "",
    val desiredOutcome: String = "",
    val smallGoal: String = "",
    val preparationNote: String = "",
    val mayNeedFollowUp: Boolean = false,
    val createdAt: Long,
    val conversationHappened: String = "",
    val outcomeSummary: String = "",
    val emotionalReflection: String = "",
    val goalResult: String = "",
    val needsFollowUp: Boolean = false,
    val followUpDate: Long? = null,
    val reflectionNote: String = "",
    val reflectionComplete: Boolean = false,
    val updatedAt: Long,
    val linkedInteractionId: Long? = null
)
