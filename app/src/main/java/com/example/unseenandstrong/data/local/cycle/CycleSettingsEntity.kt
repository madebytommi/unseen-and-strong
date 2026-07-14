package com.example.unseenandstrong.data.local.cycle

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cycle_settings")
data class CycleSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val trackingMode: String
) {
    init {
        require(trackingMode in setOf("Standard", "TTC", "Avoidance")) {
            "trackingMode must be one of: Standard, TTC, Avoidance"
        }
    }
}
