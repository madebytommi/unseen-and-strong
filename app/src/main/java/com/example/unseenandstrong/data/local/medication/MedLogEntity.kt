package com.example.unseenandstrong.data.local.medication

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "med_logs",
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
data class MedLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medId: Long,
    val scheduledTime: Long,
    val actualTakenTime: Long? = null,
    val status: String
) {
    init {
        require(status in setOf("Taken", "Skipped", "Delayed")) {
            "status must be Taken, Skipped, or Delayed"
        }
    }
}
