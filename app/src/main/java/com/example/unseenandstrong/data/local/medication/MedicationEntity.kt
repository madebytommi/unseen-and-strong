package com.example.unseenandstrong.data.local.medication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dosage: String,
    val frequency: String,
    val instructions: String,
    val isPRN: Boolean = false,
    val isActive: Boolean = true
)
