package com.example.unseenandstrong.data.local.claims

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disability_claims")
data class DisabilityClaimEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val claimType: String, // "STD" or "LTD"
    val employerName: String = "",
    val administratorName: String = "",
    val claimNumber: String = "",
    val status: String = "Preparing",
    val filedDate: Long? = null,
    val leaveStartDate: Long? = null,
    val leaveEndDate: Long? = null,
    val benefitStartDate: Long? = null,
    val benefitEndDate: Long? = null,
    val decisionDate: Long? = null,
    val appealDeadline: Long? = null,
    val nextAction: String = "",
    val nextActionDueDate: Long? = null,
    val notes: String = "",
    val linkedRequestId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
