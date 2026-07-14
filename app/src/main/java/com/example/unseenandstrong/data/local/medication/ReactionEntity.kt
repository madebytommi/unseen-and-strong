package com.example.unseenandstrong.data.local.medication

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reactions",
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
data class ReactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medId: Long,
    val date: Long,
    val description: String,
    val severity: Int
) {
    init {
        require(severity in 1..5) { "severity must be between 1 and 5" }
    }
}
