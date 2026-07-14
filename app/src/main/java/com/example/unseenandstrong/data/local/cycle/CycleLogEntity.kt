package com.example.unseenandstrong.data.local.cycle

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cycle_logs")
data class CycleLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val phase: String,
    val flowIntensity: String
) {
    init {
        require(phase in setOf("Rest & Restore", "Emerging", "Bloom", "Retreat")) {
            "phase must be one of: Rest & Restore, Emerging, Bloom, Retreat"
        }
        require(flowIntensity in setOf("Light", "Medium", "Heavy", "None")) {
            "flowIntensity must be one of: Light, Medium, Heavy, None"
        }
    }
}
