package com.example.unseenandstrong.data.local.claims

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "disability_claim_tasks",
    foreignKeys = [
        ForeignKey(
            entity = DisabilityClaimEntity::class,
            parentColumns = ["id"],
            childColumns = ["claimId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("claimId")
    ]
)
data class DisabilityClaimTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val claimId: Long,
    val category: String,
    val title: String,
    val status: String = "Not started",
    val dueDate: Long? = null,
    val completedDate: Long? = null,
    val notes: String = "",
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
