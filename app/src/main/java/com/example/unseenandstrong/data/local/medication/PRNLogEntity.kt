package com.example.unseenandstrong.data.local.medication

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prn_logs",
    foreignKeys = [
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["medId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["medId"])]
)
data class PRNLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medId: Long,
    val timeTaken: Long,
    val reason: String,
    val reliefDurationHours: Int,
    val effectivenessRating: Int
) {
    init {
        require(effectivenessRating in 1..5) { "effectivenessRating must be between 1 and 5" }
    }
}
